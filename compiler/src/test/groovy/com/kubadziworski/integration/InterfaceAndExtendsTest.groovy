package com.kubadziworski.integration

import com.kubadziworski.compiler.Compiler
import org.apache.commons.io.FileUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method


class InterfaceAndExtendsTest extends Specification {

    private final static Compiler compiler = new Compiler()

    def setup() {
        new File("target/integration/interfaceAndExtendsTest/").mkdirs()
    }

    @Unroll
    "Should Compile and run"() {
        given:
        def file = new File("target/integration/interfaceAndExtendsTest/" + filename)
        FileUtils.writeStringToFile(file, code)
        compiler.compile("target/integration/interfaceAndExtendsTest/" + filename)

        URL u = new File("target/integration/interfaceAndExtendsTest/").toURI().toURL()
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader()
        Class urlClass = URLClassLoader.class
        Method method = urlClass.getDeclaredMethod("addURL", URL.class)
        method.setAccessible(true)
        method.invoke(urlClassLoader, u) == null

        def name = Class.forName(filename.replace(".enk", ""))
        def method1 = name.getMethod("main", String[].class)

        expect:
        Object[] arggg = [[] as String[]]
        method1.invoke(null, arggg) == null

        where:
        code                                 | filename
        classExtends                         | "ClassExtends.enk"
        classExtendsConstructor              | "ClassExtendsConstructor.enk"
        classExtendsConstructorDefParam      | "ClassExtendsConstructorDefParam.enk"
        classExtendsConstructorDefFieldParam | "ClassExtendsConstructorDefFieldParam.enk"
    }


    private final static classExtends =
            """
                            import com.kubadziworski.test.superclass.BaseClass;
                          
                            class ClassExtends : BaseClass() {

                                fn start {
                                    foo();
                                }
                            }
							"""

    private final static classExtendsConstructorDefParam =
            """
                            import com.kubadziworski.test.superclass.BaseConstructor;
                          
                            class ClassExtendsConstructorDefParam(x:String = "test") : BaseConstructor(x) {
                                fn start {
                                    foo();
                                }
                            }
							"""
    private final static classExtendsConstructorDefFieldParam =
            """
                            import com.kubadziworski.test.superclass.BaseConstructor;
                          
                            class ClassExtendsConstructorDefFieldParam(val x:String = "test") : BaseConstructor(x) {
                                fn start {
                                    foo();
                                }
                            }
							"""
    private final static classExtendsConstructor =
            """
                            import com.kubadziworski.test.superclass.BaseConstructor;
                          
                            class ClassExtendsConstructor : BaseConstructor("test") {
                                fn start {
                                    foo();
                                }
                            }
							"""
}
