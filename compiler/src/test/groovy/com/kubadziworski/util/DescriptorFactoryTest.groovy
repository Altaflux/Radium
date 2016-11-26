package com.kubadziworski.util

import com.kubadziworski.domain.Function
import com.kubadziworski.domain.node.expression.Parameter
import com.kubadziworski.domain.scope.FunctionSignature
import com.kubadziworski.domain.type.DefaultTypes
import com.kubadziworski.domain.type.JavaClassType
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes
/**
 * Created by kuba on 10.04.16.
 */
class DescriptorFactoryTest extends spock.lang.Specification {
    def "test descriptor factory with function"() {
        given:
            Function function = Mock(Function)
            Parameter param = new Parameter("param", paramType,null)

        when:
            def descr = DescriptorFactory.getMethodDescriptor(function)

        then:
            1 * function.getParameters() >> [param]
            1 * function.getReturnType() >> retType

        expect: "function descriptor should be equal to the expected"
            descr.equals(descriptor)

        where:
            paramType                             | retType             | descriptor
            PrimitiveTypes.INT_TYPE               | PrimitiveTypes.INT_TYPE     | "(I)I"
            DefaultTypes.STRING                   | DefaultTypes.STRING | "(Ljava/lang/String;)Ljava/lang/String;"
            new JavaClassType("java.lang.String") | PrimitiveTypes.INT_TYPE     | "(Ljava/lang/String;)I"
    }

    def "test descriptor factory with signature"() {
        given:
            Parameter param = new Parameter("param", paramType,null)
            FunctionSignature signature = Mock(FunctionSignature)

        when:
            def descr = DescriptorFactory.getMethodDescriptor(signature)

        then:
            1 * signature.getParameters() >> [param]
            1 * signature.getReturnType() >> retType

        expect: "function descriptor should be equal to the expected"
            descr.equals(descriptor)

        where:
            paramType                             | retType             | descriptor
            PrimitiveTypes.INT_TYPE               | PrimitiveTypes.INT_TYPE     | "(I)I"
            DefaultTypes.STRING                   | DefaultTypes.STRING | "(Ljava/lang/String;)Ljava/lang/String;"
            new JavaClassType("java.lang.String") | PrimitiveTypes.INT_TYPE     | "(Ljava/lang/String;)I"
    }
}
