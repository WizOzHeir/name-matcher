package com.homeproject.namematcher.app;

import com.homeproject.namematcher.util.VersionProvider;
import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(
    name = "start",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class,
    description = "Compare given name against blacklist. At least 2 letters.")
public class Args {
  @Parameters(index = "0", description = "The name to match")
  private String inputName;

  @Parameters(index = "1..*", arity = "1..2", description = "Files (1-2) for matching")
  private String[] inputFiles;

  @Option(
      names = {"-o", "--out"},
      description = "Output directory (default: print to console)")
  private String outputDirName;

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Display this help message")
  private boolean showHelp = false;

  @Option(
      names = {"-v", "--version"},
      versionHelp = true,
      description = "Display version info")
  private boolean showVersion = false;

  public String getInputName() {
    return inputName;
  }

  public String[] getInputFiles() {
    return inputFiles;
  }

  public String getOutputDirName() {
    return outputDirName;
  }

  public boolean isRunningMode() {
    return !isShowVersion() && !isShowHelp();
  }

  public boolean isShowHelp() {
    return showHelp;
  }

  public boolean isShowVersion() {
    return showVersion;
  }

  public void showHelp(CommandLine commandLine) {
    commandLine.usage(System.out);
  }
}
