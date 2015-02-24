package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.compat.log.AbstractLogger;
import net.spy.memcached.compat.log.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: jinglongyang
 */
public class SLF4JLogger extends AbstractLogger {
    private final Logger logger;

    /**
     * Get an instance of the SLF4JLogger.
     */
    public SLF4JLogger(String name) {
        super(name);
        logger = LoggerFactory.getLogger(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * Wrapper around SLF4J logger facade.
     *
     * @param level   net.spy.compat.log.Level level.
     * @param message object message
     * @param e       optional throwable
     */
    @Override
    public void log(Level level, Object message, Throwable e) {
        if (level == null) {
            level = Level.FATAL;
        }
        String msg = message == null ? "null" : message.toString();
        switch (level) {
            case DEBUG:
                logger.debug(msg, e);
                break;
            case INFO:
                logger.info(msg, e);
                break;
            case WARN:
                logger.warn(msg, e);
                break;
            case ERROR:
                logger.error(msg, e);
                break;
            case FATAL:
                logger.error(msg, e);
                break;
            default:
                logger.error(String.format("Unhandled Logging Level: [%s] with log message: [%s]", level.toString(), msg), e);
        }
    }
}
