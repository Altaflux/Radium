package com.kubadziworski.parsing.visitor.phase;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.configuration.CompilerConfigInstance;
import com.kubadziworski.configuration.JvmConfiguration;
import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.CompilationData;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.parsing.visitor.ClassVisitor;
import com.kubadziworski.resolver.ImportResolver;
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

    private ResolverContainerFilePath processClassNames(CompilationData compilationData) {

        EnkelParser.CompilationUnitContext context = compilationData.getEnkelParser().compilationUnit();
        String packageDeclaration = "";
        EnkelParser.PackageDeclarationContext declarationContexts = context.packageDeclaration();
        if (declarationContexts != null) {
            packageDeclaration = declarationContexts.SimpleName().stream().map(ParseTree::getText).collect(Collectors.joining("."));
        }
        JvmConfiguration jvmConfiguration = CompilerConfigInstance.getConfig();
        ImportResolver importResolver = new ImportResolver(context.importDeclaration(), jvmConfiguration.getResolverContainer());
        String packageDeclaration2 = packageDeclaration;
        List<ClassContextClassNamePackage> classes = context.classDeclaration().stream().map(ctx -> {
            if (StringUtils.isNotEmpty(packageDeclaration2)) {
                return new ClassContextClassNamePackage(ctx, ctx.className().getText(), packageDeclaration2);
            } else {
                return new ClassContextClassNamePackage(ctx, ctx.className().getText(), "");
            }
        }).collect(Collectors.toList());


        return new ResolverContainerFilePath(importResolver, classes, compilationData.getFilePath());
    }

    private ImportHolderListPath processClassDeclarations(ResolverContainerFilePath resolverContainerFilePath) {
        ImportResolver importResolver = resolverContainerFilePath.importResolver;
        List<Holder> scopes = resolverContainerFilePath.containers.stream().map(holderOfClasses -> holderOfClasses)
                .map(ctx -> {
                    String superClass = AnyType.INSTANCE.getName();
                    return new Holder(ctx.context, new Scope(new MetaData(ctx.className, ctx.classPackage, superClass,
                            Collections.emptyList(), resolverContainerFilePath.path), importResolver));
                }).collect(Collectors.toList());

        return new ImportHolderListPath(importResolver, scopes, resolverContainerFilePath.path);
    }


    private void processFieldDeclarations(Holder compilationData) {
        FieldPhaseVisitor fieldPhaseVisitor = new FieldPhaseVisitor(compilationData.scope);
        compilationData.ctx.accept(fieldPhaseVisitor);
    }

    private void processConstructorDeclarations(Holder compilationData) {
        ConstructorPhaseVisitor constructorPhaseVisitor = new ConstructorPhaseVisitor(compilationData.scope);
        compilationData.ctx.accept(constructorPhaseVisitor);
    }

    private void processMethodDeclarations(Holder compilationData) {
        MethodPhaseVisitor methodPhaseVisitor = new MethodPhaseVisitor(compilationData.scope);
        compilationData.ctx.accept(methodPhaseVisitor);
    }

    private CompilationUnit processCompilationUnit(ImportHolderListPath compilationData) {
        List<ClassDeclaration> classDeclaration = compilationData.holder.stream().map(holder -> {
            ClassVisitor classVisitor = new ClassVisitor(holder.scope);
            return holder.ctx.accept(classVisitor);
        }).collect(Collectors.toList());

        return new CompilationUnit(classDeclaration, compilationData.path,
                classDeclaration.stream().findAny().map(ClassDeclaration::getClassPackage).filter(Objects::nonNull).orElse(""));

    }

    public List<CompilationUnit> processAllClasses(List<CompilationData> enkelParsers) {

        //Phase 1, loads to the Global Scope all the names of the Enkel files
        List<ResolverContainerFilePath> ofClasses = enkelParsers.stream()
                .map(this::processClassNames)
                .peek(holderOfHolderOfClasses -> holderOfHolderOfClasses.containers
                        .forEach(holderOfClasses -> {
                            if (StringUtils.isNotEmpty(holderOfClasses.classPackage)) {
                                globalScope.registerClass(holderOfClasses.classPackage + "." + holderOfClasses.className);
                            } else {
                                globalScope.registerClass(holderOfClasses.className);
                            }
                        }))
                .collect(Collectors.toList());

        ofClasses.forEach(holderOfHolderOfClasses -> holderOfHolderOfClasses.importResolver.loadClassImports());

        List<ImportHolderListPath> parserScopes = ofClasses
                .stream()
                .map(this::processClassDeclarations)
                .peek(holderOfHolders -> holderOfHolders.holder
                        .forEach(holder -> globalScope.addScope(holder.scope.getFullClassName(), holder.scope)))
                .collect(Collectors.toList());

        //Phase 2 resolve all class references of the ImportResolvers of each scope
        parserScopes.stream()
                .peek(holderOfHolders -> holderOfHolders.holder.forEach(this::processFieldDeclarations))
                .peek(holderOfHolders -> holderOfHolders.holder.forEach(this::processConstructorDeclarations))
                .forEach(holderOfHolders -> holderOfHolders.holder.forEach(this::processMethodDeclarations));

        //Phase 3 resolve all static methods and field references of the ImportResolvers of each scope
        parserScopes.forEach(holderOfHolders -> holderOfHolders.importResolver.loadMethodsAndFieldsImports());

        //Phase 4 process the compilation data, all imports should already be resolved by now.
        return parserScopes.stream()
                .map(this::processCompilationUnit)
                .collect(Collectors.toList());

    }

    private static class ImportHolderListPath {
        private final ImportResolver importResolver;
        private final List<Holder> holder;
        private final String path;

        ImportHolderListPath(ImportResolver importResolver, List<Holder> holder, String path) {
            this.importResolver = importResolver;
            this.holder = holder;
            this.path = path;
        }
    }

    private static class ResolverContainerFilePath {
        private final ImportResolver importResolver;
        private final List<ClassContextClassNamePackage> containers;
        private final String path;

        ResolverContainerFilePath(ImportResolver importResolver, List<ClassContextClassNamePackage> containers, String path) {
            this.importResolver = importResolver;
            this.containers = containers;
            this.path = path;
        }
    }

    private static class ClassContextClassNamePackage {
        private final EnkelParser.ClassDeclarationContext context;
        private final String className;
        private final String classPackage;

        ClassContextClassNamePackage(EnkelParser.ClassDeclarationContext context, String className, String classPackage) {
            this.context = context;
            this.className = className;
            this.classPackage = classPackage;
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
