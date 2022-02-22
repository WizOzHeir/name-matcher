package exception;

import java.util.Date;
import java.util.logging.Logger;

public class Handler implements Thread.UncaughtExceptionHandler {
  private static final Logger LOGGER = Logger.getLogger(Handler.class.getName());

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), t.getName(), e.getMessage());
    LOGGER.info(String.format("UncaughtException--->\n%s", errorDetails));
  }
}
