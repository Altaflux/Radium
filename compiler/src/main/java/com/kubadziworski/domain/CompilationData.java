package com.kubadziworski.domain;

import com.kubadziworski.antlr.EnkelParser;

public class CompilationData {

    private final String filePath;
    private final String basePath;
    private final EnkelParser enkelParser;

    public CompilationData(String basePath, String filePath, EnkelParser enkelParser) {
        this.filePath = filePath;
        this.enkelParser = enkelParser;
        this.basePath = basePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public EnkelParser getEnkelParser() {
        return enkelParser;
    }

    public String getBasePath() {
        return basePath;
    }
}
