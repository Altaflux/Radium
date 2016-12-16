package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.node.expression.trycatch.TryCatchExpression;
import com.kubadziworski.domain.node.statement.*;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.commons.InstructionAdapter;


public class StatementGeneratorFilter implements StatementGenerator {

    protected final StatementGenerator next;
    private final StatementGenerator parent;
    private final Scope scope;

    public StatementGeneratorFilter(InstructionAdapter methodVisitor, Scope scope) {
        this.parent = null;
        this.scope = scope;
        this.next = new BaseStatementGenerator(this, methodVisitor);
    }

    protected StatementGeneratorFilter(StatementGenerator parent, StatementGenerator next, Scope scope) {
        this.parent = parent;
        this.scope = scope;
        this.next = next.copy(this);
    }

    @Override
    final public void generate(NotNullCastExpression castExpression) {
        if (parent != null) {
            parent.generate(castExpression);
        } else {
            generate(castExpression, this);
        }
    }

    @Override
    final public void generate(ThrowStatement throwStatement) {
        if (parent != null) {
            parent.generate(throwStatement);
        } else {
            generate(throwStatement, this);
        }
    }

    @Override
    final public void generate(TryCatchStatement tryCatchStatement) {
        if (parent != null) {
            parent.generate(tryCatchStatement);
        } else {
            generate(tryCatchStatement, this);
        }
    }

    @Override
    final public void generate(TryCatchExpression tryCatchExpression) {
        if (parent != null) {
            parent.generate(tryCatchExpression);
        } else {
            generate(tryCatchExpression, this);
        }
    }


    @Override
    final public void generate(BlockExpression blockExpression) {
        if (parent != null) {
            parent.generate(blockExpression);
        } else {
            generate(blockExpression, this);
        }
    }

    @Override
    final public void generate(IfExpression ifExpression) {
        if (parent != null) {
            parent.generate(ifExpression);
        } else {
            generate(ifExpression, this);
        }
    }

    @Override
    final public void generate(UnaryExpression unaryExpression) {
        if (parent != null) {
            parent.generate(unaryExpression);
        } else {
            generate(unaryExpression, this);
        }
    }

    @Override
    final public void generate(PrintStatement printStatement) {
        if (parent != null) {
            parent.generate(printStatement);
        } else {
            generate(printStatement, this);
        }
    }

    @Override
    final public void generate(VariableDeclaration variableDeclaration) {
        if (parent != null) {
            parent.generate(variableDeclaration);
        } else {
            generate(variableDeclaration, this);
        }
    }

    @Override
    final public void generate(DupExpression dupExpression) {
        if (parent != null) {
            parent.generate(dupExpression);
        } else {
            generate(dupExpression, this);
        }
    }

    @Override
    final public void generate(IncrementDecrementExpression incrementDecrementExpression) {
        if (parent != null) {
            parent.generate(incrementDecrementExpression);
        } else {
            generate(incrementDecrementExpression, this);
        }
    }

    @Override
    final public void generate(FunctionCall functionCall) {
        if (parent != null) {
            parent.generate(functionCall);
        } else {
            generate(functionCall, this);
        }
    }

    @Override
    final public void generate(ReturnStatement returnStatement) {
        if (parent != null) {
            parent.generate(returnStatement);
        } else {
            generate(returnStatement, this);
        }
    }

    @Override
    final public void generate(IfStatement ifStatement) {
        if (parent != null) {
            parent.generate(ifStatement);
        } else {
            generate(ifStatement, this);
        }
    }

    @Override
    final public void generate(Block block) {
        if (parent != null) {
            parent.generate(block);
        } else {
            generate(block, this);
        }
    }

    @Override
    final public void generate(RangedForStatement rangedForStatement) {
        if (parent != null) {
            parent.generate(rangedForStatement);
        } else {
            generate(rangedForStatement, this);
        }
    }

    @Override
    final public void generate(Assignment assignment) {
        if (parent != null) {
            parent.generate(assignment);
        } else {
            generate(assignment, this);
        }
    }

    @Override
    final public void generate(FieldAssignment assignment) {
        if (parent != null) {
            parent.generate(assignment);
        } else {
            generate(assignment, this);
        }
    }

    @Override
    final public void generate(SuperCall superCall) {
        if (parent != null) {
            parent.generate(superCall);
        } else {
            generate(superCall, this);
        }
    }

    @Override
    final public void generate(ConstructorCall constructorCall) {
        if (parent != null) {
            parent.generate(constructorCall);
        } else {
            generate(constructorCall, this);
        }
    }

    @Override
    final public void generate(Argument argument) {
        if (parent != null) {
            parent.generate(argument);
        } else {
            generate(argument, this);
        }
    }


    @Override
    final public void generate(Addition addition) {
        if (parent != null) {
            parent.generate(addition);
        } else {
            generate(addition, this);
        }
    }

    @Override
    final public void generate(Parameter parameter) {
        if (parent != null) {
            parent.generate(parameter);
        } else {
            generate(parameter, this);
        }
    }

    @Override
    final public void generate(ConditionalExpression conditionalExpression) {
        if (parent != null) {
            parent.generate(conditionalExpression);
        } else {
            generate(conditionalExpression, this);
        }
    }


    @Override
    final public void generate(Value value) {
        if (parent != null) {
            parent.generate(value);
        } else {
            next.generate(value, this);
        }
    }


