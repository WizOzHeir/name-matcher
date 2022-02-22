package com.homeproject.namematcher.util;

import static com.homeproject.namematcher.util.Checker.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import com.homeproject.namematcher.lucene.Indexer;

public class FileUtil {
  private static final Logger LOGGER = Logger.getLogger(Indexer.class.getName());

  FileUtil() {}

  public static File getFileFromFileName(String fileName, Class<?> clazz) {
    LOGGER.info(String.format("Get file %s", fileName));

    ClassLoader classLoader = clazz.getClassLoader();
    return new File(classLoader.getResource(fileName).getFile());
  }

  public static File createDirectory(String dirName) throws IOException {
    LOGGER.info(String.format("Creating directory %s", dirName));

    isCorrectFileName(dirName);
    String jarFilePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    String targetPath = Paths.get(jarFilePath).getParent().toString();
    File targetDir = new File(targetPath, dirName);
    if (!targetDir.exists()) {
      Files.createDirectory(targetDir.toPath());
    }
    return targetDir;
  }

  public static List<String> readFromIOStream(InputStream ioStream) throws IOException {
    LOGGER.info("Creating array of strings from the stream");

    ArrayList<String> lines = new ArrayList<>();
    String line;

    try (InputStreamReader isr = new InputStreamReader(ioStream);
        BufferedReader br = new BufferedReader(isr); ) {
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
    }
    return lines;
  }

  public static InputStream readAsIOStream(String fileName, Class<?> clazz) throws IOException {
    LOGGER.info(String.format("Reading %s as stream", fileName));

    InputStream ioStream = clazz.getClassLoader().getResourceAsStream(fileName);
    if (ioStream == null) {
      throw new FileNotFoundException(String.format("<%s> not found", fileName));
    }
    return ioStream;
  }

  public static void writeInFile(File outputFileName, Set<String> strs) throws IOException {
    LOGGER.info("Writing in the file");

    try (FileWriter fileWriter = new FileWriter(outputFileName);
        PrintWriter printWriter = new PrintWriter(fileWriter)) {
      for (String str : strs) {
        printWriter.println(str);
      }
    }
  }
}
