package com.kubadziworski.configuration;


import com.kubadziworski.compiler.RadiumArguments;

public class CompilerConfigInstance {

    private static CompilerConfiguration ourInstance;

    private CompilerConfigInstance() {
    }

    public static void initialize(RadiumArguments arguments) {
        ourInstance = new JvmConfiguration(arguments);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CompilerConfiguration> T getConfig() {
        return (T) ourInstance;
    }


}
