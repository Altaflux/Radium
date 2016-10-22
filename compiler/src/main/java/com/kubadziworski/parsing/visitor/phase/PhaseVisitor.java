package com.kubadziworski.parsing.visitor.phase;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.CompilationData;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.CompilationUnitVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

import java.util.stream.Collectors;

public class PhaseVisitor {
    private final GlobalScope globalScope;

    public PhaseVisitor(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    private EnkelParserScope processClassDeclarations(CompilationData enkelParser) {
        EnkelParser.CompilationUnitContext context = enkelParser.getEnkelParser().compilationUnit();
        String packageDeclaration = "";
        EnkelParser.PackageDeclarationContext declarationContexts = context.packageDeclaration();
        if (declarationContexts != null) {
            packageDeclaration = declarationContexts.ID().stream().map(ParseTree::getText).collect(Collectors.joining("."));
        }
        ClassDeclarationVisitor classDeclarationVisitor = new ClassDeclarationVisitor(context.importDeclaration(), packageDeclaration, globalScope);
        Scope scope = context.classDeclaration().accept(classDeclarationVisitor);
        return new EnkelParserScope(enkelParser, scope);
    }

    private void processFieldDeclarations(CompilationData compilationData, Scope scope) {
        compilationData.getEnkelParser().reset();
        EnkelParser.CompilationUnitContext context = compilationData.getEnkelParser().compilationUnit();
        FieldPhaseVisitor fieldPhaseVisitor = new FieldPhaseVisitor(scope);
        context.classDeclaration().accept(fieldPhaseVisitor);
    }

    private void processMethodDeclarations(CompilationData compilationData, Scope scope) {
        compilationData.getEnkelParser().reset();
        EnkelParser.CompilationUnitContext context = compilationData.getEnkelParser().compilationUnit();
        MethodPhaseVisitor methodPhaseVisitor = new MethodPhaseVisitor(scope);
        context.classDeclaration().accept(methodPhaseVisitor);
    }

    private CompilationUnit processCompilationUnit(CompilationData compilationData, Scope scope) {
        compilationData.getEnkelParser().reset();
        CompilationUnitVisitor compilationUnitVisitor = new CompilationUnitVisitor(scope, compilationData.getFilePath());
        return compilationData.getEnkelParser().compilationUnit().accept(compilationUnitVisitor);
    }

    public List<CompilationUnit> processAllClasses(List<CompilationData> enkelParsers) {

        //Phase 1, loads to the Global Scope all the names of the Enkel files
        List<EnkelParserScope> parserScopes = enkelParsers
                .stream()
                .map(this::processClassDeclarations)
                .peek(scope -> globalScope.addScope(scope.scope.getFullClassName(), scope.scope))
                .collect(Collectors.toList());

        //Phase 2 resolve all class references of the ImportResolvers of each scope
        parserScopes.stream()
                .peek(enkelParserScope -> enkelParserScope.scope.getImportResolver().loadClassImports())
                .peek(enkelParserScope -> processFieldDeclarations(enkelParserScope.compilationData, enkelParserScope.scope))
                .forEach(enkelParserScope -> processMethodDeclarations(enkelParserScope.compilationData, enkelParserScope.scope));

        //Phase 3 resolve all static methods and field references of the ImportResolvers of each scope
        parserScopes.forEach(enkelParserScope -> enkelParserScope.scope.getImportResolver().loadMethodsAndFieldsImports());

        //Phase 4 process the compilation data, all imports should already be resolved by now.
        return parserScopes.stream()
                .map(enkelParserScope -> processCompilationUnit(enkelParserScope.compilationData, enkelParserScope.scope))
                .collect(Collectors.toList());
    }

    private static class EnkelParserScope {
        private final CompilationData compilationData;
        private final Scope scope;

        private EnkelParserScope(CompilationData compilationData, Scope scope) {
            this.compilationData = compilationData;
            this.scope = scope;
        }
    }
}
