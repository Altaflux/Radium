package com.kubadziworski.integration

import com.kubadziworski.compiler.Compiler
import org.apache.commons.io.FileUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

class ShouldCompileTest extends Specification {

    private final static Compiler compiler = new Compiler()

    def setup() {
        new File("target/enkelClasses/").mkdirs()
    }

    @Unroll
    "Should Compile and run"() {
        given:
        def file = new File("target/enkelClasses/" + filename)
        FileUtils.writeStringToFile(file, code)
        compiler.compile("target/enkelClasses/" + filename)

        URL u = new File("target/enkelClasses/").toURI().toURL()
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
        code                             | filename
        helloWorld                       | "HelloWorld.enk"
        loopsCode                        | "Loops.enk"
        allTypes                         | "AllPrimitiveTypes.enk"
        moreDefaultParams                | "MoreDefaultParams.enk"
        defaultParams                    | "DefaultParamTest.enk"
        fields                           | "Fields.enk"
        namedParams                      | "NamedParamsTest.enk"
        sumCalculator                    | "SumCalculator.enk"
        defaultConstructor               | "DefaultConstructor.enk"
        parameterLessConsturctor         | "ParameterLessConstructor.enk"
        construcotrWithParams            | "ConstructorWithParams.enk"
        equalityTest                     | "EqualitySyntax.enk"
        unaryExpressionTest              | "UnaryExpressions.enk"
        globalLocal                      | "GlobalLocal.enk"
        staticTest                       | "StaticTest.enk"
        staticFunctionTest               | "StaticFunctionTest.enk"
        importingTest                    | "ImportingTest.enk"
        getterSetter                     | "GetterSetter.enk"
        getterStatement                  | "GetterStatement.enk"
        functionSingleStatements         | "FunctionSingleStatements.enk"
        ifExpressions                    | "IfExpression.enk"
        myTryStatement                   | "TryStatement.enk"
        fieldInitializing                | "FieldInitializing.enk"
        fieldInitializingWithConstructor | "FieldInitializingWithConstructor.enk"
        detectReturnCompleteStatement    | "DetectReturnCompleteStatement.enk"
        throwStatement                   | "ThrowStatement.enk"
        nullValue                        | "NullValue.enk"
        returnUnit                       | "ReturnUnit.enk"
        concreteReturnUnit               | "ConcreteReturnUnit.enk"
        superCall                        | "CallParentClass.enk"
        typeCoercion                     | "TypeCoercion.enk"
        primitiveFunctions               | "PrimitiveFunctions.enk"
        innerTry                         | "InnerTry.enk"
        parenthesisExpressions           | "ParenthesisExpressions.enk"
        inlineCode                       | "InlineCode.enk"
        callStaticImports                | "CallStaticImports.enk"
        variableEscaping                 | "VariableEscaping.enk"
        numericLiterals                  | "NumericLiterals.enk"
        sendNullToMethod                 | "SendNullToMethod.enk"
    }


