package com.kubadziworski.exception;


import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.List;
import java.util.stream.Collectors;

public class AmbiguousCallException extends RuntimeException {

    public AmbiguousCallException(List<FunctionSignature> functionSignatures) {
        super(buildMessage(functionSignatures));
    }

    private static String buildMessage(List<FunctionSignature> functionSignatures) {
        StringBuilder builder = new StringBuilder("Ambiguous method call for methods: \n");
        for (FunctionSignature signature : functionSignatures) {
            builder.append(signature.getName()).append(" (")
                    .append(String.join(",", signature.getParameters()
                            .stream()
                            .map(parameter -> parameter.getType().readableString())
                            .collect(Collectors.toList()))).append(")\n");
        }
        return builder.toString();
    }
}
