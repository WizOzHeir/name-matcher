package exception;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public abstract class BaseException extends Exception {
  private String msg;

  public BaseException(String message) {
    this.msg = message;
  }

  public BaseException() {
    this("Unknown error");
  }

  public String getMsg() {
    return msg;
  }

  public String showWithTime() {
    LocalDateTime now = LocalDateTime.now();
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    String timeString = format.format(now);
    return String.format("[%s] %s", timeString, getMessage());
  }
}
