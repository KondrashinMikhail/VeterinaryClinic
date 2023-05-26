package mailLogic;

import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalTime;
import java.util.Date;

@AllArgsConstructor
public class AutomaticJob implements Job {
    private final String recipient;
    private final String subject;
    private final String text;

    public void execute(JobExecutionContext context) {
        System.out.println("AutomaticJob --------------------> " + LocalTime.now());

        //Select from database all users, and check visit data -> send mail to user, if less than one day left

        MailSender.sendMail(recipient, subject, text);
        System.out.println("---------- Mail sent successfully ----------");
    }
}
