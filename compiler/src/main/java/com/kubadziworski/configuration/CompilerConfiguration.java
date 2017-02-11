package com.kubadziworski.configuration;


import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.resolver.ResolverContainer;

public abstract class CompilerConfiguration {

    private GlobalScope globalScope = new GlobalScope();

    public GlobalScope getGlobalScope() {
        return globalScope;
    }

    public abstract ResolverContainer getResolverContainer();

}
