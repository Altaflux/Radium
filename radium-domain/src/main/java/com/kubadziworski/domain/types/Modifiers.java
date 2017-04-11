package com.kubadziworski.domain.types;

import java.util.*;
import java.util.stream.Collectors;

public class Modifiers {

    public static Set<Modifier> ACCESS_MODIFIERS =
            new HashSet<>(Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE, Modifier.INTERNAL));

    private static Modifiers EMPTY = new Modifiers(Collections.emptySet());

    public Modifiers(Collection<Modifier> modifiers) {
        this.modifiers = Collections.unmodifiableSet(new HashSet<>(modifiers));
    }

    public static Modifiers empty() {
        return EMPTY;
    }

    private Set<Modifier> modifiers;

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public Modifiers without(Modifier modifier) {
        return new Modifiers(modifiers.stream().filter(modifier1 -> !modifier.equals(modifier1))
                .collect(Collectors.toSet()));
    }

    public Modifiers with(Modifier modifier) {
        Set<Modifier> modifierSet = new HashSet<>(modifiers);
        modifierSet.add(modifier);
        return new Modifiers(modifierSet);
    }

    public boolean contains(Modifier modifier) {
        return modifiers.contains(modifier);
    }
}
