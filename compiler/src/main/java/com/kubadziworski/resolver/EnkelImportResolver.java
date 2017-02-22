package com.kubadziworski.resolver;


import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.resolver.descriptor.ClassDescriptor;
import com.kubadziworski.resolver.descriptor.DeclarationDescriptor;
import com.kubadziworski.resolver.descriptor.FunctionDescriptor;
import com.kubadziworski.resolver.descriptor.PropertyDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnkelImportResolver implements ClassPathResolver {

    private final GlobalScope globalScope;

    public EnkelImportResolver(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    @Override
    public Optional<DeclarationDescriptor> preParseClassDeclarations(String importPackage) {
        if (globalScope.classExists(importPackage)) {
            ClassEntity entity = splitDeclaration(importPackage);
            return Optional.of(new ClassDescriptor(entity.clazzName, entity.packageName));
        }
        return Optional.empty();
    }

    @Override
    public List<DeclarationDescriptor> extractFieldOrMethods(String importPackage) {
        return findSpecificMethodOrField(importPackage);
    }


    @Override
    public List<DeclarationDescriptor> extractClassesFromPackage(String importPackage) {

        return globalScope.getAllScopes().stream()
                .filter(stringScopeEntry -> stringScopeEntry.getFullClassName().contains(importPackage))
                .filter(stringScopeEntry -> stringScopeEntry.getFullClassName().matches(importPackage + ".\\w+$"))
                .map(stringScopeEntry -> {
                    ClassEntity classEntity = splitDeclaration(stringScopeEntry.getFullClassName());
                    return new ClassDescriptor(classEntity.clazzName, classEntity.packageName);
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<List<DeclarationDescriptor>> getMethodsOrFields(String importPackage) {

        List<DeclarationDescriptor> descriptors = new ArrayList<>();
        Scope scope;
        if ((scope = globalScope.getScopeByClassName(importPackage)) != null) {
            List<DeclarationDescriptor> fields = scope.getFields().values()
                    .stream()
                    .filter(field -> field.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC))
                    .map(field -> new PropertyDescriptor(field.getName(), field)).collect(Collectors.toList());
            List<DeclarationDescriptor> methods = scope.getFunctionSignatures()
                    .stream()
                    .filter(function -> function.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC))
                    .map(field -> new FunctionDescriptor(field.getName(), field)).collect(Collectors.toList());
            descriptors.addAll(fields);
            descriptors.addAll(methods);
        }
        if (descriptors.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(descriptors);
    }

    private List<DeclarationDescriptor> findSpecificMethodOrField(String importPackage) {

        if (StringUtils.isEmpty(importPackage)) {
            return Collections.emptyList();
        }

        ClassEntity entity = splitDeclaration(importPackage);
        Scope scope;
        if ((scope = globalScope.getScopeByClassName(entity.packageName)) != null) {
            Optional<Field> fieldOptional = scope.getFields().values().stream()
                    .filter(field -> field.getName().equals(entity.clazzName))
                    .filter(field -> field.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC))
                    .findAny();
            if (fieldOptional.isPresent()) {
                return Collections.singletonList(new PropertyDescriptor(fieldOptional.get().getName(), fieldOptional.get()));
            }
            return scope.getFunctionSignatures().stream()
                    .filter(functionSignature -> functionSignature.getName().equals(entity.clazzName))
                    .filter(functionSignature -> functionSignature.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC))
                    .map(functionSignature -> new FunctionDescriptor(functionSignature.getName(), functionSignature))
                    .collect(Collectors.toList());
        } else {
            if (!entity.packageName.contains(".")) {
                return Collections.emptyList();
            }
            return findSpecificMethodOrField(entity.packageName);
        }
    }
}
