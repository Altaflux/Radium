package com.kubadziworski.parsing.visitor.phase;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.resolver.ImportResolver;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class ClassDeclarationVisitor extends EnkelBaseVisitor<Scope> {

    private final List<EnkelParser.ImportDeclarationContext> importDeclarationContexts;
    private final GlobalScope globalScope;
    private final String packageDeclaration;

    public ClassDeclarationVisitor(List<EnkelParser.ImportDeclarationContext> importDeclarationContexts, String packageDeclaration, GlobalScope globalScope) {
        this.importDeclarationContexts = importDeclarationContexts;
        this.globalScope = globalScope;
        this.packageDeclaration = packageDeclaration;
    }

    @Override
    public Scope visitClassDeclaration(@NotNull EnkelParser.ClassDeclarationContext ctx) {

        ImportResolver importResolver = new ImportResolver(importDeclarationContexts, globalScope);
        if (StringUtils.isNotEmpty(packageDeclaration)) {
            return new Scope(new MetaData(ctx.className().getText(), packageDeclaration), importResolver);
        }
        return new Scope(new MetaData(ctx.className().getText(), ""), importResolver);
    }
}