    @Unroll
    "Should Create Multiple files"() {
        given:
        def file = new File("target/enkelClasses/" + filename)

        FileUtils.writeStringToFile(file, code)
        compiler.compile("target/enkelClasses/" + filename)

        URL u = new File("target/enkelClasses/").toURI().toURL()
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

    private final static helloWorld =
            """
                            HelloWorld {

                                fn start {
                                    var x:String = "Enkel"
                                    println("hello \$x world!")
                                    println("hello \${x} world!")
                                }
                            }
							"""

    private final static loopsCode =
            """
                            Loops {
                                fn start() {
                                    for i from 3 to 1 {
                                        method(i)
                                    }
                                }

                                fn method(x: Int) {
                                    println(x)
                                }
                            }
							"""

    private final static allTypes =
            """
                            AllPrimitiveTypes {
                                fn start() {
                                    var stringVar = "str"
                                    var booleanVar = false
                                    var integerVar = 2745
                                    var doubleVar = 2.5
                                    integerVar = 1234

                                    val floatVar:Float = 43.6F
                                    var foofar:Long = 32344L

                                    println("stringVar=" + stringVar + ", booleanVar=" + booleanVar + ", integerVar=" + integerVar + ", doubleVar=" + doubleVar)
                                    println("floatVar=" + floatVar + ", longVar=" + foofar)
                                    println(2.5+2.5 + " is the sum of 2.5 and 2.5")
                                }
                            }
							"""
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
    private final static fields =
            """
                            Fields {
                                var myField : Int
                                fn start {
                                    myField = 5
                                    println(myField)
                                }
                            }
							"""

    private final static namedParams =
            """
                            NamedParamsTest {

                                fn start {
                                    createRect(x1->25,x2->-25,y1->50,y2->-0xE)
                                }

                                fn createRect (x1 :Int , y1: Int, x2: Int, y2:Int) {
                                    println("Created rect with x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2)
                                }
                            }
							"""

    private final static sumCalculator =
            """
                                SumCalculator {
                                    fn start() {
                                        var expected = 8
                                        var firstNum:Int = 3
                                        var secondNum:Int = 5

                                        var actual = sum(firstNum , secondNum)
                                        if( actual == expected ) {
                                            println("test passed")
                                        } else {
                                            println("test failed")
                                        }

                                        assert(secondNum % firstNum == 2, true)
                                    }
                                    fn assert(actual: Boolean , expected: Boolean) {
                                        if (actual == expected) {
                                            println("OK")
                                        }
                                        else {
                                            println("TEST FAILED")
                                            throw new AssertionError("TEST FAILED")
                                        }
                                    }

                                    fn fooo(){
                                        bla(3)
                                    }
                                    fn bla(mip: Int?):Int?{
                                        return mip;
                                    }

                                    fn sum (x:Int ,y:Int):Int {
                                        println(x)
                                        println(y)
                                        return x+y
                                    }
                                    fn mod(x:Int, y:Int):Int {
                                        println(x)
                                        println(y)
                                        return x%y
                                    }
                            }
							"""

//    private final static sumCalculator =
//            """
//                                SumCalculator {
//                                    fn start() {
//                                        var expected = 8
//                                        var firstNum:Int = 3
//                                       // var secondNum:Int = 5
//
//                                       // var actual = sum(firstNum , secondNum)
//                                         println("test passed")
//
//                                        fooo(firstNum)
//                                        //assert(secondNum % firstNum == 2, true)
//                                    }
//                                    fn fooo(x :Int){
//                                    }
//                                    fn assert(actual: Boolean , expected: Boolean) {
//                                        if (actual == expected) {
//                                         //   println("OK")
//                                        }
//                                        else {
//                                        //    println("TEST FAILED")
//                                        }
//                                    }
//
//                                    fn sum (x:Int, y:Int):Int {
//                                     //   println(x)
//                                     //   println(y)
//                                        return x+y
//                                    }
//
//                            }
//							"""
    private final static defaultConstructor =
            """
                            DefaultConstructor(val x:Int = 2) {
                                
                                init {
                                    println("Hello")
                                }
                                fn start() {
                                   println("Hey I am 'start' method. I am not static so the default constructor must have been called, even though it is not defined")
                                }
                            }
							"""

    private final static construcotrWithParams =
            """
                            ConstructorWithParams(x:Int = 2) {

                                init {
                                    println("Hey I am constructor with parameter x = " + x)
                                }

                                fn start() {
                                    var instance = new ConstructorWithParams(5)
                                    instance.doStuff()
                                }

                                fn doStuff {
                                    println("doing stuff on ConstructorWithParams instance")
                                }
                            }
							"""

    private final static parameterLessConsturctor =
            """
                            ParameterLessConstructor() {

                                init {
                                    println("Hey I am constructor without parameters")
                                }

                                fn start() {
                                    doStuff()
                                }

                                fn doStuff {
                                    println("doing stuff on ParameterLessConstructor object")
                                }
                            }
							"""

    private static final equalityTest =
            """
                            EqualitySyntax {

                             fn start {
                                   objectComparisonTest()
                                   primitiveComparisonTest()
                                   primitiveComparisonTest2()
                                   objectComparisonTest2()
                                   booleanNegationTest()
                             }

                             fn primitiveComparisonTest {
                                 var a:Int = 3
                                 var b:Int = 3

                                 println("Comparing primitive \${a} and \${b}")

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

                             fn booleanNegationTest(){
                                println("Doing boolean negation")

                                var result = !false
                                assert(expected -> true , actual -> result)

                             }

                             fn objectComparisonTest() {
                                 var a:Int = 3
                                 var b:Int = 3

                                 println("Comparing integer \${a} and \${b}")


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

                             fn primitiveComparisonTest2 {
                                  var a = 3
                                  var b = 4

                                 println("Comparing primitive \${a} and \${b}")


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

                             fn objectComparisonTest2() {
                                  var a = 3
                                  var b = 4

                                  println("Comparing integer \${a} and \${b}")

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

                              fn assert(actual: Boolean , expected: Boolean) {
                                if (actual == expected) {
                                    println("OK")
                                }
                                else {
                                    println("TEST FAILED")
                                    throw new AssertionError("TEST FAILED")
                                }
                              }
                            }
							"""

    private static final unaryExpressionTest = """
                            import com.kubadziworski.test.unary.ClassWithField;
                            
                            UnaryExpressions {

                                var globalField : Int

                                fn start(){

                                    var x = 1
                                    var y = 1

                                    var preIncrement = ++x
                                    var postIncrement = y++

                                    var result = preIncrement == x
                                    assert(expected -> true , actual -> result)

                                    var result = postIncrement < y
                                    assert(expected -> true , actual -> result)

                                    globalField = 1
                                    var incPrefix = ++globalField
                                    var result2 = incPrefix == globalField
                                    assert(expected -> true , actual -> result2)
                                    
                                    testJavaFields();
                                }
                                
                                fn testJavaFields(){
                                    println("Testing Java field increment")
                                    val myObject = new ClassWithField();
                                    assert(expected -> true , actual -> myObject.nonStatValue == 1)
                                    val result = myObject.nonStatValue++;
                                    assert(expected -> true , actual -> result == 1)
                                    assert(expected -> true , actual -> myObject.nonStatValue == 2)

                                    println("Testing Java static field increment")
                                    assert(expected -> true , actual -> ClassWithField.statValue == 1)
                                    val secondResult = ClassWithField.statValue++;
                                    assert(expected -> true , actual -> secondResult == 1)
                                    assert(expected -> true , actual -> ClassWithField.statValue == 2)
                                }
                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                  }
                            }
                            """
    private static final globalLocal = """
                            GlobalLocal {
                                var x : Int

                                fn start(){
                                    x = 2;
                                    var x = 1;
                                    println(this.x);
                                    println(x);

                                    assert(expected -> true , actual -> x == 1);
                                    assert(expected -> true , actual -> this.x == 2);
                                }
                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
						"""
    private static final staticTest = """
                            StaticTest {

                                fn start(){
                                    println(java.lang.System.out.hashCode());
                                    com.kubadziworski.test.statics.ImportMethods.execute("Hello!!");
                                }
                            }
						"""
    private static final staticFunctionTest = """
                            StaticFunctionTest {

                                fn start(){
                                    assert(expected -> true , actual -> 1 == 1);
                                    this.assert(expected -> true , actual -> 1 == 1);
                                }
                                static fn assert(actual: Boolean , expected: Boolean):Unit {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
						"""
    private static final importingTest = """
                            import com.kubadziworski.test.statics.ImportMethods.*;
                            ImportingTest {

                                fn start(){
                                    execute("hello");
                                    var myStuff = 1
                                    println(myStuff)
                                    println(statField)
                                }

                            }
						"""
    private final static getterSetter =
            """
                            GetterSetter {
                                var myField : Int
                                get(){
                                    println("returning value getter")
                                    println(field)
                                    return field;
                                }
                                set(value){
                                    println("setting value")
                                    field = value
                                    println(field)
                                }
                                fn start {
                                    myField = 5
                                    var result = myField == 5
                                    assert(result, true)
                                }
                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
							"""
    private final static getterStatement =
            """
                            GetterStatement {
                                var myField : Int
                                get() = field
                                set(value){
                                    println("setting value")
                                    field = value
                                    println(field)
                                }
                                fn start {
                                    myField = 5
                                    var result = myField == 5
                                    assert(result, true)
                                }
                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
							"""

    private final static functionSingleStatements =
            """
                            FunctionSingleStatements {

                                fn start {
                                    println(singleIntFunction())
                                    assert(singleIntFunction() == 300, true)
                                    loggingFunction("OK")
                                }

                                fn loggingFunction(stuff: String) = println(stuff)
                                fn singleIntFunction():Int = 300

                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
							"""

    private final static multiFiles =
            """
                            FirstClass {
                                fn start {
                                    println("Hello First Class")
                                }

                            }
                            SecondClass {
                                fn start {
                                   println("Hello Second Class")
                                }
                            }
							"""
    private final static ifExpressions =
            """
                            IfExpression {
                            
                                fn start {
                                    var shouldBeFive = if(true){
                                        5
                                    } else {
                                        6
                                    }
                                    assert(shouldBeFive == 5, true)
                                    var shouldBeSix = if(false){
                                        5
                                    } else {
                                        6
                                    }
                                    assert(shouldBeSix == 6, true)
                            
                                    var shouldBeTwo = if(false){
                                        1
                                    }else if(true){
                                        2
                                    }else {
                                        3
                                    }
                                    assert(shouldBeTwo == 2, true)
                            
                                    var shouldBeEight = if true 8 else 9
                                    assert(shouldBeEight == 8, true)
                                }
                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
							"""
    private static final myTryStatement = """
                            TryStatement {
                                var myTrue : Boolean = true
                            
                                fn start(){
                                    process();
                                    val y = testFinalBlocks();
                                    println(y)
                            
                                    println(finReturn())
                                }
                            
                                fn finReturn() : Int{
                            
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
                            
                                fn throwingMethod() : Int{
                                    throw new RuntimeException()
                                }
                            
                                fn testFinalBlocks(): Int{
                            
                                   try {
                                        if(true){
                                            println("return 1")
                                            return 1;
                                        }
                                        println("return 2")
                                        return 2;
                                   }
                                   catch(e:RuntimeException){
                                        println("return 3")
                                        return 3;
                                   }catch(e: Exception){
                                     println("return 5")
                                     return 5;
                                   }finally {
                                        println("final block")
                                   }
                            
                                }
                            
                                fn process(){
                                    try {
                                        throw new RuntimeException()
                                    } catch (e:RuntimeException){
                                        println("OK")
                                    }
                                    
                                    try {
                                       throw new RuntimeException()
                                    } catch (e:RuntimeException){
                                        println("OK")
                                    }catch(e:Exception){
                                        println("FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                    try {
                                        throw new RuntimeException()
                                    } catch (e:RuntimeException){
                                        println("OK")
                                    }catch(e:Exception){
                                        println("FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }finally{
                                        println("finally called OK")
                                    }
                                }
                            
                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
						"""
    private static final fieldInitializing =
            """
                            FieldInitializing {
                                val myField : Int = 10
                            
                                fn start {
                                    var result = myField == 10
                                    assert(result, true)
                                }
                                fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
							"""

    private final static fieldInitializingWithConstructor =
            """
                            FieldInitializingWithConstructor {
                                val myField : Int = 10
                            
                                fn FieldInitializingWithConstructor() {
                                    println(myField)
                                }
                            
                                fn start() {
                                    var result = myField == 10
                                    assert(result, true)
                                }
                            
                                fn assert(actual: Boolean , expected: Boolean) {
                                        if (actual == expected) {
                                            println("OK")
                                        }
                                        else {
                                            println("TEST FAILED")
                                            throw new AssertionError("TEST FAILED")
                                        }
                                }
                            }
            """
    private static final detectReturnCompleteStatement =
            """
                    DetectReturnCompleteStatement {
                        val myField : Int = 10

                        fn start(){
                            assert(foo(), true);
                        }

                        fn foo() : Boolean{
                            if(myField == 10){
                                return true;
                            }else {
                                return false;
                            }
                        }
                         fn assert(actual: Boolean , expected: Boolean) {
                                if (actual == expected) {
                                    println("OK")
                                }
                                else {
                                    println("TEST FAILED")
                                    throw new AssertionError("TEST FAILED")
                                }
                         }
                    }
            """

    private static final throwStatement =
            """
                    ThrowStatement {

                        fn start(){
                            try {
                                throw new RuntimeException();
                            }catch(e : Exception){
                                println("CATCH SUCCESS")
                            }
                        }

                    }
            """

    private static final nullValue =
            """
                    NullValue {
                        fn start(){
                            var nullableString:String? = null
                            println(nullableString)

                          var tryResult = try {
                                throw new NullPointerException();
                            }catch(e: NullPointerException){
                                println("PASS - NullPointerCatch")
                            }catch(e:Exception){
                                println("FAIL")
                                throw new AssertionError("TEST FAILED")
                            }

                            println(tryResult)
                        }
                    }
            """

    private static final returnUnit =
            """
                ReturnUnit {

                    fn start{
                        var x = voidMethod();
                        println(x)
                        var y = returnConcreteUnit();
                        println(y)
                        //TODO FIX THIS
                        //println(returnConcreteUnitByReference())
                    }

                    fn voidMethod(){
                        println("Called method without return")
                    }
                    fn returnConcreteUnit(){
                        return Unit.INSTANCE
                    }
                    fn returnConcreteUnitByReference(){
                        var x = Unit.INSTANCE
                        return x
                    }
                }
            """
    private static final concreteReturnUnit =
            """
                ConcreteReturnUnit {

                    fn start{
                        var x = nullableUnit();
                        println(x!!)
                    }

                    fn nullableUnit(): Unit?{
                        var x = Unit.INSTANCE
                        return x
                    }

                }
            """
    private static final superCall = """
               CallParentClass {

                   fn start(){
                        println(toString());
                        var superString = super.toString();
                        assert(superString.contains("myToString"), false)
                        assert(toString().contains("CallParentClass"), true)
                   }

                   fn toString(): String {
                      return "myToString :: " + super.toString()
                   }

                   fn assert(actual: Boolean , expected: Boolean) {
                        if (actual == expected) {
                            println("OK")
                        }
                        else {
                            println("TEST FAILED")
                            throw new AssertionError("TEST FAILED")
                        }
                   }
                }
            """

    private static final typeCoercion = """
                import com.kubadziworski.test.coercion.TypeCoercionTest;
                TypeCoercion {

                    fn start(){
                        var typeCoercionTest = new TypeCoercionTest();
                        var number:Int? = typeCoercionTest.objectInt();

                        var sum = number!! + 1.5
                        println(sum)
                        number = null
                        typeCoercionTest.returnString("foo")
                    }
                }
            """

    private static final primitiveFunctions = """
                PrimitiveFunctions {

                    fn start(){

                       val myVal:Int = 1.plus(3)
                       println(myVal.toString())
                       assert(myVal == 4, true)

                       val newVal = myVal.minus(3)
                       assert(newVal == 1, true)

                       println(2 > 1.toLong())
                    }
                    
                    fn assert(actual: Boolean , expected: Boolean) {
                        if (actual == expected) {
                            println("OK")
                        }
                        else {
                            println("TEST FAILED")
                            throw new AssertionError("TEST FAILED")
                        }
                    }
                }
            """
    private static final innerTry = """
                InnerTry{
                    var myCondition: Boolean = true
                    var mySecondCondition: Boolean = false
                    
                   fn start(){
                        println(testTry())
                        assert(testTry() == 5, true)
                        
                        myCondition = false
                        println(testTry())
                        assert(testTry() == 1, true)
                        
                        mySecondCondition = true
                        println(testTry())
                        assert(testTry() == 3, true)
                    }
                    
                    fn testTry():Int {
                        try {
                            try {
                                return 1
                            } catch(e: Exception) {
                                return 2
                            } finally {
                                if (mySecondCondition) {
                                    return 3
                                }
                            }
                        } catch(e: Exception) {
                            return 4
                        } finally {
                            if (myCondition) {
                                return 5
                            }
                        }
                    }
                    
                    fn assert(actual: Boolean , expected: Boolean) {
                            if (actual == expected) {
                                println("OK")
                            }
                            else {
                                println("TEST FAILED")
                                throw new AssertionError("TEST FAILED")
                            }
                    }               
                }
            """
    private static final parenthesisExpressions = """
                            ParenthesisExpressions {

                                fn start(){
                                    val sumNoParenthesis = 5 + 2 * 10;
                                    val sumParenthesis = (5 + 2) * 10;
                                    
                                    assert(expected -> true , actual -> sumNoParenthesis == 25);
                                    assert(expected -> true , actual -> sumParenthesis == 70);
                                }
                                 fn assert(actual: Boolean , expected: Boolean) {
                                    if (actual == expected) {
                                        println("OK")
                                    }
                                    else {
                                        println("TEST FAILED")
                                        throw new AssertionError("TEST FAILED")
                                    }
                                }
                            }
                        """
    private final static inlineCode =
            """
                            import com.kubadziworski.test.minline.InlinedClass;
                            InlineCode {

                                fn start {
                                    var x = new InlinedClass();
                                    x.inlinedNonStatic("hello world!")
                                    InlinedClass.inlinedStatic(this.toString())
                                }
                            }
							"""

    private final static callStaticImports =
            """
                            import com.kubadziworski.test.statics.*;
                            CallStaticImports {

                                fn start {
                                    var x = new ClassWithStatics();
                                    x.nonStaticMethod("hello world!")
                                    myMethod()
                                }
                                
                                fn myMethod() {
                                    ClassWithStatics.staticMethod(this.toString())
                                }
                            }
							"""


    private final static variableEscaping =
            """
                            `VariableEscaping` {

                                fn `start` {
                                    var x:`String` = "Enkel"
                                    var `y` = `x`
                                    
                                    `println`("not \$x reserved")
                                    `println`("not \${`y`} reserved")
                                    `println`("not \${y} reserved")
                                    var `import` = "reservedVarName"
                                    `println`("not \$import world!")
                                }
                            }
							"""

    private final static numericLiterals =
            """
                            NumericLiterals {

                                fn start {
                                    println(03_4);
                                    println(0x0E);
                                    println(0B01_011);
                                    println(49_37_28);
                            
                                    println(034L);
                                    println(0x0EL);
                                    println(0B0_10_11L);
                                    println(49_37_28L);
                            
                                    println(2.2250738585072014E-308);
                                    println(2.22507_385_8507_2014E-308);
                                    println(2.22_507_38_585);
                                    println(0x1.fffff_fff_fffffp1023);
                                    println(0x1.0P-10_74);    
                                }
                                  fn upperCast(){
                                    println("Decimal upper cast")
                                    println(2_147_483_647)
                                    println(2_147_483_648)
                                    println(2_147_483_648L)
                                    
                                    println("Binary upper cast")
                                    println(0B1111111111111111111111111111111)
                                    println(0B10000000000000000000000000000000)
                                    println(0B10000000000000000000000000000000L)
                                    
                                    println("Hexadecimal upper cast")
                                    println(0x7FFFFFFF)
                                    println(0x80000000)
                                    println(0x80000000L)
                                    
                                    println("Octal upper cast")
                                    println(017777777777)
                                    println(020000000000)
                                    println(020000000000L)
                                }
                            }
							"""
    private static final sendNullToMethod = """
                        SendNullToMethod {
                            fn start() {
                                var nullString: String? = null
                                method(nullString)
                                method(null)
                                var notNull: String = "notNull"
                                method(notNull)
                                var nullableNotNull: String? = "nullableNotNull"
                                method(nullableNotNull)
                            }

                            fn method(x: String?) {
                                println(x)
                            }
                        }

    """

}
