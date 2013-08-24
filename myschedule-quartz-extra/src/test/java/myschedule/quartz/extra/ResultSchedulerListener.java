package myschedule.quartz.extra;

import org.quartz.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultSchedulerListener extends SimpleSchedulerListener {
    public static Result result = new Result();

    public static void resetResult() {
        result = new Result();
    }

    public static class Result {
        public List<Object[]> scheduleErrorTimes = new ArrayList<Object[]>();
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        result.scheduleErrorTimes.add(new Object[]{new Date(), msg, cause});
    }

}
