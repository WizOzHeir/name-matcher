package com.homeproject.namematcher.util;

import com.homeproject.namematcher.app.NameMatcher;
import exception.InputException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static com.homeproject.namematcher.util.FileUtil.createDirectory;
import static com.homeproject.namematcher.util.FileUtil.writeInFile;

public class NameMatcherViewer {
  private static final Logger LOGGER = Logger.getLogger(NameMatcherViewer.class.getName());

  private NameMatcher nameMatcher;
  private File outputDir = null;

  public NameMatcherViewer(NameMatcher nameMatcher) {
    this.nameMatcher = nameMatcher;
  }

  public void setOutputDir(String outputDirName)
      throws InputException.InvalidParam, IOException, InputException.NoData {
    LOGGER.info(String.format("Create output directory %s", outputDir));
    if (outputDirName != null) {
      outputDir = createDirectory(outputDirName);
    }
  }

  public void getView(String outputDirName)
      throws InputException.InvalidParam, IOException, InputException.NoData {
    LOGGER.info("Set appropriate result(s) view");

    if (outputDir != null) {
      setOutputDir(outputDirName);
      nameMatcher
          .getInputFileToNameMatches()
          .forEach(
              (inputFile, uniqueNames) -> {
                File outputFile =
                    new File(outputDir.getPath(), String.format("OUTPUT-%s", inputFile));
                try {
                  writeInFile(outputFile, uniqueNames);
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });
    }
    nameMatcher
        .getInputFileToNameMatches()
        .forEach(
            (inputFile, uniqueNames) -> {
              System.out.println(String.format("=== Output for %s file ===", inputFile));
              uniqueNames.forEach(name -> System.out.println(name));
            });
  }
}
