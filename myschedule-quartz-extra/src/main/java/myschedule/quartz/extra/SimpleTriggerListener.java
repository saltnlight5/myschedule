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
public class SimpleTriggerListener implements TriggerListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getName() {
        return getClass().getName() + "@" + System.identityHashCode(this);
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        logger.debug("triggerFired trigger={}, context={}", trigger, context);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        logger.debug("vetoJobExecution trigger={}, context={}", trigger, context);
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        logger.debug("triggerMisfired trigger={}", trigger);
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        logger.debug("triggerComplete trigger={}, context={}, triggerInstructionCode={}",
                new Object[]{trigger, context, triggerInstructionCode});
    }

    @Override
    public String toString() {
        return getName();
    }
}
