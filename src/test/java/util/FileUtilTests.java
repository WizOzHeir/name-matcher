package util;

import exception.InputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.homeproject.namematcher.util.FileUtil.*;
import static helper.NiceTestHelper.readFromInputStream;
import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTests {
  private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));

  @Test
  void givenFileNameWhenUsingClassloaderThenFileData() throws IOException {
    String expectedData = "Hello, world!";

    File file = getFileFromFileName("hello.txt", getClass());
    InputStream inputStream = new FileInputStream(file);
    String data = readFromInputStream(inputStream);

    assertEquals(expectedData, data.trim());
  }

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
}
