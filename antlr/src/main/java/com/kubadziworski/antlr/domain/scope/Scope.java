package com.kubadziworski.antlr.domain.scope;

import com.google.common.collect.Lists;
import com.kubadziworski.antlr.exceptions.NoIdentifierFound;
import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.expression.Identifier;
import com.kubadziworski.antlr.domain.global.MetaData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kuba on 02.04.16.
 */
public class Scope {
    private List<Identifier> identifiers;
    private List<FunctionSignature> functionSignatures;
    private final MetaData metaData;

    public Scope(MetaData metaData) {
        this.identifiers = new ArrayList<>();
        this.functionSignatures = new ArrayList<>();
        this.metaData = metaData;
    }

    public Scope(Scope scope) {
        this.metaData = scope.metaData;
        this.identifiers = Lists.newArrayList(scope.identifiers);
        this.functionSignatures = Lists.newArrayList(scope.functionSignatures);
    }

    public void addSignature(FunctionSignature signature) {
        functionSignatures.add(signature);
    }

    public Expression getExpression(String identifierName) {
        return identifiers.stream()
                .filter(identifier -> identifier.getName().equals(identifierName))
                .map(Identifier::getExpression)
                .findFirst()
                .orElseThrow(() -> new NoIdentifierFound(this,identifierName));
    }

    public <T extends Expression> T getExpression(String identifierName,Class<T> requiredExpression) {
        return identifiers.stream()
                .filter(identifier -> identifier.getName().equals(identifierName))
                .map(Identifier::getExpression)
                .filter(requiredExpression::isInstance)
                .map(expresssion -> (T)expresssion)
                .findFirst()
                .orElseThrow(() -> new NoIdentifierFound(this,identifierName));
    }

    public FunctionSignature getSignatureForName(String methodName) {
        return functionSignatures.stream()
                .filter(signature -> signature.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("nie istenije"));
    }

    public void addIdentifier(Identifier identifier) {
        identifiers.add(identifier);
    }

    public Identifier getIdentifier(String text) {
        return identifiers.stream()
                .filter(identifier -> identifier.getName().equals(text))
                .findFirst()
                .orElseThrow(() -> new NoIdentifierFound(null,text));
    }

    public String getClassName() {
        return metaData.getClassName();
    }

    public Collection<Identifier> getIdentifiers() {
        return identifiers;
    }
}
