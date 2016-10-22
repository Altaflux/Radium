package com.kubadziworski.domain.scope;

import java.util.*;

/**
 * Created by plozano on 10/19/2016.
 */
public class GlobalScope {

    private final Map<String, Scope> scopeMap = new HashMap<>();

    public void addScope(String clazzName, Scope scope) {
        scopeMap.put(clazzName, scope);
    }

    public Scope getScopeByClassName(String clazzName) {
        return scopeMap.get(clazzName);
    }

    public Collection<Scope> getAllScopes() {
        return scopeMap.values();
    }
}
