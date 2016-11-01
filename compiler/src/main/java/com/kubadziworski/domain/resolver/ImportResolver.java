package com.kubadziworski.domain.resolver;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.BadImportException;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.stream.Collectors;


public class ImportResolver {

    private final List<ImportDeclaration> importDeclarationContexts;
    private final HashSet<DeclarationDescriptor> declarationDescriptors = new HashSet<>();
    private final ClazzImportResolver clazzImportResolver;
    private final EnkelImportResolver enkelImportResolver;
    private final GlobalScope globalScope;

    public ImportResolver(List<EnkelParser.ImportDeclarationContext> importDeclarationContexts, GlobalScope globalScope) {

        this.importDeclarationContexts = new ArrayList<>(convertToImportDeclarations(importDeclarationContexts));
        this.clazzImportResolver = new ClazzImportResolver();
        this.enkelImportResolver = new EnkelImportResolver(globalScope);
        this.globalScope = globalScope;

    }

    public void loadClassImports() {
        List<DeclarationDescriptor> imports = new ArrayList<>();
        List<ImportDeclaration> missingDeclarations = new ArrayList<>();
        for (ImportDeclaration importDeclarationContext : importDeclarationContexts) {
            String importPackage = importDeclarationContext.importDeclaration;
            if (!importDeclarationContext.isOnDemand) {
                Optional<DeclarationDescriptor> descriptors =
                        enkelImportResolver.preParseClassDeclarations(importPackage)
                                .map(Optional::of).orElse(clazzImportResolver.preParseClassDeclarations(importPackage));

                if (descriptors.isPresent()) {
                    imports.add(descriptors.get());
                } else {
                    missingDeclarations.add(importDeclarationContext);
                }
            } else {
                List<DeclarationDescriptor> descriptors = ListUtils.sum(clazzImportResolver.extractClassesFromPackage(importPackage),
                        enkelImportResolver.extractClassesFromPackage(importPackage));
                imports.addAll(descriptors);
                if (descriptors.isEmpty()) {
                    missingDeclarations.add(importDeclarationContext);
                }
            }
        }
        imports.addAll(clazzImportResolver.extractClassesFromPackage("java.lang"));
        declarationDescriptors.addAll(imports);
        importDeclarationContexts.clear();
        importDeclarationContexts.addAll(missingDeclarations);
    }

    public void loadMethodsAndFieldsImports() {
        List<DeclarationDescriptor> imports = new ArrayList<>();
        List<ImportDeclaration> missingDeclarations = new ArrayList<>();
        for (ImportDeclaration importDeclarationContext : importDeclarationContexts) {
            String importPackage = importDeclarationContext.importDeclaration;
            List<DeclarationDescriptor> descriptors;

            if (!importDeclarationContext.isOnDemand) {
                descriptors = doSingleTypeImport(importPackage);
            } else {
                descriptors = doOnDemandImport(importPackage);
            }

            imports.addAll(descriptors);
            if (descriptors.isEmpty()) {
                missingDeclarations.add(importDeclarationContext);
            }
        }

        declarationDescriptors.addAll(imports);
        if (!missingDeclarations.isEmpty()) {
            throw new BadImportException(missingDeclarations.stream()
                    .map(importDeclaration -> importDeclaration.importDeclaration)
                    .collect(Collectors.toList()));
        }

    }

    private List<DeclarationDescriptor> doOnDemandImport(String importPackage) {
        return enkelImportResolver.getMethodsOrFields(importPackage).map(Optional::of)
                .orElse(clazzImportResolver.getMethodsOrFields(importPackage))
                .orElse(Collections.emptyList());
    }

    private List<DeclarationDescriptor> doSingleTypeImport(String originalImportString) {
        List<DeclarationDescriptor> descriptors = new ArrayList<>();

        descriptors.addAll(enkelImportResolver.extractFieldOrMethods(originalImportString));
        descriptors.addAll(clazzImportResolver.extractFieldOrMethods(originalImportString));

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

    public Optional<Type> getClass(String clazzName) {
        return getClassInternal(clazzName).map(ClassDescriptor::getType);
    }

    public Optional<ClassDescriptor> getClassInternal(String clazzName) {
        Optional<DeclarationDescriptor> descriptor = declarationDescriptors.stream()
                .filter(declarationDescriptor -> declarationDescriptor instanceof ClassDescriptor)
                .filter(declarationDescriptor -> declarationDescriptor.getName().equals(clazzName))
                .findAny();

        if (descriptor.isPresent()) {
            return Optional.of(((ClassDescriptor) descriptor.get()));
        }
        return Optional.empty();
    }

    public Optional<String> getClassName(String clazzName) {
        return getClassInternal(clazzName).map(ClassDescriptor::getFullClassName);
    }

    public GlobalScope getGlobalScope() {
        return globalScope;
    }

    private static List<ImportDeclaration> convertToImportDeclarations(List<EnkelParser.ImportDeclarationContext> importDeclarationContexts) {
        return importDeclarationContexts.stream()
                .map(importDeclarationContext -> {
                    if (importDeclarationContext.singleTypeImportDeclaration() != null) {
                        return new ImportDeclaration(importDeclarationContext.singleTypeImportDeclaration().typeName().getText(),
                                false);
                    } else {
                        return new ImportDeclaration(importDeclarationContext.typeImportOnDemandDeclaration().packageOrTypeName().getText(),
                                true);
                    }
                })
                .collect(Collectors.toList());
    }

    private static class ImportDeclaration {
        private final String importDeclaration;
        private final boolean isOnDemand;

        private ImportDeclaration(String importDeclaration, boolean isOnDemand) {
            this.importDeclaration = importDeclaration;
            this.isOnDemand = isOnDemand;
        }
    }
}
