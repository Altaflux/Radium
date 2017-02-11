package com.kubadziworski.resolver;


import com.kubadziworski.resolver.descriptor.DeclarationDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResolverContainer implements ClassPathResolver {

    private final List<ClassPathResolver> resolvers;

    public ResolverContainer(List<ClassPathResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Optional<DeclarationDescriptor> preParseClassDeclarations(String importPackage) {
        return resolvers.stream().map(classPathResolver -> classPathResolver.preParseClassDeclarations(importPackage))
                .filter(Optional::isPresent)
                .findFirst().orElse(Optional.empty());
    }

    @Override
    public List<DeclarationDescriptor> extractFieldOrMethods(String importPackage) {
        return resolvers.stream().map(classPathResolver -> classPathResolver.extractFieldOrMethods(importPackage))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<DeclarationDescriptor>> getMethodsOrFields(String importPackage) {
        return resolvers.stream().map(classPathResolver -> classPathResolver.getMethodsOrFields(importPackage))
                .filter(Optional::isPresent)
                .findFirst().orElse(Optional.empty());
    }

    @Override
    public List<DeclarationDescriptor> extractClassesFromPackage(String importPackage) {
        return resolvers.stream().map(classPathResolver -> classPathResolver.extractClassesFromPackage(importPackage))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
