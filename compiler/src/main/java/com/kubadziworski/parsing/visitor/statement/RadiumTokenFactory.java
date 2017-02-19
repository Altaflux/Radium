package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.parsing.visitor.RadiumToken;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;

import static com.kubadziworski.antlr.EnkelParser.SimpleName;


public class RadiumTokenFactory implements TokenFactory<RadiumToken> {

    public static final TokenFactory<RadiumToken> DEFAULT = new RadiumTokenFactory();

    private RadiumTokenFactory() {
    }

    @Override
    public RadiumToken create(Pair<TokenSource, CharStream> source, int type, String text, int channel, int start, int stop, int line, int charPositionInLine) {
        RadiumToken t = new RadiumToken(source, type, channel, start, stop);
        t.setLine(line);
        t.setCharPositionInLine(charPositionInLine);
        if (type == SimpleName && text != null) {
            t.setText(text.replace("`", ""));
        } else {
            if (text != null) {
                t.setText(text);
            }
        }
        return t;
    }

    @Override
    public RadiumToken create(int type, String text) {
        if (type == SimpleName && text != null) {
            return new RadiumToken(type, text.replace("`", ""));
        }
        return new RadiumToken(type, text);
    }
}
