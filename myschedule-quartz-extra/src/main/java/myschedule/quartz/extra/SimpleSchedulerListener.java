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
public class SimpleSchedulerListener implements SchedulerListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void jobScheduled(Trigger trigger) {
        logger.debug("jobScheduled {}", trigger);
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        logger.debug("jobUnscheduled {}", triggerKey);
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        logger.debug("triggerFinalized {}", trigger);
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        logger.debug("triggersPaused {}", triggerKey);
    }

    @Override
    public void triggersPaused(String triggerGroup) {
        logger.debug("triggersPaused {}", triggerGroup);
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        logger.debug("triggerResumed {}", triggerKey);
    }

    @Override
    public void triggersResumed(String triggerGroup) {
        logger.debug("triggersResumed {}", triggerGroup);
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        logger.debug("jobAdded {}", jobDetail);
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        logger.debug("jobDeleted {}", jobKey);
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        logger.debug("jobsPaused {}", jobKey);
    }

    @Override
    public void jobsPaused(String jobGroup) {
        logger.debug("jobsPaused {}", jobGroup);
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        logger.debug("jobsResumed {}", jobKey);
    }

    @Override
    public void jobsResumed(String jobGroup) {
        logger.debug("jobsResumed {}", jobGroup);
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        logger.debug("schedulerError msg={}, cause={}", msg, cause);
    }

    @Override
    public void schedulerInStandbyMode() {
        logger.debug("schedulerInStandbyMode");
    }

    @Override
    public void schedulerStarted() {
        logger.debug("schedulerStarted");
    }

    @Override
    public void schedulerStarting() {
        logger.debug("schedulerStarting");
    }

    @Override
    public void schedulerShutdown() {
        logger.debug("schedulerShutdown");
    }

    @Override
    public void schedulerShuttingdown() {
        logger.debug("schedulerShuttingdown");
    }

    @Override
    public void schedulingDataCleared() {
        logger.debug("schedulingDataCleared.");
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + System.identityHashCode(this);
    }
}
