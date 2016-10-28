package com.kubadziworski.parsing.visitor.phase;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.CompilationData;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.resolver.ImportResolver;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.ClassVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import java.util.stream.Collectors;

public class PhaseVisitor {

    private final GlobalScope globalScope;

    public PhaseVisitor(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    private HolderOfHolders processClassDeclarations(CompilationData enkelParser) {
        EnkelParser.CompilationUnitContext context = enkelParser.getEnkelParser().compilationUnit();
        String packageDeclaration = "";
        EnkelParser.PackageDeclarationContext declarationContexts = context.packageDeclaration();
        if (declarationContexts != null) {
            packageDeclaration = declarationContexts.ID().stream().map(ParseTree::getText).collect(Collectors.joining("."));
        }
        String packageDeclaration2 = packageDeclaration;
        ImportResolver importResolver = new ImportResolver(context.importDeclaration(), globalScope);

        List<Holder> scopes = context.classDeclaration().stream().map(ctx -> {
            if (StringUtils.isNotEmpty(packageDeclaration2)) {
                return new PhaseVisitor.Holder(ctx, new Scope(new MetaData(ctx.className().getText(), packageDeclaration2), importResolver));
            } else {
                return new PhaseVisitor.Holder(ctx, new Scope(new MetaData(ctx.className().getText(), ""), importResolver));
            }
        }).collect(Collectors.toList());

        return new HolderOfHolders(importResolver, scopes, enkelParser.getFilePath());
    }


    private void processFieldDeclarations(Holder compilationData) {
        FieldPhaseVisitor fieldPhaseVisitor = new FieldPhaseVisitor(compilationData.scope);
        compilationData.ctx.accept(fieldPhaseVisitor);
    }

    private void processMethodDeclarations(Holder compilationData) {
        MethodPhaseVisitor methodPhaseVisitor = new MethodPhaseVisitor(compilationData.scope);
        compilationData.ctx.accept(methodPhaseVisitor);
    }

    private CompilationUnit processCompilationUnit(HolderOfHolders compilationData) {
        List<ClassDeclaration> classDeclaration = compilationData.holders.stream().map(holder -> {
            ClassVisitor classVisitor = new ClassVisitor(holder.scope);
            return holder.ctx.accept(classVisitor);
        }).collect(Collectors.toList());

        return new CompilationUnit(classDeclaration, compilationData.filePath);

    }

    public List<CompilationUnit> processAllClasses(List<CompilationData> enkelParsers) {

        //Phase 1, loads to the Global Scope all the names of the Enkel files
        List<HolderOfHolders> parserScopes = enkelParsers
                .stream()
                .map(this::processClassDeclarations)
                .peek(holderOfHolders -> holderOfHolders.holders.forEach(holder -> globalScope.addScope(holder.scope.getFullClassName(), holder.scope)))
                .collect(Collectors.toList());

        //Phase 2 resolve all class references of the ImportResolvers of each scope
        parserScopes.stream()
                .peek(holderOfHolders -> holderOfHolders.importResolver.loadClassImports())
                .peek(holderOfHolders -> holderOfHolders.holders.forEach(this::processFieldDeclarations))
                .forEach(holderOfHolders -> holderOfHolders.holders.forEach(this::processMethodDeclarations));

        //Phase 3 resolve all static methods and field references of the ImportResolvers of each scope
        parserScopes.forEach(holderOfHolders -> holderOfHolders.importResolver.loadMethodsAndFieldsImports());

        //Phase 4 process the compilation data, all imports should already be resolved by now.
        return parserScopes.stream()
                .map(this::processCompilationUnit)
                .collect(Collectors.toList());

    }

    private class HolderOfHolders {
        private final ImportResolver importResolver;
        private final List<Holder> holders;
        private final String filePath;

        HolderOfHolders(ImportResolver importResolver, List<Holder> holders, String filePath) {
            this.importResolver = importResolver;
            this.holders = holders;
            this.filePath = filePath;
        }
    }

    private static class Holder {
        private final EnkelParser.ClassDeclarationContext ctx;
        private final Scope scope;


        Holder(EnkelParser.ClassDeclarationContext ctx, Scope scope) {
            this.ctx = ctx;
            this.scope = scope;
        }
    }

}
