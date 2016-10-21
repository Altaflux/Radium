package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ClassDeclarationContext;
import com.kubadziworski.antlr.EnkelParser.CompilationUnitContext;
import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.scope.Scope;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Created by kuba on 28.03.16.
 */
public class CompilationUnitVisitor extends EnkelBaseVisitor<CompilationUnit> {

    private final Scope scope;
    private final String filePath;

    public CompilationUnitVisitor(Scope scope, String filePath) {
        this.scope = scope;
        this.filePath = filePath;
    }

    @Override
    public CompilationUnit visitCompilationUnit(@NotNull CompilationUnitContext ctx) {

        ClassDeclarationContext classDeclarationContext = ctx.classDeclaration();
        ClassVisitor classVisitor = new ClassVisitor(scope);
        ClassDeclaration classDeclaration = classDeclarationContext.accept(classVisitor);
        return new CompilationUnit(classDeclaration, filePath);
    }


}