    @Override
    final public void generate(EmptyExpression emptyExpression) {
        if (parent != null) {
            parent.generate(emptyExpression);
        } else {
            generate(emptyExpression, this);
        }
    }

    @Override
    final public void generate(LocalVariableReference localVariableReference) {
        if (parent != null) {
            parent.generate(localVariableReference);
        } else {
            generate(localVariableReference, this);
        }
    }

    @Override
    final public void generate(FieldReference fieldReference) {
        if (parent != null) {
            parent.generate(fieldReference);
        } else {
            generate(fieldReference, this);
        }
    }

    @Override
    final public void generateDup(FieldReference fieldReference) {
        if (parent != null) {
            parent.generateDup(fieldReference);
        } else {
            generateDup(fieldReference, this);
        }
    }

    @Override
    final public void generate(PopExpression popExpression) {
        if (parent != null) {
            parent.generate(popExpression);
        } else {
            generate(popExpression, this);
        }
    }

    /////////////
    @Override
    public void generate(NotNullCastExpression castExpression, StatementGenerator statementGenerator) {
        next.generate(castExpression, statementGenerator);
    }

    @Override
    public void generate(ThrowStatement throwStatement, StatementGenerator statementGenerator) {
        next.generate(throwStatement, statementGenerator);
    }

    @Override
    public void generate(TryCatchStatement tryCatchStatement, StatementGenerator generator) {
        next.generate(tryCatchStatement, generator);
    }

    @Override
    public void generate(TryCatchExpression tryCatchExpression, StatementGenerator generator) {
        next.generate(tryCatchExpression, generator);
    }

    @Override
    public void generate(BlockExpression blockExpression, StatementGenerator generator) {
        next.generate(blockExpression, generator);
    }

    @Override
    public void generate(IfExpression ifExpression, StatementGenerator generator) {
        next.generate(ifExpression, generator);
    }

    @Override
    public void generate(UnaryExpression unaryExpression, StatementGenerator generator) {
        next.generate(unaryExpression, generator);
    }

    @Override
    public void generate(PrintStatement printStatement, StatementGenerator generator) {
        next.generate(printStatement, generator);
    }

    @Override
    public void generate(VariableDeclaration variableDeclaration, StatementGenerator generator) {
        next.generate(variableDeclaration, generator);
    }

    @Override
    public void generate(DupExpression dupExpression, StatementGenerator generator) {
        next.generate(dupExpression, generator);
    }

    @Override
    public void generate(IncrementDecrementExpression incrementDecrementExpression, StatementGenerator generator) {
        next.generate(incrementDecrementExpression, generator);
    }

    @Override
    public void generate(FunctionCall functionCall, StatementGenerator generator) {
        next.generate(functionCall, generator);
    }

    @Override
    public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
        next.generate(returnStatement, generator);
    }

    @Override
    public void generate(IfStatement ifStatement, StatementGenerator generator) {
        next.generate(ifStatement, generator);
    }

    @Override
    public void generate(Block block, StatementGenerator generator) {
        next.generate(block, generator);
    }

    @Override
    public void generate(RangedForStatement rangedForStatement, StatementGenerator generator) {
        next.generate(rangedForStatement, generator);
    }

    @Override
    public void generate(Assignment assignment, StatementGenerator generator) {
        next.generate(assignment, generator);
    }

    @Override
    public void generate(FieldAssignment assignment, StatementGenerator generator) {
        next.generate(assignment, generator);
    }

    @Override
    public void generate(SuperCall superCall, StatementGenerator generator) {
        next.generate(superCall, generator);
    }

    @Override
    public void generate(ConstructorCall cons, StatementGenerator generator) {
        next.generate(cons, generator);
    }

    @Override
    public void generate(Argument argument, StatementGenerator generator) {
        next.generate(argument, generator);
    }

    @Override
    public void generate(Addition addition, StatementGenerator generator) {
        next.generate(addition, generator);
    }

    @Override
    public void generate(Parameter parameter, StatementGenerator generator) {
        next.generate(parameter, generator);
    }

    @Override
    public void generate(ConditionalExpression conditionalExpression, StatementGenerator generator) {
        next.generate(conditionalExpression, generator);
    }

    @Override
    public void generate(Value value, StatementGenerator generator) {
        next.generate(value, generator);
    }

    @Override
    public void generate(EmptyExpression emptyExpression, StatementGenerator generator) {
        next.generate(emptyExpression, generator);
    }

    @Override
    public void generate(LocalVariableReference localVariableReference, StatementGenerator generator) {
        next.generate(localVariableReference, generator);
    }

    @Override
    public void generate(FieldReference fieldReference, StatementGenerator generator) {
        next.generate(fieldReference, generator);
    }

    @Override
    public void generateDup(FieldReference fieldReference, StatementGenerator generator) {
        next.generateDup(fieldReference, generator);
    }

    @Override
    public void generate(PopExpression popExpression, StatementGenerator generator) {
        next.generate(popExpression, generator);
    }

    @Override
    public Scope getScope() {
        if (parent != null) {
            return parent.getScope();
        }
        return scope;
    }

    public StatementGenerator copy(StatementGenerator parent) {
        return new StatementGeneratorFilter(parent, this.next, scope);
    }
}
