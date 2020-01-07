package lu.uni.lcsb.vizbin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.*;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Custom log4j {@link Appender}. This class is used to store logs in the
 * memory. Such logs should be stored for short period of time. Only logs for
 * the {@link Thread} that created the object will be stored.
 * 
 * @author Piotr Gawron
 *
 */
public class EventStorageLoggerAppender extends AppenderSkeleton {

  /**
   * List of {@link LogLevel#DEBUG} logs.
   */
  private List<LoggingEvent> debugEvents = new ArrayList<>();

  /**
   * List of {@link LogLevel#INFO} logs.
   */
  private List<LoggingEvent> infoEvents = new ArrayList<>();

  /**
   * List of {@link LogLevel#WARN} logs.
   */
  private List<LoggingEvent> warnEvents = new ArrayList<>();

  /**
   * List of {@link LogLevel#ERROR} logs.
   */
  private List<LoggingEvent> errorEvents = new ArrayList<>();

  /**
   * List of {@link LogLevel#FATAL} logs.
   */
  private List<LoggingEvent> fatalEvents = new ArrayList<>();

  /**
   * List of logs with unknown log level.
   */
  private List<LoggingEvent> otherEvents = new ArrayList<>();

  /**
   * Identifier of {@link Thread} that created this object.
   */
  private long threadId;

  /**
   * Flag that describe if we log only entries for current thread (
   * <code>true</code>) or for all threads (<code>false</code>).
   */
  private boolean currentThreadLogOnly = true;

  /**
   * Default constructor.
   */
  public EventStorageLoggerAppender() {
    this(true);
  }

  /**
   * Default constructor.
   * 
   * @param currentThreadLogOnly
   *          if <code>true</code> logs should be taken only from thread that
   *          created object, if <code>false</code> all logs will be stored
   */
  public EventStorageLoggerAppender(boolean currentThreadLogOnly) {
    this.threadId = Thread.currentThread().getId();
    this.currentThreadLogOnly = currentThreadLogOnly;
  }

  @Override
  protected void append(LoggingEvent event) {
    // store information for all thread only if it is flagged by
    // currentThreadLogOnly, if not only logs from current thread should be
    // stored
    if (!currentThreadLogOnly || threadId == Thread.currentThread().getId()) {
      if (event.getLevel().equals(Level.DEBUG)) {
        debugEvents.add(event);
      } else if (event.getLevel().equals(Level.INFO)) {
        infoEvents.add(event);
      } else if (event.getLevel().equals(Level.WARN)) {
        warnEvents.add(event);
      } else if (event.getLevel().equals(Level.ERROR)) {
        errorEvents.add(event);
      } else if (event.getLevel().equals(Level.FATAL)) {
        fatalEvents.add(event);
      } else {
        otherEvents.add(event);
      }
    }
  }

  @Override
  public void close() {
  }

  @Override
  public boolean requiresLayout() {
    return false;
  }

  /**
   * Returns list of warning logs.
   * 
   * @return list of warning logs
   */
  public List<LoggingEvent> getWarnings() {
    return warnEvents;
  }

  /**
   * Returns list of warning logs.
   * 
   * @return list of warning logs
   */
  public List<LoggingEvent> getInfos() {
    return infoEvents;
  }

  /**
   * Returns list of error logs.
   * 
   * @return list of error logs
   */
  public List<LoggingEvent> getErrors() {
    return errorEvents;
  }

  /**
   * Returns list of fatal logs.
   * 
   * @return list of fatal logs
   */
  public List<LoggingEvent> getFatals() {
    return fatalEvents;
  }
}