# Scala Date Transformer

See [HomeWork](HomeWork.md) for an overview of the requirements.

## Assumptions and Issues

The pattern for the source and destinations is based off [DateTimeFormatter](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/format/DateTimeFormatter.html).
The Oracle Java docs define the patterns supported however, this application is using only the year, month, and 
day of month. The assumption is we are only dealing with dates, and not times or eras.  This keeps it simple in regards
to what the user can input.  See the 'Source Pattern' section below.

The HomeWork shows an example where the source text has a date `April 26th, 2008`.  The 'th' is supported in both
the source and destination patterns however, the limitation is only one form is supported at a time when running the 
app at.  The supported forms are `st | nd | rd | th`.  See the sample [file2.txt](files/file2.txt) having the 
value `April 26th, 2008`.  This works when the source pattern is `MMMM d'th', yyyy` however, if 
the file also contained `March 3rd, 2012` and `May 2nd, 2000` there is no way, at the moment, to tell the application 
to replace both the 'th', 'nd', and 'rd'.

The HomeWork shows an example source with date `9/6/78` and the expected output of `September 06, 1978`.  The 
application does not produce the year '1978' but instead produces '2078'.  The JavaDoc states for Year
```text
 If the count of letters is two, then a reduced two digit form is used. For printing, this outputs the rightmost two 
 digits. For parsing, this will parse using the base value of 2000, resulting in a year within the range 2000 to 2099 
 inclusive.
```


## Source Pattern

The valid **source pattern** used by this application are: 
* Year: yy, yyyy
* Month: M, MM, MMM, MMMM (not supported is MMMMM) 
* Day of Month: d, dd

Examples are:
* MMM dd, yyyy -> Mar 21, 2010
* yyyy-MM-dd -> 2019-12-08
* MMMM d'th', yyyy -> April 4th, 2023 

See below for running the application.  There is some things to be aware of when running this from the commandline.  One
example is needing to escape the single quotes around the `th`. 

## Tools

To build and run the application you need

* Java: this project was developed using Java 17, Java 8 and higher should work
* Scala: this project was developed using Scala version 3.3.1
* SBT: this project was developed using SBT version 1.9.7


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

See the test class [DateTransformerTestSuite](src/test/scala/DateTransformerTestSuite.scala).

### Running the application

The application outputs the results to the console.  A future improvement could be adding an argument that tells the 
application the file to write the transformed contents, or a REST API taking a file upload that returns the
transformed text in the response body.

The arguments required by the application are expecetd to be in this order: `filepath sourcePattern destinationPattern`

* filepath - the path to the file to be transformed, it can be an absolute path or a path relative to the folder you are in
* sourcePattern - the pattern of the dates in the file
* destinationPattern - the pattern to transform the dates in the file to

If there is an error encountered when running the application it will be printed to the console.

#### Run with scala

There are different ways to run the application.  Once the JAR is built in the `target/scala-3.3.1` folder you 
can run it using `scala`.  

Open a terminal in the root of this project.  Below are two sample commands that use files in the project.  They were 
tested on Mac.  This has not been tested on Windows and will use different syntax.  

There is escaping required for certain source/destination patterns.

`scala target/scala-3.3.1/*.jar files/file1.txt "MMMM dd, yyyy" M/d/yy`

`scala target/scala-3.3.1/*.jar files/file2.txt "MMMM d'\'th\'', yyyy" yyyy-MM-dd`

#### Run with SBT

Below are the two sample commands that use SBT to run the application, these will not start the SBT console

`sbt "run files/file1.txt \"MMMM dd, yyyy\" M/d/yy"`

`sbt "run files/file2.txt \"MMMM d'th', yyyy\" yyyy-MM-dd"`

You can use the SBT console.  Run `sbt` from your terminal to start the console.  Once the console has started you
can run these commands to test the application.

`run files/file1.txt "MMM dd, yyyy" M/d/yy`

`run files/file2.txt "MMMM d'th', yyyy" yyyy-MM-dd`

If you wanted to see an error you can give the application a file that does not exist
`run files/doesnotexist.txt "MMM dd, yyyy" M/d/yy`

Another error is giving a date pattern that causes an error, for example
`run files/file1.txt "MMMM d'th', yyyy" M/d/yy`

The error displayed is 
```text
Error transforming dates with sourcePattern: MMMM d'th', yyyy, destinationPattern: M/d/yy - message: Text 'March 21, 1975' could not be parsed at index 8
```

## Design

The solution used was to convert the source pattern into a regular expression.  

With the regular expression, the file is searched matching strings.  

Given a list of string matches, they would be converted to a date instance using the source pattern then formatted 
back into a string using the destination pattern.  

Iterating over the string matches, the file text is replaced where the string matches are replaced with the destination
pattern date strings.


