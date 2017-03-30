package com.kubadziworski.util;

import net.florianschoppmann.java.reflect.ReflectionTypes;

import javax.lang.model.type.DeclaredType;
import java.util.List;

/**
 * Created by plozano on 3/12/2017.
 */
public class NTest {

    public static void main(String... args) {

        ReflectionTypes types = ReflectionTypes.getInstance();
// listSuperNumberType: List<? super Number>
        DeclaredType listSuperNumberType = types.getDeclaredType(
                types.typeElement(List.class),
                types.getWildcardType(null, types.typeMirror(Number.class))
        );
// iterableExtendsNumberType: Iterable<? extends Number>
        DeclaredType iterableExtendsNumberType = types.getDeclaredType(
                types.typeElement(Iterable.class),
                types.getWildcardType(types.typeMirror(Number.class), null)
        );
// iterableType: Iterable<?>
        DeclaredType iterableType = types.getDeclaredType(
                types.typeElement(Iterable.class),
                types.getWildcardType(null, null)
        );
        Class<List>  foo = List.class;


        System.out.println(types.isSubtype(listSuperNumberType, iterableType));
        System.out.println(types.isSubtype(iterableExtendsNumberType, iterableType));
        System.out.println(!types.isSubtype(listSuperNumberType, iterableExtendsNumberType));

    }
}
