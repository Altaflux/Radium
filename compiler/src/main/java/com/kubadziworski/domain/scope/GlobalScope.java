package com.kubadziworski.domain.scope;

import java.util.*;

/**
 * Created by plozano on 10/19/2016.
 */
public class GlobalScope {

    private final Map<String, Scope> scopeMap = new HashMap<>();
    private final Set<String> classesNames = new HashSet<>();

    public void addScope(String clazzName, Scope scope) {
        scopeMap.put(clazzName, scope);
        classesNames.add(clazzName);
    }

    public void registerClass(String clazzName){
        classesNames.add(clazzName);
    }

    public Scope getScopeByClassName(String clazzName) {
        return scopeMap.get(clazzName);
    }

    public boolean classExists(String clazzName) {
        return classesNames.contains(clazzName);
    }

    public Collection<Scope> getAllScopes() {
        return scopeMap.values();
    }
}
