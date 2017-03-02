package com.kubadziworski.configuration;


import com.kubadziworski.compiler.RadiumArguments;
import com.kubadziworski.resolver.ClazzImportResolver;
import com.kubadziworski.resolver.EnkelImportResolver;
import com.kubadziworski.resolver.ResolverContainer;

import java.util.Arrays;

public class JvmConfiguration extends CompilerConfiguration {

    private final ClassLoader classLoader;

    public JvmConfiguration(RadiumArguments arguments) {
        this.classLoader = arguments.classLoader;
        ImportResolverContainerEnum.INSTANCE.initialize(this, arguments);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ResolverContainer getResolverContainer() {
        return ImportResolverContainerEnum.INSTANCE.getImportResolver();
    }

    enum ImportResolverContainerEnum {
        INSTANCE;
        ResolverContainer importResolver;

        public void initialize(JvmConfiguration cfg, RadiumArguments arguments) {
            importResolver = new ResolverContainer(Arrays.asList(new EnkelImportResolver(cfg.getGlobalScope()),
                    new ClazzImportResolver(cfg.getClassLoader())));
        }

        public ResolverContainer getImportResolver() {
            return importResolver;
        }
    }


}
