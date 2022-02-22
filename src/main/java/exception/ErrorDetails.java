package exception;

import java.util.Date;

public class ErrorDetails {
  private Date timestamp;
  private String threadName;
  private String message;

  public ErrorDetails(Date timestamp, String threadName, String message) {
    this.timestamp = timestamp;
    this.threadName = threadName;
    this.message = message;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public String getThreadName() {
    return this.threadName;
  }

  public String getMessage() {
    return this.message;
  }

  @Override
  public String toString() {
    return getTimestamp() + "-> |" + getThreadName() + " | " + getMessage();
  }
}
