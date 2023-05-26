package mailLogic;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class AutomaticMailSender {
    public AutomaticMailSender() {
        try {
            JobDetail job = JobBuilder.newJob(AutomaticJob.class).withIdentity("job", "group").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", "group").withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(10).withRepeatCount(1)).build();
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println(e.getMessage());
        }
    }
}
