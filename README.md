# Blacklist Name Matching
> Simple algorithm to compare given name against blacklist. In case of partial match, algorithm return as few "false positive" matches as possible. 

Program accepts:

• Argument 1 is the name to match

• Argument 2 is 1-2 files where to match

• Optionally could be specified output directory. The default way - console.


## Matching approach:
* Create a Lucene Document for each name and add it to index
* Scoring documents by calculating similarity using build-in Levenshtein Distance Algorithm
* Retrieve the best candidate with the highest score
* Considering percentage difference retrieve other candidates that are similar enough to the best one


## Technology
* Java programming language
* Maven build automation tool
* Lucene library
* Picocli framework
* Jupiter/JUnit 5 as testing tools
* Google Formatter Plugin

## How to run NameMatcher
```
cd namematcher
mvn clean package
java -jar target/namematcher-0.1.0.jar [--out=<outputFile>] <inputName> <inputFiles>

```
Help

* Example of inputFiles - basic.txt, names.txt, namesWithNoise.txt
* Example of inputName - Osama Bin Laden

Example 

```
java -jar target/namematcher-0.1.0.jar "Osama Bin Laden" names.txt namesWithNoise.txt
java -jar target/namematcher-0.1.0.jar "Osama Bin Laden" basic.txt
```



## How to run test

```
cd namematcher
mvn clean test

```

## Project Status
Project is: _in progress_ 


## Room for Improvement
- improve scoring algorithm
- improve test coverage
- create independent and isolated environment to launch app
- convert to web app with drag-and-drop files and different display filters
