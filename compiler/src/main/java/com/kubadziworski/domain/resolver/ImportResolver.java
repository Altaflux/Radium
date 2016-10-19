package com.kubadziworski.domain.resolver;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.scope.ClassPathScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.exception.BadImportException;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ImportResolver {

    private final List<EnkelParser.ImportDeclarationContext> importDeclarationContexts;
    private final ClassPathScope classPathScope = new ClassPathScope();
    private final List<DeclarationDescriptor> declarationDescriptors;

    private static Map<String, Reflections> packageCache = new ConcurrentHashMap<>();

    public ImportResolver(List<EnkelParser.ImportDeclarationContext> importDeclarationContexts) {

        this.importDeclarationContexts = importDeclarationContexts;
        declarationDescriptors = parseImports();
    }

    private List<DeclarationDescriptor> parseImports() {
        List<DeclarationDescriptor> imports = new ArrayList<>();
        imports.addAll(extractClasses("java.lang"));
        List<DeclarationDescriptor> classImports = importDeclarationContexts.stream().map(importDeclarationContext -> {
            if (importDeclarationContext.singleTypeImportDeclaration() != null) {
                return determineType(importDeclarationContext.singleTypeImportDeclaration().typeName().getText());
            }

            if (importDeclarationContext.typeImportOnDemandDeclaration() != null) {
                String importPackage = importDeclarationContext.typeImportOnDemandDeclaration().packageOrTypeName().getText();
                return extractClasses(importPackage);
            }
            return null;
        }).filter(stringStringMap -> stringStringMap != null)
                .flatMap(Collection::stream).collect(Collectors.toList());

        imports.addAll(classImports);
        return imports;
    }

    private List<DeclarationDescriptor> extractClasses(String importPackage) {
        List<DeclarationDescriptor> descriptors = new ArrayList<>();
        try {
            Class<?> clazz = Class.forName(importPackage);
            for(Field field : clazz.getFields()){
                if(Modifier.isStatic(field.getModifiers())){
                    classPathScope.getFieldSignature(new ClassType(clazz.getName()), field.getName()).ifPresent(field1 -> {
                        PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), field1);
                        descriptors.add(descriptor);
                    });
                }
            }

            descriptors.addAll(getFunctionDescriptorFromClass(clazz, null));
        } catch (Exception e) {
            ResourcesScanner resourcesScanner = new ResourcesScanner();
            Reflections reflections = packageCache.get(importPackage);
            if (reflections == null) {
                reflections = new Reflections(new ConfigurationBuilder()
                        .setScanners(resourcesScanner)
                        .forPackages(importPackage)
                        .setUrls(ClasspathHelper.forClassLoader()));
                packageCache.put(importPackage, reflections);
            }

            List<DeclarationDescriptor> clazzList = reflections.getResources(Pattern.compile("[^/]*.class")).stream()
                    .filter(s -> s.matches(importPackage.replace(".", "/") + "/" + "[^/]*.class"))
                    .map(x -> {
                        try {
                            String clazz = x.replace("/", ".").replace(".class", "");
                            return Class.forName(clazz);
                        } catch (Exception ex) {
                            return null;
                        }
                    }).filter(aClass -> aClass != null)
                    .map(aClass -> new ClassDescriptor(ClassUtils.getSimpleName(aClass), ClassUtils.getPackageName(aClass)))
                    .collect(Collectors.toList());
            descriptors.addAll(clazzList);
        }


        return descriptors;
    }

    private List<DeclarationDescriptor> determineType(String originalImportString) {

        List<DeclarationDescriptor> descriptors = new ArrayList<>();
        String importString = originalImportString;

        try {
            Class<?> clazz = Class.forName(importString);
            descriptors.add(new ClassDescriptor(ClassUtils.getSimpleName(clazz), ClassUtils.getPackageName(clazz)));
            return descriptors;

        } catch (ClassNotFoundException e) {
            String methodOrField = originalImportString.substring(importString.lastIndexOf('.'));
            importString = originalImportString.substring(0, originalImportString.lastIndexOf('.'));
            try {
                Class<?> clazz = Class.forName(importString);
                Field field = FieldUtils.getField(clazz, methodOrField);
                if (field != null && Modifier.isStatic(field.getModifiers())) {
                    classPathScope.getFieldSignature(new ClassType(clazz.getName()), field.getName()).ifPresent(field1 -> {
                        PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), field1);
                        descriptors.add(descriptor);
                    });
                } else {
                    descriptors.addAll(getFunctionDescriptorFromClass(clazz, methodOrField));
                }

            } catch (ClassNotFoundException e1) {
                //
            }
        }
        if (descriptors.isEmpty()) {
            throw new BadImportException(originalImportString);
        }
        return descriptors;
    }


    private List<FunctionDescriptor> getFunctionDescriptorFromClass(Class clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        List<FunctionDescriptor> functionDescriptors = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                if (methodName == null) {
                    FunctionSignature signature = ReflectionObjectToSignatureMapper.fromMethod(method);
                    FunctionDescriptor functionDescriptor = new FunctionDescriptor(method.getName(), signature);
                    functionDescriptors.add(functionDescriptor);
                } else {
                    if (methodName.equals(method.getName())) {
                        FunctionSignature signature = ReflectionObjectToSignatureMapper.fromMethod(method);
                        FunctionDescriptor functionDescriptor = new FunctionDescriptor(method.getName(), signature);
                        functionDescriptors.add(functionDescriptor);
                    }
                }
            }
        }
        return functionDescriptors;
    }

    public Optional<com.kubadziworski.domain.scope.Field> getField(String fieldName) {
        Optional<DeclarationDescriptor> descriptor = declarationDescriptors.stream()
                .filter(declarationDescriptor -> declarationDescriptor instanceof PropertyDescriptor)
                .filter(declarationDescriptor -> declarationDescriptor.getName().equals(fieldName))
                .findAny();

        if (descriptor.isPresent()) {
            return Optional.of(((PropertyDescriptor) descriptor.get()).getField());
        }
        return Optional.empty();
    }

    public Optional<FunctionSignature> getMethod(String methodName, List<Argument> arguments) {
        Optional<DeclarationDescriptor> descriptor = declarationDescriptors.stream()
                .filter(declarationDescriptor -> declarationDescriptor instanceof FunctionDescriptor)
                .filter(declarationDescriptor -> declarationDescriptor.getName().equals(methodName))
                .filter(declarationDescriptor -> ((FunctionDescriptor) declarationDescriptor).getFunctionSignature().matches(methodName, arguments))
                .findAny();

        if (descriptor.isPresent()) {
            return Optional.of(((FunctionDescriptor) descriptor.get()).getFunctionSignature());
        }
        return Optional.empty();
    }

    public Optional<ClassType> getClass(String clazzName) {
        Optional<DeclarationDescriptor> descriptor = declarationDescriptors.stream()
                .filter(declarationDescriptor -> declarationDescriptor instanceof ClassDescriptor)
                .filter(declarationDescriptor -> declarationDescriptor.getName().equals(clazzName))
                .findAny();

        if (descriptor.isPresent()) {
            return Optional.of(((ClassDescriptor) descriptor.get()).getType());
        }
        return Optional.empty();
    }


}
