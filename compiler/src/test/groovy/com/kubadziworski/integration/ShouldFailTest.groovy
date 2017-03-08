package com.kubadziworski.integration

import com.kubadziworski.compiler.Compiler
import com.kubadziworski.exception.*
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification
import spock.lang.Unroll

class ShouldFailTest extends Specification {

    private final static Compiler compiler = new Compiler()
    private static final Logger LOGGER = LoggerFactory.getLogger(ShouldFailTest.class)

    def setup() {
        new File("target/enkelClasses/shouldFail/").mkdirs()
    }

    @Unroll
    "Should Not Compile"() {
        given:
        def file = new File("target/enkelClasses/shouldFail/" + filename)
        FileUtils.writeStringToFile(file, code)
        Exception foundException = null
        try {
            compiler.compile("target/enkelClasses/shouldFail/" + filename)
        } catch (Exception e) {
            LOGGER.info("Expected exception caught: \n{} \n{}", e.class.typeName, e.getMessage())
            foundException = e
        }
        expect:
        exception == foundException.class

        where:
        code                      | filename                        | exception
        sendNullToMethod          | "SendNullToMethod.enk"          | MethodSignatureNotFoundException.class
        callFunctionOfNullable    | "CallFunctionOfNullable.enk"    | MethodSignatureNotFoundException.class
        notReturnComplete         | "NotReturnComplete.enk"         | MissingReturnStatementException.class
        unreachableStatement      | "UnreachableStatement.enk"      | UnreachableStatementException.class
        incompatibleLocalVariable | "IncompatibleLocalVariable.enk" | IncompatibleTypesException.class
        incompatibleField         | "IncompatibleField.enk"         | IncompatibleTypesException.class
        incompatibleThrow         | "IncompatibleThrow.enk"         | IncompatibleTypesException.class
        assignNullToNotNullable   | "AssignNullToNotNullable.enk"   | IncompatibleTypesException.class
        modifyFinalVariable       | "ModifyFinalVariable.enk"       | FinalFieldModificationException.class
        modifyFinalField          | "ModifyFinalField.enk"          | FinalFieldModificationException.class
        fieldByConstructor        | "FieldByConstructor.enk"        | IncompatibleTypesException.class
        noAccessToMethod          | "NoAccessToMethod.enk"          | AccessException.class
    }

    private static final noAccessToMethod = """
                        import com.kubadziworski.test.NoAccess;
                        
                        class NoAccessToMethod {
                            fn start() {
                                val obj = new NoAccess();
                                obj.packagePrivate();
                            }
                        }
    """

    private static final sendNullToMethod = """
                        class SendNullToMethod {
                            fn start() {
                                var nullString: String? = null
                                method(nullString)
                            }

                            fn method(x: String) {
                                println(x)
                            }
                        }
    """

    private static final callFunctionOfNullable = """
                        import com.kubadziworski.test.SimpleObject;
                        
                        class CallFunctionOfNullable {
                            fn start() {
                                var nullObject: SimpleObject? = null
                                nullObject.foo();
                            }
                        }
    """
    private static final notReturnComplete = """
                        class NotReturnComplete {
                            fn start() {
                                method()
                            }

                            fn method():String {
                                if(false){
                                    return "Fail"
                                }
                            }
                        }
    """

    private static final unreachableStatement = """
                        class UnreachableStatement {
                            fn start() {
                                method()
                            }

                            fn method():String {
                                if(false){
                                    return "Correct"
                                }else {
                                    return "Correct too"
                                }
                                return "Fail"
                            }
                        }
    """

    private static final incompatibleLocalVariable = """
                        class IncompatibleLocalVariable {
                            fn start() {
                                var myString: String = 2;
                            }
                        }
    """
    private static final incompatibleField = """
                        class IncompatibleField {
                            val myField : Int = "Foo"
                            fn start() {
                               
                            }
                        }
    """

    private static final incompatibleThrow = """
                        import com.kubadziworski.test.SimpleObject;
                        
                        class IncompatibleThrow {
                            var myField : Int = "Foo"
                            fn start() {
                               method();
                            }
                            fn method(){
                                throw new SimpleObject();
                            }
                        }
    """

    private static final assignNullToNotNullable = """

                        class AssignNullToNotNullable {
                            fn start() {
                               var myString:String = null
                            }
                        }
    """

    private static final modifyFinalVariable = """

                        class ModifyFinalVariable {
                            fn start() {
                               val myString:String = "String"
                               myString = "error"
                            }
                        }
    """

    private static final modifyFinalField = """
                        class ModifyFinalField {
                            val myField : String = "Foo"
                        
                            fn start() {
                               myField = "error"
                            }
                        }
    """

    private final static fieldByConstructor = """

                            class FieldByConstructor(var myString:String = 2) {
        
                            }
							"""
}
