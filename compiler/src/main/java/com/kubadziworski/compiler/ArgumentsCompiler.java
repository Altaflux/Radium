package com.kubadziworski.compiler;


import com.kubadziworski.bytecodegeneration.BytecodeGenerator;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.exception.CompilationException;
import com.kubadziworski.parsing.Parser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tools.ant.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArgumentsCompiler {

    private final RadiumArguments arguments;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentsCompiler.class);

    public ArgumentsCompiler(RadiumArguments arguments) {
        this.arguments = arguments;
    }

    public void run() throws CompilationException {
        ClassTypeFactory.classLoader = arguments.classLoader;
        GlobalScope globalScope = new GlobalScope();
        ClassTypeFactory.initialize(globalScope);
        Parser parser = new Parser(globalScope);

        List<Pair<String, List<String>>> compilationData = arguments.sourceDirs.stream()
                .map(s -> Pair.of(s, getListOfFiles(s))).collect(Collectors.toList());
        List<CompilationUnit> compilationUnits = parser.processAllFiles(compilationData);

        compilationUnits.forEach(this::saveBytecodeToClassFile);
    }

    private List<String> getListOfFiles(String path) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{path + File.separator + "**" + File.separator + "*"});
        scanner.setCaseSensitive(true);
        scanner.scan();

        String[] files = scanner.getIncludedFiles();
        return Arrays.stream(files).filter(s -> s.endsWith(".enk"))
                .collect(Collectors.toList());
    }


    private void saveBytecodeToClassFile(CompilationUnit compilationUnit) {
        BytecodeGenerator bytecodeGenerator = new BytecodeGenerator();
        List<BytecodeGenerator.GeneratedClassHolder> bytecode = bytecodeGenerator.generate(compilationUnit);

        bytecode.forEach(generatedClassHolder -> {
            try {
                String className = generatedClassHolder.getName();
                String outputPath = compilationUnit.getClassPackage().replace(".", "/") + "/";
                Path paths = Files.createDirectories(Paths.get(arguments.outputDirectory, outputPath));
                File compileFile = new File(paths.toFile(), className + ".class");
                LOGGER.info("Finished Compiling. Saving bytecode to '{}'.", compileFile.getAbsolutePath());
                OutputStream os = new FileOutputStream(compileFile);
                IOUtils.write(generatedClassHolder.getBytes(), os);
            } catch (Exception e) {
                throw new CompilationException("Could not convert to bytecode", e);
            }
        });
    }
}
