package com.homeproject.namematcher.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.homeproject.namematcher.util.Constants.*;

public class Checker {
  private static final Logger LOGGER = Logger.getLogger(Checker.class.getName());

  public static final Character[] INVALID_WINDOWS_SPECIFIC_CHARS = {'"', '*', ':', '<', '>', '?', '\\', '|', 0x7F};
  public static final Character[] INVALID_UNIX_SPECIFIC_CHARS = {'\000'};

  public static boolean isCorrectFileName(String fileName) {
    LOGGER.info(String.format("Check if %s is correct", fileName));

    if (fileName == null || fileName.isEmpty() || fileName.length() > 255) {
      throw new IllegalArgumentException("Invalid directory name");
    }
    return Arrays.stream(getInvalidCharsByOS()).noneMatch(ch -> fileName.contains(ch.toString()));
  }

  public static boolean isFileExisted(String fileName) throws FileNotFoundException {
    LOGGER.info(String.format("Check if %s exists", fileName));

    InputStream ioStream = Checker.class.getClassLoader().getResourceAsStream(fileName);
    if (ioStream == null) {
      throw new FileNotFoundException(String.format("<%s> not found", fileName));
    }
    return true;
  }

  public static boolean isCorrectInputName(String inputName) {
    LOGGER.info(String.format("Check if %s is correct", inputName));

    if (inputName == null || inputName.length() == 0 || inputName.length() > MAX_INPUT_NAME_LEN) {
      throw new IllegalArgumentException("Invalid input");
    }
    Pattern pattern = Pattern.compile("^[(?i)a-z ,.'-]+", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(inputName);
    inputName = inputName.strip();
    if (inputName.length() < MIN_INPUT_NAME_LEN) {
      throw new IllegalArgumentException(String.format("Must be at least %d characters in the name", MIN_INPUT_NAME_LEN));
    }
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid input");
    }
    return true;
  }

  public static boolean isCorrectInputFiles(String[] inputFiles) {
    LOGGER.info("Check if input files are correct");

    if (inputFiles == null || inputFiles.length == 0 || inputFiles.length > MAX_INPUT_NAME_LEN) {
      throw new IllegalArgumentException("Invalid input");
    }
    int checkedFileCnt = 0;
    for (String inputFile : inputFiles) {
      try {
        if (isFileExisted(inputFile)) {
          checkedFileCnt++;
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return checkedFileCnt == inputFiles.length;
  }

  public static Character[] getInvalidCharsByOS() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {
      return INVALID_WINDOWS_SPECIFIC_CHARS;
    } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
      return INVALID_UNIX_SPECIFIC_CHARS;
    } else {
      return new Character[] {};
    }
  }

  public static boolean isGoodScore(double a, double b) {
    if (a == b) return true;
    return ((Math.max(a, b) - Math.min(a, b)) / Math.max(a, b) * 100) < 25;
  }
}
