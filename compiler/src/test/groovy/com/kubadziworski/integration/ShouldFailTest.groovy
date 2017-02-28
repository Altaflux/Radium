package com.kubadziworski.integration

import com.kubadziworski.compiler.Compiler
import com.kubadziworski.exception.FinalFieldModificationException
import com.kubadziworski.exception.IncompatibleTypesException
import com.kubadziworski.exception.MethodSignatureNotFoundException
import com.kubadziworski.exception.MissingReturnStatementException
import com.kubadziworski.exception.UnreachableStatementException
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
        incompatibleField       | "IncompatibleField.enk"       | IncompatibleTypesException.class
        incompatibleThrow       | "IncompatibleThrow.enk"       | IncompatibleTypesException.class
        assignNullToNotNullable | "AssignNullToNotNullable.enk" | IncompatibleTypesException.class
        modifyFinalVariable     | "ModifyFinalVariable.enk"     | FinalFieldModificationException.class
        modifyFinalField        | "ModifyFinalField.enk"        | FinalFieldModificationException.class
        fieldByConstructor      | "FieldByConstructor.enk"      | IncompatibleTypesException.class
    }


    private static final sendNullToMethod = """
                        SendNullToMethod {
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
                        
                        CallFunctionOfNullable {
                            fn start() {
                                var nullObject: SimpleObject? = null
                                nullObject.foo();
                            }
                        }
    """
    private static final notReturnComplete = """
                        NotReturnComplete {
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
                        UnreachableStatement {
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
                        IncompatibleLocalVariable {
                            fn start() {
                                var myString: String = 2;
                            }
                        }
    """
    private static final incompatibleField = """
                        IncompatibleField {
                            val myField : Int = "Foo"
                            fn start() {
                               
                            }
                        }
    """

    private static final incompatibleThrow = """
                        import com.kubadziworski.test.SimpleObject;
                        
                        IncompatibleThrow {
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

                        AssignNullToNotNullable {
                            fn start() {
                               var myString:String = null
                            }
                        }
    """

    private static final modifyFinalVariable = """

                        ModifyFinalVariable {
                            fn start() {
                               val myString:String = "String"
                               myString = "error"
                            }
                        }
    """

    private static final modifyFinalField = """
                        ModifyFinalField {
                            val myField : String = "Foo"
                        
                            fn start() {
                               myField = "error"
                            }
                        }
    """

    private final static fieldByConstructor =
            """
                            FieldByConstructor(var myString:String = 2) {
        
                            }
							"""
}
