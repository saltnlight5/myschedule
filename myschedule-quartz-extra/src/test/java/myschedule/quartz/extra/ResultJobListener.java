package myschedule.quartz.extra;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultJobListener implements JobListener {
    public static Result result = new Result();

    public static void resetResult() {
        result = new Result();
    }

    @Override
    public String getName() {
        return "TestJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        result.jobToBeExecutedTimes.add(new Date());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        result.jobExecutionVetoedTimes.add(new Date());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        result.jobWasExecutedTimes.add(new Object[]{new Date(), context, jobException});
        result.jobResults.add(context.getResult());
    }

    public static class Result {
        public List<Date> jobToBeExecutedTimes = new ArrayList<Date>();
        public List<Date> jobExecutionVetoedTimes = new ArrayList<Date>();
        public List<Object[]> jobWasExecutedTimes = new ArrayList<Object[]>();
        public List<Object> jobResults = new ArrayList<Object>();
    }

}
