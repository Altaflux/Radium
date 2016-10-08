package com.kubadziworski.integration

import com.kubadziworski.compiler.Compiler
import org.apache.commons.io.FileUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

/**
 * Created by kuba on 11.05.16.
 */
class ShouldCompileTest extends Specification {

	private final static helloWorld =
							"""
							HelloWorld {

								start {
									print "hello world!"
								}
							}
							"""

	private final static loopsCode =
							"""
							Loops {
								start() {
									for i from 3 to 1 {
										metod(i)
									}
								}

								metod(int x) {
									print x
								}
							}
							"""

	private final static allTypes =
							"""
							AllPrimitiveTypes {
								start() {
									var stringVar = "str"
									var booleanVar = false
									var integerVar = 2745
									var doubleVar = 2343.05

									print "stringVar=" + stringVar + ", booleanVar=" + booleanVar + ", integerVar=" + integerVar + ", doubleVar=" + doubleVar
									print 2.5+2.5 + " is the sum of 2.5 and 2.5"
								}
							}
							"""

	private final static defaultParams =
							"""
							DefaultParamTest {
								start() {
									greet("kuba","enkel")
									greet("andrew")
								}

								greet (string name,string favouriteLanguage="java") {
									print "Hello my name is "
									print name
									print "and my favourite langugage is "
									print favouriteLanguage
								}
							}
    						"""
	private final static fields =
							"""
							Fields {

								int field

								start {
									field = 5
									print field
								}

							}
							"""

	private final static namedParams =
							"""
							NamedParamsTest {

								start {
									createRect(x1->25,x2->-25,y1->50,y2->-50)
								}

								createRect (int x1,int y1,int x2, int y2) {
									print "Created rect with x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2
								}
							}
							"""

	private final static sumCalculator =
							"""
								SumCalculator {
									start() {
										var expected = 8
										var actual = sum(3,5)
										if( actual == expected ) {
											print "test passed"
										} else {
											print "test failed"
										}
									}


								int sum (int x ,int y) {
									x+y
								}
							}
							"""

	private final static defaultConstructor =
							"""
							DefaultConstructor {

							start() {
							print "Hey I am 'start' method. I am not static so the default constructor must have been called, even though it is not defined"
							}
							}
							"""

	private final static construcotrWithParams =
							"""
							ConstructorWithParams {

							ConstructorWithParams(int x) {
							print "Hey I am constructor with parameter x = " + x
							}

							start() {
							var instance = new ConstructorWithParams(5)
							instance.doStuff()
							}

							doStuff {
							print "doing stuff on ConstructorWithParams instance"
							}
							}
							"""

	private final static parameterLessConsturctor =
							"""
							ParameterLessConstructor {

							ParameterLessConstructor() {
							print "Hey I am constructor without parameters"
							}

							start() {
							doStuff()
							}

							doStuff {
							print "doing stuff on ParameterLessConstructor object"
							}
							}
							"""

	private static final equalityTest =
							"""
							EqualitySyntax {

							 start {
								   objectComparisonTest()
								   primitiveComparisonTest()
								   primitiveComparisonTest2()
								   objectComparisonTest2()
							 }

							 primitiveComparisonTest {
								 var a = 3
								 var b = 3

								 print "Comparing primitive " + a +" and " + b

								 var result = a == b
								 assert(expected -> true , actual -> result)

								 var result = a != b
								 assert(expected -> false , actual -> result)

								 var result = a > b
								 assert(expected -> false , actual -> result)

								 var result = a < b
								 assert(expected -> false , actual -> result)

								 var result = a >= b
								 assert(expected -> true , actual -> result)

								 var result = a <= b
								 assert(expected -> true , actual -> result)
							 }


							 objectComparisonTest() {
								 var a = new java.lang.Integer(3)
								 var b = new java.lang.Integer(3)

								  print "Comparing integer " + a.toString() +" and " + b.toString()


								 var result = a == b
								 assert(expected -> true , actual -> result)

								 var result = a != b
								 assert(expected -> false , actual -> result)

								 var result = a > b
								 assert(expected -> false , actual -> result)

								 var result = a < b
								 assert(expected -> false , actual -> result)

								 var result = a >= b
								 assert(expected -> true , actual -> result)

								 var result = a <= b
								 assert(expected -> true , actual -> result)
							 }

							 primitiveComparisonTest2 {
								  var a = 3
								  var b = 4

								 print "Comparing primitive " + a +" and " + b


									var result = a == b
									assert(expected -> false , actual -> result)

									var result = a != b
									assert(expected -> true , actual -> result)

									var result = a > b
									assert(expected -> false , actual -> result)

									var result = a < b
									assert(expected -> true , actual -> result)

									var result = a >= b
									assert(expected -> false , actual -> result)

									var result = a <= b
									assert(expected -> true , actual -> result)
							  }

							 objectComparisonTest2() {
								  var a = new java.lang.Integer(3)
								  var b = new java.lang.Integer(4)

								print "Comparing integer " + a.toString() +" and " + b.toString()

								  var result = a == b
								  assert(expected -> false , actual -> result)

								  var result = a != b
								  assert(expected -> true , actual -> result)

								  var result = a > b
								  assert(expected -> false , actual -> result)

								  var result = a < b
								  assert(expected -> true , actual -> result)

								  var result = a >= b
								  assert(expected -> false , actual -> result)


								  var result = a <= b
								  assert(actual -> result, expected -> true )
							  }

							  void assert(boolean actual,boolean expected) {
								if (actual == expected) {
									print "OK"
								}
								else {
									print "TEST FAILED"
								}
							  }
							}
							"""

	@Unroll
	def "Should Compile and run"() {
		expect:
			def file = new File(filename)
			FileUtils.writeStringToFile(file, code)
			Compiler.main(filename)

			URL u = new File(".").toURL();
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(urlClassLoader, u) == null;

//			def name = Class.forName(filename.replace(".enk", ""))
//			def method1 = name.getMethod("main", String[].class)
//
//			String[] objs = null
//			MethodUtils.invokeStaticMethod(name,"main",objs);
		where:
			code                     | filename
			helloWorld               | "HelloWorld.enk"
			loopsCode                | "Loops.enk"
			allTypes                 | "AllPrimitiveTypes.enk"
			defaultParams            | "DefaultParamTest.enk"
			fields                   | "Fields.enk"
			namedParams              | "NamedParamsTest.enk"
			sumCalculator            | "SumCalculator.enk"
			defaultConstructor       | "DefaultConstructor.enk"
			parameterLessConsturctor | "ParameterLessConstructor.enk"
			construcotrWithParams    | "ConstructorWithParams.enk"
			equalityTest    | "EqualitySyntax.enk"
	}

}