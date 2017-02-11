package com.kubadziworski.parsing.visitor.phase;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.CompilationData;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.resolver.ImportResolver;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.exception.ClassNotFoundForNameException;
import com.kubadziworski.parsing.visitor.ClassVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PhaseVisitor {

    private final GlobalScope globalScope;

    public PhaseVisitor(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    private Triple<ImportResolver, List<Triple<EnkelParser.ClassDeclarationContext, String, String>>, String> processClassNames(CompilationData enkelParser) {

        EnkelParser.CompilationUnitContext context = enkelParser.getEnkelParser().compilationUnit();
        String packageDeclaration = "";
        EnkelParser.PackageDeclarationContext declarationContexts = context.packageDeclaration();
        if (declarationContexts != null) {
            packageDeclaration = declarationContexts.ID().stream().map(ParseTree::getText).collect(Collectors.joining("."));
        }
        ImportResolver importResolver = new ImportResolver(context.importDeclaration(), globalScope);
        String packageDeclaration2 = packageDeclaration;
        List<Triple<EnkelParser.ClassDeclarationContext, String, String>> classes = context.classDeclaration().stream().map(ctx -> {
            if (StringUtils.isNotEmpty(packageDeclaration2)) {
                return new Triple<>(ctx, ctx.className().getText(), packageDeclaration2);
            } else {
                return new Triple<>(ctx, ctx.className().getText(), "");
            }
        }).collect(Collectors.toList());
        return new Triple<>(importResolver, classes, enkelParser.getFilePath());
    }

    private Triple<ImportResolver, List<Holder>, String> processClassDeclarations(Triple<ImportResolver, List<Triple<EnkelParser.ClassDeclarationContext, String, String>>,
            String> enkelParser) {
        ImportResolver importResolver = enkelParser.value1;
        List<Holder> scopes = enkelParser.value2.stream().map(holderOfClasses -> holderOfClasses)
                .map(ctx -> {
                    String superClass = importResolver.getClassName("Any")
                            .orElseThrow(() -> new ClassNotFoundForNameException("Any"));
                    return new Holder(ctx.value1, new Scope(new MetaData(ctx.value2, ctx.value3, superClass, Collections.emptyList()),
                            importResolver));
                }).collect(Collectors.toList());
        return new Triple<>(importResolver, scopes, enkelParser.value3);
    }


    private void processFieldDeclarations(Holder compilationData) {
        FieldPhaseVisitor fieldPhaseVisitor = new FieldPhaseVisitor(compilationData.scope);
        compilationData.ctx.accept(fieldPhaseVisitor);
    }

    private void processMethodDeclarations(Holder compilationData) {
        MethodPhaseVisitor methodPhaseVisitor = new MethodPhaseVisitor(compilationData.scope);
        compilationData.ctx.accept(methodPhaseVisitor);
    }

    private CompilationUnit processCompilationUnit(Triple<ImportResolver, List<Holder>, String> compilationData) {
        List<ClassDeclaration> classDeclaration = compilationData.value2.stream().map(holder -> {
            ClassVisitor classVisitor = new ClassVisitor(holder.scope);
            return holder.ctx.accept(classVisitor);
        }).collect(Collectors.toList());

        return new CompilationUnit(classDeclaration, compilationData.value3,
                classDeclaration.stream().findAny().map(ClassDeclaration::getClassPackage).filter(Objects::nonNull).orElse(""));

    }

    public List<CompilationUnit> processAllClasses(List<CompilationData> enkelParsers) {

        //Phase 1, loads to the Global Scope all the names of the Enkel files
        List<Triple<ImportResolver, List<Triple<EnkelParser.ClassDeclarationContext, String, String>>, String>> ofClasses = enkelParsers.stream()
                .map(this::processClassNames)
                .peek(holderOfHolderOfClasses -> holderOfHolderOfClasses.value2
                        .forEach(holderOfClasses -> globalScope.registerClass(holderOfClasses.value2)))
                .collect(Collectors.toList());

        ofClasses.forEach(holderOfHolderOfClasses -> holderOfHolderOfClasses.value1.loadClassImports());

        List<Triple<ImportResolver, List<Holder>, String>> parserScopes = ofClasses
                .stream()
                .map(this::processClassDeclarations)
                .peek(holderOfHolders -> holderOfHolders.value2
                        .forEach(holder -> globalScope.addScope(holder.scope.getFullClassName(), holder.scope)))
                .collect(Collectors.toList());

        //Phase 2 resolve all class references of the ImportResolvers of each scope
        parserScopes.stream()
                .peek(holderOfHolders -> holderOfHolders.value2.forEach(this::processFieldDeclarations))
                .forEach(holderOfHolders -> holderOfHolders.value2.forEach(this::processMethodDeclarations));

        //Phase 3 resolve all static methods and field references of the ImportResolvers of each scope
        parserScopes.forEach(holderOfHolders -> holderOfHolders.value1.loadMethodsAndFieldsImports());

        //Phase 4 process the compilation data, all imports should already be resolved by now.
        return parserScopes.stream()
                .map(this::processCompilationUnit)
                .collect(Collectors.toList());

    }


    private class Triple<A, B, C> {
        private final A value1;
        private final B value2;
        private final C value3;

        Triple(A value1, B value2, C value3) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
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
