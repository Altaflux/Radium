package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ClassDeclarationContext;
import com.kubadziworski.antlr.domain.global.CompilationUnit;
import com.kubadziworski.antlr.domain.global.ClassDeclaration;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Created by kuba on 28.03.16.
 */
public class CompilationUnitVisitor extends EnkelBaseVisitor<CompilationUnit> {

    @Override
    public CompilationUnit visitCompilationUnit(@NotNull EnkelParser.CompilationUnitContext ctx) {
        String classsName = ctx.classDeclaration().className().getText();
        ClassVisitor classVisitor = new ClassVisitor();
        ClassDeclarationContext classDeclarationContext = ctx.classDeclaration();
        ClassDeclaration classDeclaration = classDeclarationContext.accept(classVisitor);
        return new CompilationUnit(classDeclaration);
    }
}
