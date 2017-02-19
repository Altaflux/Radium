package com.kubadziworski.parsing.visitor;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

import static com.kubadziworski.antlr.EnkelParser.SimpleName;

public class RadiumToken extends CommonToken {

    public RadiumToken(int type, String text) {
        super(type, text);
    }

    public RadiumToken(Pair<TokenSource, CharStream> source, int type, int channel, int start, int stop) {
        super(source, type, channel, start, stop);
    }

    @Override
    public String getText() {
        String tokenText = super.getText();
        if (tokenText != null && type == SimpleName) {
            return tokenText.replace("`", "");
        }
        return tokenText;
    }
}


