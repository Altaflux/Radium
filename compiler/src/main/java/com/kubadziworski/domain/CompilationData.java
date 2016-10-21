package com.kubadziworski.domain;

import com.kubadziworski.antlr.EnkelParser;

/**
 * Created by plozano on 10/20/2016.
 */
public class CompilationData {

    private final String filePath;
    private final EnkelParser enkelParser;

    public CompilationData(String filePath, EnkelParser enkelParser) {
        this.filePath = filePath;
        this.enkelParser = enkelParser;
    }

    public String getFilePath() {
        return filePath;
    }

    public EnkelParser getEnkelParser() {
        return enkelParser;
    }
}
