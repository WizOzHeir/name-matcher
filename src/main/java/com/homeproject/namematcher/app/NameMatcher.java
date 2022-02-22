package com.homeproject.namematcher.app;

import com.homeproject.namematcher.lucene.Indexer;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.homeproject.namematcher.util.Checker.*;
import static com.homeproject.namematcher.util.Constants.*;

public class NameMatcher {
  private static final Logger LOGGER = Logger.getLogger(NameMatcher.class.getName());

  private String inputName;
  private String[] inputFiles;
  private Map<String, Set<String>> inputFileToNameMatches;

  public NameMatcher(String inputName, String[] inputFiles) {
    setInputName(inputName);
    setInputFiles(inputFiles);
  }

  private void setInputName(String inputName) {
    if (isCorrectInputName(inputName)) {
      this.inputName = inputName;
    }
  }

  private void setInputFiles(String[] inputFiles) {
    if (isCorrectInputFiles(inputFiles)) {
      this.inputFiles = inputFiles;
    }
  }

  public Map<String, Set<String>> getInputFileToNameMatches() {
    return inputFileToNameMatches;
  }

  public void start() {
    inputFileToNameMatches = new HashMap<>();

    for (String inputFile : inputFiles) {
      LOGGER.info(String.format("Work with %s file", inputFile));
      try (Indexer indexer = new Indexer(inputFile)) {
        List<Document> nameDocs = indexer.searchIndexWithFuzzyQuery(inputName);
        List<String> names = nameDocs.stream().map(nameDoc -> nameDoc.get(LUCENE_RAW_CONTENT)).collect(Collectors.toList());
        inputFileToNameMatches.put(inputFile, new HashSet<>(names));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
