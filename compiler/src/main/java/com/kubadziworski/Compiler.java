package com.kubadziworski;

import com.kubadziworski.parsing.SyntaxTreeTraverser;
import com.kubadziworski.bytecodegeneration.CompilationUnit;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.Opcodes;

import java.io.*;

/**
 * Created by kuba on 15.03.16.
 */
public class Compiler implements Opcodes {

    public static void main(String[] args) throws Exception {
        new Compiler().compile(args);
    }

    public void compile(String[] args) throws Exception {
        final ARGUMENT_ERRORS argumentsErrors = getArgumentValidationErrors(args);
        if (argumentsErrors != ARGUMENT_ERRORS.NONE) {
            System.out.println(argumentsErrors.getMessage());
            return;
        }
        final File enkelFile = new File(args[0]);
        String fileAbsolutePath = enkelFile.getAbsolutePath();
        final CompilationUnit compilationUnit = new SyntaxTreeTraverser().getCompilationUnit(fileAbsolutePath);
        saveBytecodeToClassFile(compilationUnit);
    }

    private ARGUMENT_ERRORS getArgumentValidationErrors(String[] args) {
        if (args.length != 1) {
            return ARGUMENT_ERRORS.NO_FILE;
        }
        String filePath = args[0];
        if (!filePath.endsWith(".enk")) {
            return ARGUMENT_ERRORS.BAD_FILE_EXTENSION;
        }
        return ARGUMENT_ERRORS.NONE;
    }

    private void saveBytecodeToClassFile(CompilationUnit compilationUnit) throws IOException {
        final byte[] byteCode = compilationUnit.getByteCode();
        String className = compilationUnit.getClassName();
        String fileName = className + ".class";
        OutputStream os = new FileOutputStream(fileName);
        IOUtils.write(byteCode,os);
    }
}
