# Scala Date Transformer

See [HomeWork](HomeWork.md) for an overview of the requirements.

## Assumptions

The pattern for the source and destinations is based off [DateTimeFormatter](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/format/DateTimeFormatter.html).
The Oracle docs define the patterns supported however this application is using only the year, month, and day of month.
The assumption is we are only dealing with dates, and not times or eras.

## Source Pattern

The valid **source pattern** used by this application are: 
* Year: yy, yyyy
* Month: M, MM, MMM, MMMM (not supported is MMMMM) 
* Day of Month: d, dd

Examples are:
* MMM dd, yyyy
* yyyy-MM-dd
* MMMM d'th', yyyy

See below for running the application.  There is some things to be aware of when running this for the commandline.

## Tools

To build and run the application you need

* Java: this project was developed using Java `17`, Java 8 and up should work
* Scala: this project was developed using Scala version `3.3.1`
* SBT: this project was developed using SBT version `1.9.7`


## Usage

To interact with the application open a terminal and navigate to the root of the project.

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console`
will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).

### Building

To build a JAR that can be executed by Scala:

Run: `sbt package`

The command builds a JAR in the `target/scala-3.3.1` - the target directory may vary depending on the version of Scala you are using.

### Tests

To execute the application tests:

Run `sbt test`

### Running the application

The application outputs the results to the console.  A future improvement could be adding an argument that tells the 
application where to write the transformed contents.

#### Run with scala
There are different ways to run the application.  Once the JAR is built in the `target/scala-3.3.1` folder you 
can run it using `scala`.  

Open a terminal in the root of this project.  Below are two sample commands that use files in the project.

`scala target/scala-3.3.1/*.jar files/file1.txt "MMMM dd, yyyy" M/d/yy`

`scala target/scala-3.3.1/*.jar files/file2.txt "MMMM d'\'th\'', yyyy" yyyy-MM-dd`

#### Run with SBT


