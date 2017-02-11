package com.kubadziworski.resolver;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.BadImportException;
import com.kubadziworski.resolver.descriptor.ClassDescriptor;
import com.kubadziworski.resolver.descriptor.DeclarationDescriptor;
import com.kubadziworski.resolver.descriptor.FunctionDescriptor;
import com.kubadziworski.resolver.descriptor.PropertyDescriptor;
import com.kubadziworski.util.TypeResolver;

import java.util.*;
import java.util.stream.Collectors;


public class ImportResolver {

    private final List<ImportDeclaration> importDeclarationContexts;
    private final HashSet<DeclarationDescriptor> declarationDescriptors = new HashSet<>();
    private final ResolverContainer resolverContainer;

    public ImportResolver(List<EnkelParser.ImportDeclarationContext> importDeclarationContexts, ResolverContainer resolverContainer) {
        this.importDeclarationContexts = new ArrayList<>(convertToImportDeclarations(importDeclarationContexts));
        this.resolverContainer = resolverContainer;
    }

    public void loadClassImports() {
        List<DeclarationDescriptor> imports = new ArrayList<>();
        List<ImportDeclaration> missingDeclarations = new ArrayList<>();
        for (ImportDeclaration importDeclarationContext : importDeclarationContexts) {
            String importPackage = importDeclarationContext.importDeclaration;
            if (!importDeclarationContext.isOnDemand) {
                Optional<DeclarationDescriptor> descriptors = resolverContainer.preParseClassDeclarations(importPackage);

                if (descriptors.isPresent()) {
                    imports.add(descriptors.get());
                } else {
                    missingDeclarations.add(importDeclarationContext);
                }
            } else {
                List<DeclarationDescriptor> descriptors = resolverContainer.extractClassesFromPackage(importPackage);
                imports.addAll(descriptors);
                if (descriptors.isEmpty()) {
                    missingDeclarations.add(importDeclarationContext);
                }
            }
        }
        imports.addAll(resolverContainer.extractClassesFromPackage("radium"));
        imports.addAll(resolverContainer.extractClassesFromPackage("java.lang"));
        imports.addAll(doOnDemandImport("radium.io.Console"));

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
        return resolverContainer.getMethodsOrFields(importPackage).orElse(Collections.emptyList());
    }

    private List<DeclarationDescriptor> doSingleTypeImport(String originalImportString) {
        return resolverContainer.extractFieldOrMethods(originalImportString);
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

    public Optional<FunctionSignature> getMethod(String methodName, List<ArgumentHolder> arguments) {
        Map<Integer, List<FunctionSignature>> descs = declarationDescriptors.stream()
                .filter(declarationDescriptor -> declarationDescriptor instanceof FunctionDescriptor)
                .filter(declarationDescriptor -> declarationDescriptor.getName().equals(methodName))
                .map(declarationDescriptor -> ((FunctionDescriptor) declarationDescriptor).getFunctionSignature())
                .collect(Collectors.groupingBy(o -> o.matches(methodName, arguments)));

        return TypeResolver.resolveArity(null, descs);
    }

    public Optional<Type> getClass(String clazzName) {
        return getClassInternal(clazzName).map(ClassDescriptor::getType);
    }

    private Optional<ClassDescriptor> getClassInternal(String clazzName) {
        Optional<DeclarationDescriptor> descriptor = declarationDescriptors.stream()
                .filter(declarationDescriptor -> declarationDescriptor instanceof ClassDescriptor)
                .filter(declarationDescriptor -> declarationDescriptor.getName().equals(clazzName))
                .findAny();

        if (descriptor.isPresent()) {
            return Optional.of(((ClassDescriptor) descriptor.get()));
        }
        return Optional.empty();
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
