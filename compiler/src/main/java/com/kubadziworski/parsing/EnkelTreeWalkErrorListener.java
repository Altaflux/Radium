package com.kubadziworski.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kuba on 16.03.16.
 */
public class EnkelTreeWalkErrorListener extends BaseErrorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnkelTreeWalkErrorListener.class);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        String errorFormat = "You fucked up at line %d,char %d :(. Details:%n%s";
        String errorMsg = String.format(errorFormat, line, charPositionInLine, msg);
        LOGGER.error(errorMsg);
    }
}
