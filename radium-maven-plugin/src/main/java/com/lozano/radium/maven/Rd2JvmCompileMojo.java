package com.lozano.radium.maven;

import com.kubadziworski.compiler.ArgumentsCompiler;
import com.kubadziworski.compiler.RadiumArguments;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class Rd2JvmCompileMojo extends AbstractMojo {

    @Parameter(property = "kotlin.compiler.jdkHome", required = false, readonly = false)
    protected String jdkHome;

    @Parameter(defaultValue = "${project.compileSourceRoots}", required = true)
    private List<String> defaultSourceDirs;

    @Parameter
    private List<String> sourceDirs;

    @Parameter(defaultValue = "${project.compileClasspathElements}", required = true, readonly = true)
    public List<String> classpath;


    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    public String output;


    private List<String> getSourceFilePaths() {
        if (sourceDirs != null && !sourceDirs.isEmpty()) return sourceDirs;
        return defaultSourceDirs;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {

        RadiumArguments radiumArguments = new RadiumArguments();
        radiumArguments.sourceDirs = getSourceFilePaths();
        radiumArguments.outputDirectory = output;
        radiumArguments.classLoader = new URLClassLoader(getClassPathElements(), getClass().getClassLoader());

        ArgumentsCompiler argumentsCompiler = new ArgumentsCompiler(radiumArguments);
        argumentsCompiler.run();
    }

    private URL[] getClassPathElements() {
        return classpath.stream().map(s -> {
            try {
                return new File(s).toURI().toURL();
            } catch (MalformedURLException e) {
                return null;
            }
        }).filter(Objects::nonNull).toArray(URL[]::new);
    }
}
