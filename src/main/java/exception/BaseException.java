package exception;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    String timeString = timeFormat.format(new Date());
    return String.format(
        "[%s] %s in thread \"%s\" - %s",
        timeString, BaseException.class.getName(), Thread.currentThread().getName(), getMsg());
  }
}
