package util;

import exception.InputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static com.homeproject.namematcher.util.Checker.*;
import static com.homeproject.namematcher.util.Constants.MIN_INPUT_NAME_LEN;
import static helper.NiceTestHelper.generateRandomStringOfLength;
import static org.junit.jupiter.api.Assertions.*;

public class CheckerTests {
  @Test
  public void givenTooShortInputNameWhenValidateThenFails() {
    String inputName = generateRandomStringOfLength(MIN_INPUT_NAME_LEN - 1);
    assertThrows(
        InputException.InvalidParam.class,
        () -> isCorrectInputName(inputName),
        "Invalid parameter");
  }

  @ParameterizedTest
  @NullSource
  public void givenNullInputWhenValidateThenFails(String input) {
    assertThrows(
        InputException.NoData.class, () -> isCorrectInputName(input), "Empty input is not allowed");
  }

  @Test
  public void givenTooLongFileNameWhenValidateThenFails() {
    String fileName = generateRandomStringOfLength(500);
    assertThrows(
        InputException.InvalidParam.class, () -> isCorrectFileName(fileName), "Invalid parameter");
  }

  @Test
  public void givenUnexistingFileWhenValidateThenFails() {
    String fileName = generateRandomStringOfLength(5);
    assertThrows(
        InputException.NoFile.class,
        () -> isFileExisted(fileName),
        String.format("Given %s is not found", fileName));
  }

  @ParameterizedTest
  @NullSource
  public void givenNoInputFilesWhenValidateThenFails(String[] inputFiles) {
    assertThrows(
        InputException.NoData.class,
        () -> isCorrectInputFiles(inputFiles),
        "Empty input is not allowed");
  }
}
