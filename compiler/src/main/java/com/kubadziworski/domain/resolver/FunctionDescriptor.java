package com.kubadziworski.domain.resolver;

import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.scope.FunctionSignature;

/**
 * Created by plozano on 10/15/2016.
 */
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
