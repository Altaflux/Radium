package com.kubadziworski.domain.types;

import java.util.List;


public abstract class DeclaredTypeImpl extends ComponentTypeImpl implements DeclaredType {

    protected final Modifiers modifiers;
    protected final String simpleName;
    protected final String packageName;
    protected final List<TypeReference> superTypes;

    public DeclaredTypeImpl(String simpleName, String packageName, Modifiers modifiers, List<TypeReference> superTypes) {
        this.modifiers = modifiers;
        this.simpleName = simpleName;
        this.packageName = packageName;
        this.superTypes = superTypes;
    }

    @Override
    public String getQualifiedName() {
        if (packageName != null && packageName.length() != 0) {
            return packageName + "." + simpleName;
        }
        return simpleName;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public List<TypeReference> getSuperTypes() {
        return superTypes;
    }

    @Override
    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

}
