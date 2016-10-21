package com.kubadziworski.domain.resolver;


import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class EnkelImportResolver implements BaseImportResolver {

    private final GlobalScope globalScope;

    public EnkelImportResolver(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }


    @Override
    public List<DeclarationDescriptor> extractClazzFieldOrMethods(String importPackage) {
        if (globalScope.scopeMap.containsKey(importPackage)) {
            ClassEntity entity = splitDeclaration(importPackage);
            return Collections.singletonList(new ClassDescriptor(entity.clazzName, entity.packageName));
        }
        return findSpecificMethodOrField(importPackage);
    }


    @Override
    public List<DeclarationDescriptor> extractClassesFromPackage(String importPackage) {
        return globalScope.scopeMap.entrySet().stream()
                .filter(stringScopeEntry -> stringScopeEntry.getKey().contains(importPackage))
                .filter(stringScopeEntry -> stringScopeEntry.getKey().matches(importPackage + ".\\w+$"))
                .map(stringScopeEntry -> {
                    ClassEntity classEntity = splitDeclaration(stringScopeEntry.getKey());
                    return new ClassDescriptor(classEntity.clazzName, classEntity.packageName);
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<List<DeclarationDescriptor>> getMethodsOrFields(String importPackage) {

        List<DeclarationDescriptor> descriptors = new ArrayList<>();
        if (globalScope.scopeMap.containsKey(importPackage)) {
            Scope scope = globalScope.scopeMap.get(importPackage);
            List<DeclarationDescriptor> fields = scope.getFields().values()
                    .stream()
                    .filter(field -> Modifier.isStatic(field.getModifiers()))
                    .map(field -> new PropertyDescriptor(field.getName(), field)).collect(Collectors.toList());
            List<DeclarationDescriptor> methods = scope.getFunctionSignatures()
                    .stream()
                    .filter(function -> Modifier.isStatic(function.getModifiers()))
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
        if ((scope = globalScope.scopeMap.get(entity.packageName)) != null) {
            Optional<Field> fieldOptional = scope.getFields().values().stream()
                    .filter(field -> field.getName().equals(entity.clazzName))
                    .filter(field -> Modifier.isStatic(field.getModifiers()))
                    .findAny();
            if (fieldOptional.isPresent()) {
                return Collections.singletonList(new PropertyDescriptor(fieldOptional.get().getName(), fieldOptional.get()));
            }
            return scope.getFunctionSignatures().stream()
                    .filter(functionSignature -> functionSignature.getName().equals(entity.clazzName))
                    .filter(functionSignature -> Modifier.isStatic(functionSignature.getModifiers()))
                    .map(functionSignature -> new FunctionDescriptor(functionSignature.getName(), functionSignature))
                    .collect(Collectors.toList());
        } else {
            if(!entity.packageName.contains(".")){
                return Collections.emptyList();
            }
            return findSpecificMethodOrField(entity.packageName);
        }
    }
}
