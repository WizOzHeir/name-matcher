package com.homeproject.namematcher.util;

import exception.InputException;

import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.homeproject.namematcher.util.Constants.*;

public class Checker {
  private static final Logger LOGGER = Logger.getLogger(Checker.class.getName());

  public static final Character[] INVALID_WINDOWS_SPECIFIC_CHARS = {
    '"', '*', ':', '<', '>', '?', '\\', '|', 0x7F
  };
  public static final Character[] INVALID_UNIX_SPECIFIC_CHARS = {'\000'};

  public static boolean isCorrectFileName(String fileName)
      throws InputException.NoData, InputException.InvalidParam {
    LOGGER.info(String.format("Check if %s is correct", fileName));

    if (fileName == null || fileName.isEmpty()) {
      throw new InputException.NoData();
    }
    if (fileName.length() > 255) {
      throw new InputException.InvalidParam();
    }
    return Arrays.stream(getInvalidCharsByOS()).noneMatch(ch -> fileName.contains(ch.toString()));
  }

  public static boolean isFileExisted(String fileName)
      throws InputException.NoFile, InputException.InvalidParam, InputException.NoData {
    LOGGER.info(String.format("Check if %s exists", fileName));

    if (isCorrectFileName(fileName)) {
      InputStream ioStream = Checker.class.getClassLoader().getResourceAsStream(fileName);
      if (ioStream == null) {
        throw new InputException.NoFile(fileName);
      }
      return true;
    }
    return false;
  }

  public static boolean isCorrectInputName(String inputName)
      throws InputException.NoData, InputException.InvalidParam {
    LOGGER.info(String.format("Check if %s is correct", inputName));

    if (inputName == null || inputName.length() == 0) {
      throw new InputException.NoData();
    }

    Pattern pattern = Pattern.compile("^[(?i)a-z ,.'-]+", Pattern.CASE_INSENSITIVE); // correct name
    Matcher matcher = pattern.matcher(inputName);
    inputName = inputName.strip();

    if (inputName.length() < MIN_INPUT_NAME_LEN
        || inputName.length() > MAX_INPUT_NAME_LEN
        || !matcher.matches()) {
      throw new InputException.InvalidParam();
    }
    return true;
  }

  public static boolean isCorrectInputFiles(String[] inputFiles)
      throws InputException.NoData, InputException.InvalidParam, InputException.NoFile {
    LOGGER.info("Check if input files are correct");

    if (inputFiles == null || inputFiles.length == 0) {
      throw new InputException.NoData();
    }
    if (inputFiles.length > MAX_INPUT_NAME_LEN) {
      throw new InputException.InvalidParam();
    }
    int checkedFileCnt = 0;
    for (String inputFile : inputFiles) {
      if (isFileExisted(inputFile)) {
        checkedFileCnt++;
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
