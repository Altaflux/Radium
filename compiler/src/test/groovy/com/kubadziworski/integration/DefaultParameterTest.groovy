package com.kubadziworski.integration

import com.kubadziworski.compiler.Compiler
import org.apache.commons.io.FileUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method


class DefaultParameterTest extends Specification {

    private final static Compiler compiler = new Compiler()

    def setup() {
        new File("target/integration/defaultParameters/").mkdirs()
    }

    @Unroll
    "Should Compile and run"() {
        given:
        def file = new File("target/integration/defaultParameters/" + filename)
        FileUtils.writeStringToFile(file, code)
        compiler.compile("target/integration/defaultParameters/" + filename)

        URL u = new File("target/integration/defaultParameters/").toURI().toURL()
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
        moreDefaultParams                    | "MoreDefaultParams.enk"
        defaultParams                        | "DefaultParamTest.enk"
        defaultConstructor                   | "DefaultConstructor.enk"
    }

    private final static moreDefaultParams =
            """
                            MoreDefaultParams {

                                fn start {
                                    assert(foo(x->"11") == "112")
                                    assert(foo(x->"11",y ->"22") == "1122")
                                    assert(foo(y ->"22") == "122")
                                    assert(foo("11") == "112")
                                    assert(foo() == "12")
                                }
                                
                                fn foo(x:String = "1" , y:String = "2" ) : String {
                                    println(x + y); 
                                    return x + y
                                }
                                
                                fn assert(actual: Boolean) {
                                    if (actual == true) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
							"""
    private final static defaultParams =
            """
                            DefaultParamTest {
                                fn start() {
                                    greet("kuba","enkel")
                                    greet("andrew")
                                }
                                fn greet (name :String ,  favouriteLanguage : String = "java") {
                                    println("Hello my name is ")
                                    println(name)
                                    println("and my favourite langugage is ")
                                    println(favouriteLanguage)
                                }
                            }
    						"""

    private final static defaultConstructor =
            """
                            DefaultConstructor(val x:Int = 2) {
                                
                                init {
                                    println("Hello init x: \$x")
                                }
                                fn start() {
                                   println("Hello start x: \$x")
                                }
                            }
							"""
}
