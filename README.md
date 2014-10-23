Pictorial
=========

Uses Depict to generate images and code samples.

## Pre-built Binaries

Executable jars are available [here](https://openeye.box.com/s/8egmcvclk7adg2irkv15), which require [Java JDK 1.8u20+ x64](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) to run.


## Development Requirements

* [Java JDK 1.8u20+ x64](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3.22+](http://maven.apache.org/download.cgi)

## Building

### Windows:

* `mvn package -Doe.platform=Windows`

### Linux:

* `mvn package -Doe.platform=Linux`

### OSX:

* `mvn package -Doe.platform=MacOSX`

## Running Pictorial

* `java -jar target/Pictorial-1.1.jar`

## Known Issues:

* Warnings are displayed in the console and not in the UI
* Generated code needs comments
