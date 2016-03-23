package com.kubadziworski.parsing;

import com.kubadziworski.antlr.EnkelBaseListener;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.bytecodegeneration.CompilationUnit;
import com.kubadziworski.bytecodegeneration.ClassDeclaration;
import com.kubadziworski.bytecodegeneration.classscopeinstructions.ClassScopeInstruction;
import com.kubadziworski.bytecodegeneration.classscopeinstructions.PrintVariable;
import com.kubadziworski.bytecodegeneration.classscopeinstructions.VariableDeclaration;
import com.kubadziworski.parsing.domain.Variable;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by kuba on 16.03.16.
 */
public class EnkelTreeWalkListener extends EnkelBaseListener {

    Queue<ClassScopeInstruction> classScopeInstructions = new ArrayDeque<>();
    Map<String, Variable> variables = new HashMap<>();
    private CompilationUnit compilationUnit;
    private ClassDeclaration classDeclaration;
    public Queue<ClassScopeInstruction> getClassScopeInstructions() {
        return classScopeInstructions;
    }

    @Override
    public void exitVariable(@NotNull EnkelParser.VariableContext ctx) {
        final TerminalNode varName = ctx.ID();
        final EnkelParser.ValueContext varValue = ctx.value();
        final int varType = varValue.getStart().getType();
        final int varIndex = variables.size();
        final String varTextValue = varValue.getText();
        Variable var = new Variable(varIndex, varType, varTextValue);
        variables.put(varName.getText(), var);
        classScopeInstructions.add(new VariableDeclaration(var));
        logVariableDeclarationStatementFound(varName, varValue);
    }

    @Override
    public void exitPrint(@NotNull EnkelParser.PrintContext ctx) {
        final TerminalNode varName = ctx.ID();
        final boolean printedVarNotDeclared = !variables.containsKey(varName.getText());
        if (printedVarNotDeclared) {
            final String erroFormat = "ERROR: WTF? You are trying to print var '%s' which has not been declared!!!111. ";
            System.out.printf(erroFormat, varName.getText());
            return;
        }
        final Variable variable = variables.get(varName.getText());
        classScopeInstructions.add(new PrintVariable(variable));
        logPrintStatementFound(varName, variable);
    }

    @Override
    public void exitCompilationUnit(@NotNull EnkelParser.CompilationUnitContext ctx) {
        super.enterCompilationUnit(ctx);
        compilationUnit = new CompilationUnit(classDeclaration);
    }

    @Override
    public void exitClassDeclaration(@NotNull EnkelParser.ClassDeclarationContext ctx) {
        super.enterClassDeclaration(ctx);
        final String className = ctx.className().getText();
        classDeclaration = new ClassDeclaration(classScopeInstructions,className);
    }

    private void logVariableDeclarationStatementFound(TerminalNode varName, EnkelParser.ValueContext varValue) {
        final int line = varName.getSymbol().getLine();
        final String format = "OK: You declared variable named '%s' with value of '%s' at line '%s'.%n";
        System.out.printf(format, varName, varValue.getText(), line);
    }

    private void logPrintStatementFound(TerminalNode varName, Variable variable) {
        final int line = varName.getSymbol().getLine();
        final String format = "OK: You instructed to print variable '%s' which has value of '%s' at line '%s'.'%n";
        System.out.printf(format,variable.getId(),variable.getValue(),line);
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
}
