package mailLogic;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalTime;
import java.util.Date;

public class AutomaticJob implements Job {
    public void execute(JobExecutionContext context) {
        System.out.println("AutomaticJob --------------------> " + LocalTime.now());

        //Select from database all users, and check visit data -> send mail to user, if less than one day left

        MailSender.sendMail("kondrashin.mihail@mail.ru", "Subject from " + new Date(), "This is done to check");
        System.out.println("---------- Mail sent successfully ----------");
    }
}
