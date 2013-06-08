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

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
     * is unscheduled.
     * </p>
     */
    @Override
    public void jobUnscheduled(String triggerName, String triggerGroup) {
        logger.debug("jobUnscheduled {}.{}", triggerName, triggerGroup);
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        logger.debug("triggerFinalized {}", trigger);
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code>
     * or group of <code>{@link org.quartz.Trigger}s</code> has been paused.
     * </p>
     * <p/>
     * <p>
     * If a group was paused, then the <code>triggerName</code> parameter
     * will be null.
     * </p>
     */
    @Override
    public void triggersPaused(String triggerName, String triggerGroup) {
        logger.debug("triggersPaused {}.{}", triggerName, triggerGroup);
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.Trigger}</code>
     * or group of <code>{@link org.quartz.Trigger}s</code> has been un-paused.
     * </p>
     * <p/>
     * <p>
     * If a group was resumed, then the <code>triggerName</code> parameter
     * will be null.
     * </p>
     */
    @Override
    public void triggersResumed(String triggerName, String triggerGroup) {
        logger.debug("triggersResumed {}.{}", triggerName, triggerGroup);
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        logger.debug("jobAdded {}", jobDetail);
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
     * has been deleted.
     * </p>
     */
    @Override
    public void jobDeleted(String jobName, String groupName) {
        logger.debug("jobDeleted {}.{}", jobName, groupName);
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
     * or group of <code>{@link org.quartz.JobDetail}s</code> has been
     * paused.
     * </p>
     * <p/>
     * <p>
     * If a group was paused, then the <code>jobName</code> parameter will be
     * null. If all jobs were paused, then both parameters will be null.
     * </p>
     */
    @Override
    public void jobsPaused(String jobName, String jobGroup) {
        logger.debug("jobsPaused {}.{}", jobName, jobGroup);
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
     * or group of <code>{@link org.quartz.JobDetail}s</code> has been
     * un-paused.
     * </p>
     * <p/>
     * <p>
     * If a group was resumed, then the <code>jobName</code> parameter will
     * be null. If all jobs were paused, then both parameters will be null.
     * </p>
     */
    @Override
    public void jobsResumed(String jobName, String jobGroup) {
        logger.debug("jobsResumed {}.{}", jobName, jobGroup);
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
    public void schedulerShutdown() {
        logger.debug("schedulerShutdown");
    }

    @Override
    public void schedulerShuttingdown() {
        logger.debug("schedulerShuttingdown");
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + System.identityHashCode(this);
    }
}
