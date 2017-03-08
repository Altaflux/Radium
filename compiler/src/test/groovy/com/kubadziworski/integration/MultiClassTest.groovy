package com.kubadziworski.integration

import com.kubadziworski.compiler.Compiler
import org.apache.commons.io.FileUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

class MultiClassTest  extends Specification {

    private final static Compiler compiler = new Compiler()

    def setup() {
        new File("target/integration/multiClassTest/").mkdirs()
    }


    @Unroll
    "Should Create Multiple files"() {
        given:
        def file = new File("target/integration/multiClassTest/" + filename)

        FileUtils.writeStringToFile(file, code)
        compiler.compile("target/integration/multiClassTest/" + filename)

        URL u = new File("target/integration/multiClassTest/").toURI().toURL()
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader()
        Class urlClass = URLClassLoader.class
        Method method = urlClass.getDeclaredMethod("addURL", URL.class)
        method.setAccessible(true)

        expect:
        method.invoke(urlClassLoader, u) == null

        for (int i = 0; i < classes.size(); i++) {
            Class name = Class.forName(classes.get(i))
            Method method1 = name.getMethod("main", String[].class)
            Object[] arggg = [[] as String[]]
            method1.invoke(null, arggg)
        }

        where:
        code       | filename           | classes
        multiFiles | "MultiClasses.enk" | Arrays.asList("SecondClass", "FirstClass")
    }

    private final static multiFiles =
            """
                            class FirstClass {
                                fn start {
                                    println("Hello First Class")
                                }

                            }
                            class SecondClass {
                                fn start {
                                   println("Hello Second Class")
                                }
                            }
							"""
}
