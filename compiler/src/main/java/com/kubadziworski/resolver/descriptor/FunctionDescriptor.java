package com.kubadziworski.resolver.descriptor;

import com.kubadziworski.domain.scope.FunctionSignature;


public class FunctionDescriptor implements DeclarationDescriptor {
    private final String name;
    private final FunctionSignature functionSignature;

    public FunctionDescriptor(String name, FunctionSignature functionSignature) {
        this.name = name;
        this.functionSignature = functionSignature;
    }

    @Override
    public String getName() {
        return name;
    }

    public FunctionSignature getFunctionSignature() {
        return functionSignature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionDescriptor that = (FunctionDescriptor) o;

        if (!name.equals(that.name)) return false;
        return functionSignature.equals(that.functionSignature);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + functionSignature.hashCode();
        return result;
    }
}
