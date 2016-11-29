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

                                    integerVar = 1234

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

								greet (String name, String favouriteLanguage="java") {
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

								bla : int

								start {
									bla = 5
									print bla
								}

							}
							"""

    private final static namedParams =
            """
							NamedParamsTest {

								start {
									createRect(x1->25,x2->-25,y1->50,y2->-0xE)
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
										var firstNum:Int = 3
										var secondNum:Int = 5

										var actual = sum(firstNum , secondNum)
										if( actual == expected ) {
											print "test passed"
										} else {
											print "test failed"
										}
									}

                                fooo(){
                                    bla(3)
                                }
                                Int? bla(Int? mip){
                                    return mip;
                                }

								Int sum (Int x ,Int y) {
									print x
									print y
									return x+y
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
								   booleanNegationTest()
							 }

							 primitiveComparisonTest {
								 var a:Int = 3
								 var b:Int = 3

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

                             booleanNegationTest(){
                                print "Doing boolean negation"

                                var result = !false
                                assert(expected -> true , actual -> result)

                             }

							 objectComparisonTest() {
								 var a:Int = 3
								 var b:Int = 3

								  print "Comparing integer " + a +" and " + b


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
								  var a = 3
								  var b = 4

								print "Comparing integer " + a +" and " + b

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

							  Unit assert(Boolean actual,Boolean expected) {
								if (actual == expected) {
									print "OK"
								}
								else {
									print "TEST FAILED"
								}
							  }
							}
							"""

    private static final unaryExpressionTest = """
							UnaryExpressions {

								globalField : Int

								start(){

                                    var x = 1
                                    var y = 1

                                    var preIncrement = ++x
                                    var postIncrement = y++

                                    var result = preIncrement == x
                                    assert(expected -> true , actual -> result)

                                    var result = postIncrement < y
                                    assert(expected -> true , actual -> result)

                                    globalField = 1

                                    var incSuffix = globalField++
                                    var result = incSuffix < globalField
                                    assert(expected -> true , actual -> result)

                                    globalField = 1
                                    var incPrefix = ++globalField
                                    var result2 = incPrefix == globalField
                                    assert(expected -> true , actual -> result2)

								}

								Unit assert(Boolean actual,Boolean expected) {
									if (actual == expected) {
										print "OK"
									}
									else {
										print "TEST FAILED"
									}
								  }
							}
                            """
    private static final globalLocal = """
							GlobalLocal {
								x : Int

								start(){
									x = 2;
									var x = 1;
									print this.x;
									print x;

									assert(expected -> true , actual -> x == 1);
									assert(expected -> true , actual -> this.x == 2);
								}
								Unit assert(Boolean actual,Boolean expected) {
									if (actual == expected) {
										print "OK"
									}
									else {
										print "TEST FAILED"
									}
								}
							}
						""";
    private static final staticTest = """
							StaticTest {

								start(){
									print java.lang.System.out.hashCode();
									com.kubadziworski.test.Library.execute("Hello!!");
								}
								Unit assert(Boolean actual,Boolean expected) {
									if (actual == expected) {
										print "OK"
									}
									else {
										print "TEST FAILED"
									}
								}
							}
						""";
    private static final staticFunctionTest = """

							StaticFunctionTest {

								start(){
									assert(expected -> true , actual -> 1 == 1);
									this.assert(expected -> true , actual -> 1 == 1);
								}
								static Unit assert(Boolean actual,Boolean expected) {
									if (actual == expected) {
										print "OK"
									}
									else {
										print "TEST FAILED"
									}
								}
							}
						""";
    private static final importingTest = """
							import com.kubadziworski.test.Library.*;
							ImportingTest {

								start(){
									execute("hello");
									var myStuff = 1
									print myStuff
									print statField
								}

							}
						""";
    private final static getterSetter =
            """
							GetterSetter {
								myField : Int
                                get(){
                                    print "returning value getter"
                                    print field
                                    return field;
                                }
                                set(value){
                                    print "setting value"
                                    field = value
                                    print field
                                }
								start {
									myField = 5
                                    var result = myField == 5
									assert(result, true)
								}
                                Unit assert(Boolean actual,Boolean expected) {
                                    if (actual == expected) {
                                        print "OK"
                                    }
                                    else {
                                        print "TEST FAILED"
                                    }
                                }
							}
							"""
    private final static getterStatement =
            """
							GetterStatement {
								myField : Int
                                get() = field
                                set(value){
                                    print "setting value"
                                    field = value
                                    print field
                                }
								start {
									myField = 5
                                    var result = myField == 5
									assert(result, true)
								}
                                Unit assert(Boolean actual,Boolean expected) {
                                    if (actual == expected) {
                                        print "OK"
                                    }
                                    else {
                                        print "TEST FAILED"
                                    }
                                }
							}
							"""

    private final static functionSingleStatements =
            """
							FunctionSingleStatements {

								start {
								    print singleIntFunction()
									assert(singleIntFunction() == 300, true)
									loggingFunction("OK")
								}

                                loggingFunction(String stuff) = print stuff
								Int singleIntFunction() = 300

                                Unit assert(Boolean actual,Boolean expected) {
                                    if (actual == expected) {
                                        print "OK"
                                    }
                                    else {
                                        print "TEST FAILED"
                                    }
                                }
							}
							"""

    private final static multiFiles =
            """
							FirstClass {
								start {
								    print "Hello First Class"
								}

							}
                            SecondClass {
                                start {
                                   print "Hello Second Class"
                                }
                            }
							"""
    private final static ifExpressions =
            """
							IfExpression {

								start {
                                    var foo = if(true){
                                        5
                                    } else {
                                        6
                                    }
                                    assert(foo == 5, true)
                                    var bar = if(false){
                                        5
                                    } else {
                                        6
                                    }
                                    assert(bar == 6, true)

                                    var baz = if(false){
                                        1
                                    }else if(true){
                                        2
                                    }else {
                                        3
                                    }
									assert(baz == 2, true)

									var blizz = if true 8 else 9
									assert(blizz == 8, true)
								}
                                Unit assert(Boolean actual,Boolean expected) {
                                    if (actual == expected) {
                                        print "OK"
                                    }
                                    else {
                                        print "TEST FAILED"
                                    }
                                }
							}
							"""
    private static final myTryStatement = """
							TryStatement {
                                myTrue : Boolean = true

								start(){
								    process();
								    val y = foo();
                                    print y

                                    print finReturn()
								}

                                Int finReturn(){

                                    try{
                                        return throwingMethod();
                                    }catch(e: Exception){
                                        return 2
                                    }finally {
                                        if(myTrue){
                                            return 1
                                        }
                                    }

                                }

                                Int throwingMethod(){
                                    throw new RuntimeException()
                                }

								Int foo(){

								   try {
								        if(true){
								            print "primer"
								            return 1;
								        }
								        print "segundo"
								        return 2;
								   }
								   catch(e:RuntimeException){
								        print "tercer"
								        return 3;
								   }catch(e: Exception){
								     print "cuarto"
								     return 5;
								   }finally {
								        print "hola"
								   }

								}

								process(){
                                          try {
                                                throw new RuntimeException()

                                            } catch (e:RuntimeException){
                                                print "OK"
                                            }

                                            try {
                                               throw new RuntimeException()

                                            } catch (e:RuntimeException){
                                                print "OK"
                                            }catch(e:Exception){
                                                print "FAILED"
                                            }
                                            try {
                                                throw new RuntimeException()

                                            } catch (e:RuntimeException){
                                                print "OK"
                                            }catch(e:Exception){
                                                print "FAILED"
                                            }finally{
                                                print "finally called OK"
                                          }
						        }

								Unit assert(Boolean actual, Boolean expected) {
									if (actual == expected) {
										print "OK"
									}
									else {
										print "TEST FAILED"
									}
								}
							}
						"""
    private static final fieldInitializing =
            """
							FieldInitializing {
								myField : Int = 10

								start {
                                    var result = myField == 10
                                    assert(result, true)
								}
                                Unit assert(Boolean actual, Boolean expected) {
                                    if (actual == expected) {
                                        print "OK"
                                    }
                                    else {
                                        print "TEST FAILED"
                                    }
                                }
							}
							"""

    private final static fieldInitializingWithConstructor =
            """
							FieldInitializingWithConstructor {
                                myField : Int = 10

                                FieldInitializingWithConstructor() {
                                    print myField
                                }

                                start() {
                                    var result = myField == 10
                                    assert(result, true)
                                }

                             Unit assert(Boolean actual,Boolean expected) {
                                    if (actual == expected) {
                                        print "OK"
                                    }
                                    else {
                                        print "TEST FAILED"
                                    }
                             }
							}
            """
    private static final detectReturnCompleteStatement =
            """
                    DetectReturnCompleteStatement {
                        myField : Int = 10

                        start(){
                            assert(foo(), true);
                        }

                        Boolean foo(){
                            if(myField == 10){
                                return true;
                            }else {
                                return false;
                            }
                        }
                         Unit assert(Boolean actual, Boolean expected) {
                                if (actual == expected) {
                                    print "OK"
                                }
                                else {
                                    print "TEST FAILED"
                                }
                         }
                    }
            """

    private static final throwStatement =
            """
                    ThrowStatement {

                        start(){
                            try {
                                throw new RuntimeException();
                            }catch(e : Exception){
                                print "CATCH SUCCESS"
                            }
                        }

                    }
            """

    private static final nullValue =
            """
                    NullValue {
                        start(){
                            var foo:String? = null
                            print foo

                          var bla = try {
                                throw new NullPointerException();
                            }catch(e: NullPointerException){
                                print "hello"
                            }catch(e:Exception){
                                print "FAIL"
                            }

                            print bla
                        }
                    }
            """

    private static final returnUnit =
            """
                ReturnUnit {

                    start{
                        var x = foo();
                        print x
                        var y = bar();
                        print y
                    }

                    foo(){
                        print "Called method without return"
                    }
                    bar(){
                        return Unit.INSTANCE
                    }
                    baz(){
                        var x = Unit.INSTANCE
                        return x
                    }

                }
            """
    private static final concreteReturnUnit =
            """
                ConcreteReturnUnit {

                    start{
                        var x = baz();
                        print x!!
                    }

                   Unit? baz(){
                        var x = Unit.INSTANCE
                        return x
                    }

                }
            """
    private static final superCall = """
                CallParentClass {

                    start(){
                        print toString();
                        var superString = super.toString();
                        assert(superString.contains("myToString"), false)
                        assert(toString().contains("CallParentClass"), true)

                    }

                   String toString(){
                      return "myToString :: " + super.toString()
                    }

                     Unit assert(Boolean actual,Boolean expected) {
                            if (actual == expected) {
                                print "OK"
                            }
                            else {
                                print "TEST FAILED"
                            }
                     }
                }
            """

    private static final typeCoercion = """
                import com.kubadziworski.test.DummyClass;
                TypeCoercion {

                    start(){
                        var dummyClass = new DummyClass();
                        var number:Int? = dummyClass.objectInt();

                        var sum = number!! + 1.5
                        print sum
                        number = null
                    }
                }
            """
    @Unroll
    def "Should Compile and run"() {
        expect:
        boolean dirs = new File("target/enkelClasses/").mkdirs()
        def file = new File("target/enkelClasses/" + filename)

        FileUtils.writeStringToFile(file, code)
        Compiler.main("target/enkelClasses/" + filename)

        URL u = new File("target/enkelClasses/").toURL();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u) == null;

        def name = Class.forName(filename.replace(".enk", ""))
        def method1 = name.getMethod("main", String[].class)

        Object[] arggg = [[] as String[]]
        method1.invoke(null, arggg) == null;

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
        equalityTest             | "EqualitySyntax.enk"
        unaryExpressionTest      | "UnaryExpressions.enk"
        globalLocal              | "GlobalLocal.enk"
        staticTest               | "StaticTest.enk"
        staticFunctionTest       | "StaticFunctionTest.enk"
        importingTest            | "ImportingTest.enk"
        getterSetter             | "GetterSetter.enk"
        getterStatement          | "GetterStatement.enk"
        functionSingleStatements | "FunctionSingleStatements.enk"
        ifExpressions            | "IfExpression.enk"
        myTryStatement             | "TryStatement.enk"
        fieldInitializing        | "FieldInitializing.enk"
        fieldInitializingWithConstructor | "FieldInitializingWithConstructor.enk"
        detectReturnCompleteStatement | "DetectReturnCompleteStatement.enk"
        throwStatement          | "ThrowStatement.enk"
        nullValue               | "NullValue.enk"
        returnUnit              | "ReturnUnit.enk"
        concreteReturnUnit      | "ConcreteReturnUnit.enk"
        superCall               | "CallParentClass.enk"
        typeCoercion            | "TypeCoercion.enk"
    }


    @Unroll
    def "Should Create Multiple files"() {
        expect:
        boolean dirs = new File("target/enkelClasses/").mkdirs()
        def file = new File("target/enkelClasses/" + filename)

        FileUtils.writeStringToFile(file, code)
        Compiler.main("target/enkelClasses/" + filename)

        URL u = new File("target/enkelClasses/").toURL();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u) == null;

        for (int i = 0; i < classes.size(); i++) {
            Class name = Class.forName(classes.get(i))
            Method method1 = name.getMethod("main", String[].class)
            Object[] arggg = [[] as String[]]
            method1.invoke(null, arggg);
        }

        where:
        code       | filename           | classes
        multiFiles | "MultiClasses.enk" | Arrays.asList("SecondClass", "FirstClass")
    }
}
