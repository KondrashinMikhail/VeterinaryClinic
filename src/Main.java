import form.LoginForm;
import storage.Database;
import storage.models.Visit;

import javax.swing.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        new LoginForm(null);
        //new InformationForm(null);


//        new AutomaticMailSender();
//
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY, 14);
//        c.set(Calendar.MINUTE, LocalTime.now().getMinute());
//        c.set(Calendar.SECOND, LocalTime.now().getSecond() + 10);
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //Call your method here
//                //setEmail(emailContent, subject);
//                MailSender.sendMail("kondrashin.mihail@mail.ru", "First Java app Email", "Hello, friend, we are glad to see you in our veterinary clinic");
//            }
//        }, c.getTime(), 86400000);
//
//
//        MailSender.sendMail("kondrashin.mihail@mail.ru", "First Java app Email", "Hello, friend, we are glad to see you in our veterinary clinic");
//
//
//
//        List<Integer> list = new ArrayList<>();
//        TreeSet<Integer> tree = new TreeSet<>();
//
//        long allListTime = 0;
//        long allTreeTime = 0;
//        Integer randomElementToFind = new Random().nextInt(1000);
//        Integer listElement = 10000000;
//        Integer treeElement = 10000000;
//
//        for (int j = 0; j < 100000; j++) {
//
//            for (int i = 0; i <= 1000; i++) list.add(i);
//            for (int i = 0; i <= 1000; i++) tree.add(i);
//
//            long startTimeList = System.nanoTime();
//            listElement = list.get(list.indexOf(randomElementToFind));
//            long endTimeList = System.nanoTime();
//            allListTime += (endTimeList - startTimeList);
//
//
//            long startTimeTree = System.nanoTime();
//            treeElement = tree.floor(randomElementToFind);
//            long endTimeTree = System.nanoTime();
//            allTreeTime += (endTimeTree - startTimeTree);
//        }
//
//        System.out.println(randomElementToFind);
//
//        System.out.println("Список: " + allListTime / 100000. + " нс; Элемент: " + listElement);
//        System.out.println("Дерево: " + allTreeTime / 100000. + " нс; Элемент: " + treeElement);
//
//
//
//
//        TreeSet<Users> animals = Database.getInstance().select(Users.builder().build());
//        for (Users animal : animals) {
//            System.out.println(animal.toString());
//        }
//        Database.getInstance().insertOrUpdate(
//                Animal.builder()
//                        .name("Cat")
//                        .species(PossibleAnimalSpecies.Cat.ordinal())
//                        .breed("British cat")
//                        .age(10)
//                        .client_id(1)
//                        .build());
//
//        Database.getInstance().insertOrUpdate(Users.builder()
//                .id(1)
//                .name("Mikhail")
//                .login("mk")
//                .mail("kondrashin.mihail@mail.ru")
//                .role(UserRole.Client.ordinal())
//                .build());
//
//
//        Animal animal = Animal.builder().build();
//        System.out.println(animal.toString());
//
//        Database.getInstance().delete(Animal.class, 2);
//
//        Timestamp timestamp = new Timestamp(0);
//        timestamp.setYear(2002 - 1900);
//        timestamp.setMonth(9 - 1);
//        timestamp.setDate(30);
//        timestamp.setHours(14);
//        timestamp.setMinutes(0);
//        System.out.println("Custom time with 'Timestamp': " + timestamp);
//
//        System.out.println(timestamp.getMonth() + 1);

//        Database.getInstance().insertOrUpdate(Visit.builder().date(timestamp).comment("Test visit").isClientCame(false).doctor_id(1).build());
    }
}