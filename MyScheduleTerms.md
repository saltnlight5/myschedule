The `MySchedule` is using Quartz scheduling system, and it might use many terms that are confusing. The action links used in the Web UI are mostly based on the terms used by Quartz. Here list some frequently used terms and hopefully clear up the confusion.

`Scheduler`
> The software application system that responsible to store, run and manage scheduling jobs. In case of `MySchedule` application, we are using an instance of Quartz's Scheduler implementation.

`Job`
> A job to a user is what's scheduled in the system that will do some work. But to Quartz, it means two things combined: `JobDetail` and `Trigger`.

`JobDetail`
> A job definition that must contains a Java class that implements a `org.quartz.Job` interface. We do not create instance of the actual job Class, but just provide the class name. Quartz system will automatically create the job instance when job is fired/run. A `Jobdetail` is identified by a job name and job group. Sometimes Quartz documentation would like to refer "job" as instance of `JobDetail`.

`Trigger`
> A `Trigger` tells scheduler when to fire, run or execute a associated `JobDetail`. A `JobDetail` may be added to the scheduler as standalone without any trigger. But a `Trigger` must always has a `JobDetail` associated. A `Trigger` is identified by a trigger name and a trigger group.

`ScheduleJob`
> Adding an `JobDetail` and a `Trigger` to the scheduler system.

`UnscheduleJob`
> Removing an `Trigger` from the scheduler. If the `JobDetail` that this trigger associate has no more trigger and it's not durable, then the `JobDetail` will be removed too. If the `JobDetail` is durable, then it will remain in the scheduler system until user explicitly `DeleteJob` it.

`DeleteJob`
> Removing the `JobDetail` and all of its associated `Trigger`s.