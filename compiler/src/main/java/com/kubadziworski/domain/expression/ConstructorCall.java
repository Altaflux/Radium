package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 05.05.16.
 */
public class ConstructorCall implements Call {
    private final List<Expression> arguments;
    private Type type;
    private String identifier;

    public ConstructorCall(String identifier) {
        this(identifier, Collections.emptyList());
    }

    public ConstructorCall(String className, List<Expression> arguments) {
        this.type = new ClassType(className);
        this.arguments = arguments;
        this.identifier = className;
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }

    @Override
    public List<Expression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
