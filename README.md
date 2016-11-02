#Radium

[![Build Status](https://travis-ci.org/JakubDziworski/Enkel-JVM-language.svg?branch=master)](https://travis-ci.org/JakubDziworski/Enkel-JVM-language)  [![Join the chat at https://gitter.im/JakubDziworski/Enkel-JVM-language](https://badges.gitter.im/JakubDziworski/Enkel-JVM-language.svg)](https://gitter.im/JakubDziworski/Enkel-JVM-language?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Radium is a simple programming language running on the  jvm.
It is based on the work of Jakub Dziworski
https://github.com/JakubDziworski/Enkel-JVM-language

## Compiling and running Radium scripts
1.Build compiler into executable jar

```bash
mvn clean package
```
2.Compile sample .enk file (You can find more examples in EnkelExamples directory)

```bash
java -classpath compiler/target/compiler-1.0-SNAPSHOT-jar-with-dependencies.jar:. com.kubadziworski.compiler.Compiler EnkelExamples/DefaultParamTest.enk
```

3.Run compiled .enk program

```bash
java DefaultParamTest
```
