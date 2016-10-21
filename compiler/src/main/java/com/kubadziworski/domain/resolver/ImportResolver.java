package com.kubadziworski.domain.resolver;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.exception.BadImportException;

import java.util.*;
import java.util.stream.Collectors;


public class ImportResolver {

    private final List<EnkelParser.ImportDeclarationContext> importDeclarationContexts;
    private final HashSet<DeclarationDescriptor> declarationDescriptors = new HashSet<>();
    private final ClazzImportResolver clazzImportResolver;
    private final EnkelImportResolver enkelImportResolver;
    private final GlobalScope globalScope;

    public ImportResolver(List<EnkelParser.ImportDeclarationContext> importDeclarationContexts, GlobalScope globalScope) {

        this.importDeclarationContexts = importDeclarationContexts;
        this.clazzImportResolver = new ClazzImportResolver();
        this.enkelImportResolver = new EnkelImportResolver(globalScope);
        this.globalScope = globalScope;

    }

    public void doPreClassParse(){
        List<DeclarationDescriptor> imports = new ArrayList<>();
        List<DeclarationDescriptor> classImports = importDeclarationContexts.stream().map(importDeclarationContext -> {
            if (importDeclarationContext.singleTypeImportDeclaration() != null) {
                return enkelImportResolver.preParseClassDeclarations(importDeclarationContext.singleTypeImportDeclaration().typeName().getText());
            }

            if (importDeclarationContext.typeImportOnDemandDeclaration() != null) {
                String importPackage = importDeclarationContext.typeImportOnDemandDeclaration().packageOrTypeName().getText();
                return enkelImportResolver.extractClassesFromPackage(importPackage);
            }
            return null;
        }).filter(stringStringMap -> stringStringMap != null)
                .flatMap(Collection::stream).collect(Collectors.toList());

        imports.addAll(classImports);
        declarationDescriptors.addAll(imports);
    }

    public void parseImports() {
        List<DeclarationDescriptor> imports = new ArrayList<>();
        imports.addAll(doOnDemandImport("java.lang"));
        List<DeclarationDescriptor> classImports = importDeclarationContexts.stream().map(importDeclarationContext -> {
            if (importDeclarationContext.singleTypeImportDeclaration() != null) {
                return doSingleTypeImport(importDeclarationContext.singleTypeImportDeclaration().typeName().getText());
            }

            if (importDeclarationContext.typeImportOnDemandDeclaration() != null) {
                String importPackage = importDeclarationContext.typeImportOnDemandDeclaration().packageOrTypeName().getText();
                return doOnDemandImport(importPackage);
            }
            return null;
        }).filter(stringStringMap -> stringStringMap != null)
                .flatMap(Collection::stream).collect(Collectors.toList());

        imports.addAll(classImports);
        declarationDescriptors.addAll(imports);
    }

    private List<DeclarationDescriptor> doOnDemandImport(String importPackage) {


        List<DeclarationDescriptor> descriptors = new ArrayList<>();
        Optional<List<DeclarationDescriptor>> enkelDescriptorList = enkelImportResolver.getMethodsOrFields(importPackage);
        if (enkelDescriptorList.isPresent()) {
            return enkelDescriptorList.get();
        }

        Optional<List<DeclarationDescriptor>> descriptorList = clazzImportResolver.getMethodsOrFields(importPackage);
        if (descriptorList.isPresent()) {
            return descriptorList.get();
        }

        descriptors.addAll(enkelImportResolver.extractClassesFromPackage(importPackage));
        descriptors.addAll(clazzImportResolver.extractClassesFromPackage(importPackage));

        return descriptors;
    }

    private List<DeclarationDescriptor> doSingleTypeImport(String originalImportString) {
        List<DeclarationDescriptor> descriptors = new ArrayList<>();

        descriptors.addAll(enkelImportResolver.extractClazzFieldOrMethods(originalImportString));
        descriptors.addAll(clazzImportResolver.extractClazzFieldOrMethods(originalImportString));

        if (descriptors.isEmpty()) {
            throw new BadImportException(originalImportString);
        }
        return descriptors;
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

    public GlobalScope getGlobalScope() {
        return globalScope;
    }
}
