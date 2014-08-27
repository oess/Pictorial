Pictorial
=========

Uses Depict to generate images and code samples.

## Requirements

* Java JDK 8 x64: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* Maven 3.22+: http://maven.apache.org/download.cgi

## Building

### Windows:

* `mvn package -Doe.platform=Windows`

### Linux:

* `mvn package -Doe.platform=Linux`

### OSX:

* `mvn package -Doe.platform=MacOSX`

## Running Pictorial

* `java -jar target/Pictorial-1.0.jar`

## Known Issues:

* Errors are displayed in the console and not in the UI
* Generated code needs comments
