package com.kubadziworski.parsing;

import com.kubadziworski.antlr.EnkelLexer;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.CompilationData;
import com.kubadziworski.domain.CompilationUnit;
import com.kubadziworski.domain.scope.GlobalScope;
import com.kubadziworski.parsing.visitor.phase.PhaseVisitor;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kuba on 16.03.16.
 */
public class Parser {
    private final GlobalScope globalScope;

    public Parser(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    public List<CompilationUnit> processAllFiles(List<String> files) {
        List<CompilationData> enkelParsers = files.stream().map(this::getEnkelParser).collect(Collectors.toList());
        PhaseVisitor phaseVisitor = new PhaseVisitor(globalScope);
        return phaseVisitor.processAllClasses(enkelParsers);
    }

    private CompilationData getEnkelParser(String fileAbsolutePath) {
        try {
            CharStream charStream = new ANTLRFileStream(fileAbsolutePath); //fileAbolutePath - file containing first enk code file
            EnkelLexer lexer = new EnkelLexer(charStream);  //create lexer (pass enk file to it)
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            EnkelParser parser = new EnkelParser(tokenStream);

            ANTLRErrorListener errorListener = new EnkelTreeWalkErrorListener(); //EnkelTreeWalkErrorListener - handles parse tree visiting error events
            parser.addErrorListener(errorListener);
            return new CompilationData(fileAbsolutePath, parser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
