package com.homeproject.namematcher;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;

import exception.InputException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static com.homeproject.namematcher.util.FileUtil.*;
import static com.homeproject.namematcher.util.Checker.*;

public class UtilTests {
  @Test
  public void givenListWhenWriteToFileThenContentIsCorrect(@TempDir Path tempDir)
      throws IOException {
    Path filePath = tempDir.resolve("tmp");

    List<String> lines = Arrays.asList("1", "2", "3");
    writeInFile(filePath.toFile(), new HashSet<>(lines));

    assertAll(
        () -> assertTrue(Files.exists(filePath), "File should exist"),
        () -> assertLinesMatch(lines, Files.readAllLines(filePath)));
    Files.createTempDirectory("tmp");
  }

  @Test
  void givenAlreadyWrittenToFileWhenCheckContentsThenContentIsCorrect()
      throws InputException.NoFile, InputException.InvalidParam, InputException.NoData,
          IOException {

    InputStream ioStream = readAsIOStream("abc.txt", this.getClass());
    List<String> lines = readFromIOStream(ioStream);
    assertLinesMatch(Arrays.asList("abc", "abc", "abc"), lines);
  }

  @Test
  public void givenTooLongFileNameWhenValidateThenFails() {
    String fileName = generateRandomStringOfLength(500);
    assertThrows(
        InputException.InvalidParam.class, () -> isCorrectFileName(fileName), "Invalid parameter");
  }

  @ParameterizedTest
  @NullSource
  public void givenNullInputWhenValidateThenFails(String input) {
    assertThrows(
        InputException.NoData.class, () -> isCorrectInputName(input), "Empty input is not allowed");
  }

  private String generateRandomStringOfLength(int targetStringLength) {
    int leftLimit = 97;
    int rightLimit = 122;
    Random random = new Random();

    String generatedString =
        random
            .ints(leftLimit, rightLimit + 1)
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

    return generatedString;
  }
}
