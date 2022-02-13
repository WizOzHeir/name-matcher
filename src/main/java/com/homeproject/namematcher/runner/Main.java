package com.homeproject.namematcher.runner;

import static com.homeproject.namematcher.util.Checker.*;
import static com.homeproject.namematcher.util.Constants.*;
import static com.homeproject.namematcher.util.FileUtil.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import picocli.CommandLine;
import picocli.CommandLine.*;

import com.homeproject.namematcher.lucene.Indexer;


@Command(name = "start", mixinStandardHelpOptions = true, version = "1.0",
        description = "Compare given name against blacklist. At least 2 letters.")
public class Main implements Callable<Integer> {
	
    @Parameters(index = "0", description = "The name to match")
    private String inputName;
    
    @Parameters(index = "1..*", arity="1..2", description = "Files (1-2) for matching")
    private String[] inputFiles;
    
    @Option(names = { "-o", "--out" }, description = "Output directory (default: print to console)")
    private String outputDirName;

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public Integer call() throws IOException, ParseException {
    	isCorrectInputName(inputName);
    	
    	File outputDir = null;
    	if(outputDirName != null) {
    		outputDir = createDirectory(outputDirName);
    	}
    	
    	for(String inputFile: inputFiles) {
    		Indexer indexer = new Indexer(inputFile);
	    	List<Document> nameDocs = indexer.searchIndexWithFuzzyQuery(inputName);
	    	List<String> names = nameDocs.stream().
    				map(nameDoc -> nameDoc.get(LUCENE_RAW_CONTENT)).
    				collect(Collectors.toList());  		
	    	Set<String> uniqueNames = new HashSet<>(names);
    		
	    	if (outputDir != null) {
	    		LOGGER.info(String.format("=== Output in %s directory ===", outputDir));
	    		File outputFile = new File(outputDir.getPath(), String.format("OUTPUT-%s", inputFile));
	    		writeInFile(outputFile, uniqueNames);
	        } else {
	        	LOGGER.info(String.format("=== Output for %s file ===", inputFile));
	        	uniqueNames.forEach(name -> System.out.println(name));
	        }
	    	
	    	LOGGER.info(String.format("Found %s unique result(s)", uniqueNames.size()));
	    	indexer.close();
    	}
        return 0;
    }

    public static void main(String[] args) {    	
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}