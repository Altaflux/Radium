package com.kubadziworski.util;

import com.fasterxml.classmate.*;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class Test {


    public static void main(String... args) {

        com.fasterxml.classmate.TypeResolver resolver = new TypeResolver();
        //resolver.resolve()

       ResolvedType callable = resolver.resolve(Callable.class, Integer.class);


        ResolvedType resolvedType = resolver.resolve(List.class, callable);
        System.out.println(resolvedType.getFullDescription());
        System.out.println(resolvedType.getSignature());
        MemberResolver memberResolver = new MemberResolver(resolver);



        ResolvedTypeWithMembers withMembers = memberResolver.resolve(resolvedType, null, null);
        ResolvedMethod[] methods = withMembers.getMemberMethods();

        System.out.println("METHODS");
        for(ResolvedMethod method : methods){
            System.out.println("MethodName: " + method.getName());
           for(int x = 0; x <  method.getArgumentCount() ; x++){
               System.out.println("Argument: " + x);
               ResolvedType argType = method.getArgumentType(x);

               System.out.println(argType);
           }


            ResolvedType returnType = method.getReturnType();
            System.out.println("RETURN TYPE");
            System.out.println(returnType);
        }


    }

    public static <T extends Number> void test(T foo){

        GenericType<List<T>> type = new GenericType<List<T>>() {

        };

        List<String> list = new ArrayList<>();
    }


    public interface Base<T> {
        T getStuff();
    }

    public static class ListBase<T> implements Base<List<T>> {
        protected T value;

        protected ListBase(T v) {
            value = v;
        }

        @Override
        public List<T> getStuff() {
            return Collections.singletonList(value);
        }
    }

    public static class Actual extends ListBase<String> {
        public Actual(String value) {
            super(value);
        }
    }
}
