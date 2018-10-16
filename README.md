#Radium

[![Build Status](https://api.travis-ci.org/Altaflux/Radium.svg?branch=master)](https://travis-ci.org/JakubDziworski/Enkel-JVM-language)  [![Join the chat at https://gitter.im/JakubDziworski/Enkel-JVM-language](https://badges.gitter.im/JakubDziworski/Enkel-JVM-language.svg)](https://gitter.im/JakubDziworski/Enkel-JVM-language?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Radium is a simple programming language running on the  jvm.
It is based on the work of Jakub Dziworski
https://github.com/JakubDziworski/Enkel-JVM-language

## Syntax
* Class constructors are specified at class level
```
    class MyClass(x: Int) {
    }
```
* Main method is called "start" 
```
    class MyClass {
        fn start() {
            println("Hello World")
        }
    }
```

* Functions without input don't require parenthesis
```
    class MyClass {
        fn start {
            println("Hello World")
        }
    }
```


## Features implemented
* Unified primitives and boxed classes
```
    val myInt: Int = 25; //Translates to (int)
    val myInt: Int? = 25; //Translates to (Integer) for nullability
```
* Named Parameters
```
    fn start {
        functionWithNamedParameters(x1->25,x2->-25,y1->50,y2->-0xE)
    }

    fn functionWithNamedParameters (x1 :Int , y1: Int, x2: Int, y2:Int) {
        println("Created x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2)
    }
```
* Support for single statement/expression functions
```
    class FunctionSingleStatements {
    
        fn start {
            println(singleIntFunction())
            assert(singleIntFunction() == 300, true)
            loggingFunction("OK")
        }
    
        fn loggingFunction(stuff: String) = println(stuff)
        fn singleIntFunction():Int = 300
    }
```
* Default methods similar to the ones of Kotlin
```
    fn start() {
        myMethod("hello") //Prints hello
        myMethod() //Prints world
    }
    fn myMethod(x: String = "world") {
        println(x)
    }
```
* Nullable types
```
    var myField : Int? //Can be null
    var secondField : Int //Cannnot be null
    
    fn start {
        notNullMethod(null) //Invalid
        nullMethod(null) //Valid
    } 
    fn notNullMethod(x: Int) {
        println(x)
    }
    
    fn nullMethod(x: Int?) {
        println(x)
    }
```
* Method inlining with the "inline" modifier
* Full numeric literals support
```
                            class NumericLiterals {

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
```
* Operator overloading
```
    class StartPoint {
        fn start {
            val overLoad =  new ClassWithOverload("hello") + new ClassWithOverload("world")
        }
    }
    class ClassWithOverload(myField: String) {
       
       fn plus(toConcat: ClassWithOverload): ClassWithOverload {
            return new ClassWithOverload(myField + toConcat.myField)
       }
    }
```
* String interpolation
```
    class InterpolationClass {

        fn start {
            var x:String = "Radium"
            println("hello \$x world!") // hello Radium world!
            println("hello \${x} world!") // hello \${x} world! //Escaped from interpolation
        }
    }
```
* Try Catch Finally
```
    fn functionWithTryAndReturn(): Int {

        try {
            return throwingMethod();
        } catch(e: Exception){
            return 2
        } finally {
            if(true){
                return 1
            }
        }
    }
```
* If and Try/Catch statements are expressions
```
      // try/catch expression
      var tryResult = try {
            throw new NullPointerException();
            "notReturned"
        } catch(e: NullPointerException){
            "return from NullPointer"
        } catch(e:Exception){
            "Not returned as NullPointerException was already catched"
        }
        println(tryResult) //"return from NullPointer"
        
      // IF expression
      var shouldBeFive = if(true){
            5
        } else {
            6
        }
       println(shouldBeFive) //5
```
* Concept of Unit return type
```
    class ConcreteReturnUnit {

        fn start{
            var x = nullableUnit();
            println(x!!)
        }

        fn nullableUnit(): Unit?{
            var x = Unit.INSTANCE
            return x
        }

    }
```
* Automatic Getters and Setters as well as overriding them
```
    class GetterSetter {
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
```
* Primitives can call functions thru intrinsic methods
```
    class PrimitiveFunctions {

        fn start(){

           val myVal:Int = 1.plus(3)
           println(myVal.toString())
           assert(myVal == 4, true)

           val newVal = myVal.minus(3)
           assert(newVal == 1, true)

           println(2 > 1.toLong())
        }
        
    }
```
* Equality by == for objects and primitives
* Escaping language keywords thru Grave Accent
```
    class `VariableEscaping` {

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
```
* Elvis expression
```
    class ElvisExpression {

        fn start {
            assertTrue(input(null) == "wasNull")
            assertTrue(input("aString") == "aString")
        }
        fn input(a: Any?): Any {
            return a ?: "wasNull"
        }
    }
```
* Full compatibility with Java
* Files can have multiple classes declared
```
    //Same File
    class ClassOne {
    
    }
    class ClassTwo {
    
    }
```

### Pending to implement
* Abstract classes & interfaces
* Enums
* Generics
* Switch Statement
* Inner classes
* Type aliases
* Type system re-work required for generics support

## Compiling and running Radium scripts
1.Build compiler into executable jar

```bash
mvn clean package
```
2.Compile sample .enk file (You can find more examples in EnkelExamples directory)

```bash
java -classpath compiler/target/compiler-1.0-SNAPSHOT-jar-with-dependencies.jar:. com.kubadziworski.compiler.Compiler EnkelExamples/DefaultParamTest.enk
```

3.Run compiled .enk program

```bash
java DefaultParamTest
```
