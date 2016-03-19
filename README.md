#Enkel
Enkel is a simple programming language running on jvm

## Compiling and running Enkel scripts
1.Build compiler into executable jar

```bash
mvn clean package
```
2.Compile sample .enk file

```bash
java -jar compiler/target/compiler-1.0-SNAPSHOT-jar-with-dependencies.jar  EnkelExamples/first.enk
```

3.Run compiled .enk program

```bash
java first
```