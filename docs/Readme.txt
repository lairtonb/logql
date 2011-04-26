*******************************
*     logQL Version 1.5       *
*******************************

April 2011

1. Introduction
2. Dependencies/system requirements
3. Starting logQL
4. Quick Start
5. Memory
6. Known Limitations


1.  INTRODUCTION
----------------------
logQL is a data analysis software which allows you to run SQL like 
queries directly on files without any database.

logQL supports the following file types:

(a) CSV files.
(b) Delimited record format. Where the fields are separated by a
    standard character, such as '|' or '\t'
(c) Custom formats. Can have multiple types of delimiters per row 
    but the same format is present on all rows. For instance 
    HTTP access logs.


2.  DEPENDENCIES
----------------------
logQL has the following dependencies:

    Java(JRE) 1.6 or higher.

You can download java depending on the operating systems from:

(a) Windows: http://www.java.com/en/download/manual.jsp
(b) Mac:     http://developer.apple.com/java/
(c) Linux:   http://www.java.com/en/download/manual.jsp
(d) SolarisL http://www.java.com/en/download/manual.jsp
(e) FreeBSD  http://www.freebsd.org/java/
(f) AIX      http://www.ibm.com/developerworks/java/jdk/aix/service.html
(g) HP UX    http://www.hp.com/products1/unix/java/


3. RUNNING logQL
----------------------
(a) For UI: logQL comes with an executable jar 

        double click "logQL.jar"

    If it does not work, you can run the command

        java -jar logQL.jar

    From a terminal window/command prompt.

(b) For interactive CLI: From a terminal window/command prompt, run
    the command:

    	java -jar logQL.jar -c

(c) For CLI: To execute a single query from CLI, run the command

        java -jar logQL.jar -o <outputFormat HTML|CSV> -f <outputFile> <query>


4.  MEMORY
----------------------
logQL has a small memory footprint. However, if the query results are very large,
you may want to allocate more memory using -Xmx option:

        java -Xmx256m -jar logQL.jar


5.  KNOWN LIMITATIONS
----------------------
(a) Line Feed '\n' is always the row delimiter. This cannot be changed.
    There are cases where a csv file can have LF within quotes, 
    logQL does NOT support this.
(b) Joins are not supported.
(c) WHERE clause does not allow comparison  of two fields. A field can 
    only be compared to a value or set of values.
 
