package exception;

import static com.homeproject.namematcher.util.Constants.MIN_INPUT_NAME_LEN;

public class InputException {
  public static class NoFile extends BaseException {
    public NoFile(String filename) {
      super(String.format("Given %s is not found", filename));
    }
  }

  public static class NoData extends BaseException {
    public NoData() {
      super("Empty input is not allowed");
    }
  }

  public static class InvalidParam extends BaseException {
    public InvalidParam() {
      super("Invalid parameter");
    }

    public String getHelpMessage() {
      return String.format("A valid name for input must be at least %d characters in the name", MIN_INPUT_NAME_LEN);
    }
  }
}
