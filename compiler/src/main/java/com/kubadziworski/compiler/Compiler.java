package com.kubadziworski.compiler;

import com.kubadziworski.bytecodegeneration.BytecodeGenerator;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.parsing.Parser;
import com.kubadziworski.validation.ARGUMENT_ERRORS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Compiler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);

    private File compilePath = new File(".");

    public static void main(String[] args) throws Exception {
        try {
            new Compiler().compile(args);
        } catch (IOException exception) {
            LOGGER.error("ERROR: " + exception.getMessage());
        }
    }

    public void compile(String[] args) throws Exception {

        List<String> enkelFiles = getListOfFiles(args[0]);
        if (enkelFiles.isEmpty()) {
            LOGGER.error(ARGUMENT_ERRORS.NO_FILE.getMessage());
            return;
        }
        LOGGER.info("Files to compile: ");
        enkelFiles.forEach(LOGGER::info);
        GlobalScope globalScope = new GlobalScope();
        ClassTypeFactory.initialize(globalScope);

        Parser parser = new Parser(globalScope);
        List<CompilationUnit> compilationUnits = parser.processAllFiles(enkelFiles);

        compilationUnits.forEach(compilationUnit -> {
            LOGGER.info("Finished Parsing. Started compiling to bytecode.");
            try {
                saveBytecodeToClassFile(compilationUnit);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ClassTypeFactory.initialize(null);
    }

    private List<String> getListOfFiles(String path) {

        DirectoryScanner scanner = new DirectoryScanner();
        // scanner.setIncludes(new String[]{"**/*.java"});
        scanner.setIncludes(new String[]{path});
        LOGGER.info("Base path: " + Paths.get(".").toAbsolutePath().normalize().toString());
        scanner.setBasedir(Paths.get(".").toAbsolutePath().normalize().toString());
        scanner.setCaseSensitive(true);
        scanner.scan();

        String[] files = scanner.getIncludedFiles();
        return Arrays.stream(files).filter(s -> s.endsWith(".enk"))
                .collect(Collectors.toList());
    }


    private void saveBytecodeToClassFile(CompilationUnit compilationUnit) throws IOException {
        BytecodeGenerator bytecodeGenerator = new BytecodeGenerator();
        byte[] bytecode = bytecodeGenerator.generate(compilationUnit);
        String className = compilationUnit.getClassName();

        File base = new File(compilationUnit.getFilePath());
        File compileFile = new File(base.getParentFile(), className + ".class");

        LOGGER.info("Finished Compiling. Saving bytecode to '{}'.", compileFile.getAbsolutePath());
        OutputStream os = new FileOutputStream(compileFile);
        IOUtils.write(bytecode, os);
        LOGGER.info("Done. To run compiled file execute: 'java {}' in current dir", className);
    }
}
