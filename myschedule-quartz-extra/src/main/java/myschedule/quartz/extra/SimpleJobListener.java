package myschedule.quartz.extra;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An adaptor class that provide simple DEBUG logging message for each method implemented.
 *
 * User: Zemian Deng
 * Date: 6/1/13
 */
public class SimpleJobListener implements JobListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getName() {
        return getClass().getName() + "@" + System.identityHashCode(this);
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        logger.debug("jobToBeExecuted context={}", context);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        logger.debug("jobExecutionVetoed context={}", context);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        logger.debug("jobWasExecuted context={}, jobException={}", context, jobException);
    }

    @Override
    public String toString() {
        return getName();
    }
}
