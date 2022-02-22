package com.homeproject.namematcher.app;

import java.util.logging.Logger;

import com.homeproject.namematcher.util.NameMatcherViewer;
import picocli.CommandLine;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public Main(String[] arguments) {
    Args args = parseOptions(arguments);

    if (args.isRunningMode()) {
      LOGGER.info("Start the application");
      NameMatcher nameMatcher = new NameMatcher(args.getInputName(), args.getInputFiles());
      nameMatcher.start();
      NameMatcherViewer viewer = new NameMatcherViewer(nameMatcher);
      viewer.getView(args.getOutputDirName());
      LOGGER.info("Application finished");
    }
  }

  private Args parseOptions(String[] arguments) {
    Args args = new Args();
    CommandLine commandline = new CommandLine(args);
    commandline.parseArgs(arguments);
    if (args.isShowVersion()) commandline.printVersionHelp(System.out);
    if (args.isShowHelp()) args.showHelp(commandline);
    return args;
  }

  public static void main(String[] args) {
    new Main(args);
  }
}
