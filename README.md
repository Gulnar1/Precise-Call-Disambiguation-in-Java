
The project is done using VASCO framework for performing precise inter-procedural data flow analysis using VAlue Sensitive COntexts developed by Rohan Padhye as part of his Master's thesis at IIT Bombay, being advised by Prof. Uday Khedker.

The framework classes are present in the package vasco and are described in the paper: Interprocedural Data Flow Analysis in Soot using Value Contexts.

## Building with Maven ##

### Standalone build ###

Run `mvn package` in the directory after cloning the repository.

This compiles the classes into the `target/classes` directory, along with a packaged JAR: `target/vasco-$VERSION.jar`. 

### Developing with Eclipse or IntelliJ IDEA ### 

Simply import as a Maven project. Everything should work out of the box.

## Simple Examples ##

The package [`vasco.soot.examples`](https://github.com/Gulnar1/Precise-Call-Disambiguation-in-Java/tree/master/src/main/java/vasco/soot/examples) contains [`refers-to analysis`]  (https://github.com/Gulnar1/Precise-Call-Disambiguation-in-Java/blob/master/src/main/java/vasco/soot/examples/RefToAnalysis.java) implemented for Soot . For this analysis, there is also a corresponding [driver class](https://github.com/Gulnar1/Precise-Call-Disambiguation-in-Java/tree/master/src/test/java/vasco/soot/examples) to run the analysis on some application with a main class. Try running the analyses on the [provided test cases](https://github.com/Gulnar1/Precise-Call-Disambiguation-in-Java/tree/master/src/test/java/vasco/tests/VirtualCalls) or any other Java program.

You can run the examples on the command-line using the Maven exec plugin:

```
mvn exec:java -Dexec.mainClass="vasco.soot.examples.RefToTest" -Dexec.args="-cp target/test-classes/ vasco.tests.A"
```
Or simply run:
```
./run.sh RefToTest [classname] > results/classname.txt
```

This will generate a text file in the output/results directory containing statistics of refers-to relation and callee-context transition graph.



