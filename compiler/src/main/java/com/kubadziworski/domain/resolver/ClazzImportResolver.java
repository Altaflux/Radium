package com.kubadziworski.domain.resolver;

import com.kubadziworski.domain.scope.ClassPathScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ClazzImportResolver implements BaseImportResolver {

    private static final ClassPathScope classPathScope = new ClassPathScope();

    private static final List<URL> bootClassPath;

    static {
        List<URL> bootList = Arrays.stream(ManagementFactory.getRuntimeMXBean()
                .getBootClassPath().split(java.io.File.pathSeparator))
                .map(ClazzImportResolver::pathToUrl).filter(url -> url != null).collect(Collectors.toList());
        List<URL> systemPathList = Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
                .map(ClazzImportResolver::pathToUrl).filter(url -> url != null).collect(Collectors.toList());

        bootClassPath = ListUtils.sum(systemPathList,
                ListUtils.sum(bootList, ClasspathHelper.forClassLoader().stream().collect(Collectors.toList())));
    }

    private static final Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setScanners(new ResourcesScanner())
            .forPackages("")
            .setUrls(bootClassPath));

    @Override
    public Optional<DeclarationDescriptor> preParseClassDeclarations(String importPackage) {
        Class clazz;
        if ((clazz = getClazz(importPackage)) != null) {
            return Optional.of(new ClassDescriptor(ClassUtils.getSimpleName(clazz), ClassUtils.getPackageName(clazz)));
        }
        return Optional.empty();
    }

    @Override
    public List<DeclarationDescriptor> extractFieldOrMethods(String importPackage) {
        return findSpecificMethodOrField(importPackage);
    }

    @Override
    public List<DeclarationDescriptor> extractClassesFromPackage(String importPackage) {

        return reflections.getResources(Pattern.compile("[^/]*.class")).stream()
                .filter(s -> s.matches(importPackage.replace(".", "/") + "/" + "[^/]*.class"))
                .map(clazzName -> clazzName.replace("/", ".").replace(".class", ""))
                .map(ClazzImportResolver::getClazz)
                .filter(aClass -> aClass != null)
                .map(aClass -> new ClassDescriptor(ClassUtils.getSimpleName(aClass), ClassUtils.getPackageName(aClass)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<DeclarationDescriptor>> getMethodsOrFields(String importPackage) {
        Class clazz = getClazz(importPackage);
        if (clazz != null) {
            List<DeclarationDescriptor> descriptors = new ArrayList<>();
            descriptors.addAll(getFieldsFromClass(clazz, null));
            descriptors.addAll(getFunctionDescriptorFromClass(clazz, null));
            return Optional.of(descriptors);
        }
        return Optional.empty();
    }


    private List<DeclarationDescriptor> findSpecificMethodOrField(String importPackage) {

        if (StringUtils.isEmpty(importPackage)) {
            return Collections.emptyList();
        }

        List<DeclarationDescriptor> descriptors = new ArrayList<>();
        ClassEntity entity = splitDeclaration(importPackage);
        Class clazz;
        if ((clazz = getClazz(entity.packageName)) != null) {
            List<DeclarationDescriptor> fields = getFieldsFromClass(clazz, entity.clazzName);
            if (fields.isEmpty()) {
                fields.addAll(getFunctionDescriptorFromClass(clazz, entity.clazzName));
            }
            return descriptors;
        } else {
            if (!entity.packageName.contains(".")) {
                return Collections.emptyList();
            }
            return findSpecificMethodOrField(entity.packageName);
        }
    }

    private List<FunctionDescriptor> getFunctionDescriptorFromClass(Class clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        List<FunctionDescriptor> functionDescriptors = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && !Modifier.isPrivate(method.getModifiers())) {
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


    private static Class getClazz(String clazz) {
        try {
            return Class.forName(clazz, false, ClazzImportResolver.class.getClassLoader());
        } catch (Exception e) {
            return null;
        }
    }

    private static List<DeclarationDescriptor> getFieldsFromClass(Class clazz, String fieldName) {
        List<DeclarationDescriptor> propertyDescriptors = new ArrayList<>();


        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers())) {
                if (fieldName != null) {
                    if (!field.getName().equals(fieldName)) {
                        return propertyDescriptors;
                    }
                }
                classPathScope.getFieldSignature((JavaClassType) ClassTypeFactory.createClassType(clazz.getName()), field.getName()).ifPresent(field1 -> {
                    PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), field1);
                    propertyDescriptors.add(descriptor);
                });
            }
        }

        return propertyDescriptors;
    }

    private static URL pathToUrl(String path) {
        try {
            File jar = new File(path);
            if (jar.exists()) {
                return jar.toURI().toURL();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
