/**
 * Created by Таня on 08.11.2018.
 */

import java.awt.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import com.itextpdf.text.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.server.UID;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Properties;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.util.UidGenerator;

public class GUI extends JFrame {
    private JLabel labelStart = new JLabel("Препарат, принимаемый пациентом:");
    private JRadioButton metipred = new JRadioButton("Метипред 4 мг");
    private JRadioButton pred = new JRadioButton("Преднизолон 5 мг");
    private JRadioButton other = new JRadioButton("Другой препарат или доза");
    private JLabel labelNum = new JLabel("");
    private JTextField number = new JTextField(5);
    private JLabel labelName = new JLabel("ФИО пациента:");
    private JTextField name = new JTextField(20);
    private JButton go= new JButton("Рассчитать");
    private JLabel deb = new JLabel("");
    private JCheckBox needEvent = new JCheckBox("Да");
    private JLabel emailLabel = new JLabel("Email пациента");
    private JTextField email = new JTextField(20);
    private JButton send = new JButton("Отправить");
    private JLabel result =  new JLabel("");
    private JPanel sendBox = new JPanel();
    private JPanel numberBox = new JPanel();
    private JPanel nameBox = new JPanel();

    String type;
    String filename;

    LocalDate now = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("hh.mm.ss");
    Font font = new Font(BaseFont.createFont("Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED));
    Reminder reminder = new Reminder();
    String weeks2 = "Через 2 недели необходимо посетить врача.";
    String weeks1 = "Через 1 неделю необходимо посетить врача.";
    String tomorrow = "Завтра необходимо посетить врача.";
    String todayVisit = "В случае, если вы до сих пор не посетили врача, сделайте это как можно скорее, а пока принимайте препарат ежедневно в количестве";

    public GUI() throws IOException, DocumentException {
        this.setBounds(150,150,550,450);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(7,1));

        JPanel radioPanel = new JPanel(new GridLayout(3, 1));
        JPanel choosePanel = new JPanel(new GridLayout(1, 2));
        JPanel timePanel = new JPanel();
        JPanel typeTimePanel = new JPanel(new GridLayout(1, 1));
        JPanel emailPanel = new JPanel();
        typeTimePanel.add(needEvent);
        timePanel.add(deb);
        timePanel.add(typeTimePanel);
        emailPanel.add(emailLabel);
        emailPanel.add(email);
        send.setHorizontalAlignment(SwingConstants.CENTER);
        labelStart.setHorizontalAlignment(SwingConstants.RIGHT);
        sendBox.add(send, BorderLayout.CENTER);
        send.setHorizontalAlignment(SwingConstants.CENTER);
        numberBox.add(labelNum);
        numberBox.add(number);
        nameBox.add(labelName);
        nameBox.add(name);
        nameBox.add(go);
        radioPanel.add(metipred);
        radioPanel.add(pred);
        radioPanel.add(other);
        choosePanel.add(labelStart);
        choosePanel.add(radioPanel);
        labelNum.setHorizontalAlignment(SwingConstants.LEFT);
        numberBox.setAlignmentY(BOTTOM_ALIGNMENT);
        container.add(choosePanel);
        container.add(numberBox);
        container.add(nameBox);
        nameBox.setLayout(new FlowLayout());
        emailPanel.setLayout(new FlowLayout());
        timePanel.setLayout((new FlowLayout()));
        container.add(timePanel);
        container.add(emailPanel);
        container.add(sendBox);
        container.add(result);
        result.setHorizontalAlignment(SwingConstants.CENTER);

        numberBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        other.addActionListener(eventListenerStop);
        metipred.addActionListener(eventListenerNext);
        pred.addActionListener(eventListenerNext);
        send.addActionListener(eventListenerSend);
        go.addActionListener(eventListenerCount);

        ButtonGroup group = new ButtonGroup();
        group.add(metipred);
        group.add(pred);
        group.add(other);
        number.setVisible(false);
        name.setVisible(false);
        labelName.setVisible(false);
        go.setVisible(false);
        needEvent.setVisible(false);
        email.setVisible(false);
        send.setVisible(false);
        emailLabel.setVisible(false);
        send.setSize(30, 15);

        number.setSize(30, 20);
        name.setSize(30, 30);
        choosePanel.setSize(550, 100);

    }

    ActionListener eventListenerNext = (ActionEvent e) -> {
        labelNum.setText("Укажите количество таблеток, принимаемых пациентом в день:");
        if (pred.isSelected()) type = "преднизолон 5 мг";
        if (metipred.isSelected()) type = "метипред 4 мг";
        number.setVisible(true);
        deb.setVisible(false);
        name.setVisible(true);
        labelName.setVisible(true);
        labelNum.setVisible(true);
        go.setVisible(true);
    };

    ActionListener eventListenerStop = (ActionEvent e) -> {
        String text="К сожалению, данное приложение не может создать схему приема данного препарата.";
        deb.setText(text);
        deb.setVisible(true);
        number.setVisible(false);
        name.setVisible(false);
        labelName.setVisible(false);
        go.setVisible(false);
        labelNum.setVisible(false);
        needEvent.setVisible(false);
        send.setVisible(false);
        email.setVisible(false);
        emailLabel.setVisible(false);
        result.setVisible(false);
    };

    ActionListener eventListenerCount = (ActionEvent e) -> {
        reminder = new Reminder();
        deb.setVisible(true);
        labelNum.setVisible(true);
        needEvent.setVisible(false);
        send.setVisible(false);
        email.setVisible(false);
        emailLabel.setVisible(false);
        result.setVisible(false);
        if (name.getText().length()==0 ) {
            deb.setText("Укажите ФИО пациента.");
            return;
        }
        if (number.getText().length()==0 ) {
            deb.setText("Укажите количество таблеток.");
            return;
        }
        try {
            if ((Double.parseDouble(number.getText())<=0) || (Double.parseDouble(number.getText())>16)) {
                deb.setText("Неверное количество.");
                return;
            }
        }
        catch (NumberFormatException ex){
            deb.setText("Неверное количество.");
            return;
        }
        try {
            makePDF(number.getText(), name.getText(), type);
        }
        catch (DocumentException | IOException ex){
        }
        deb.setText("Нужны ли напоминания в календаре?");
        needEvent.setVisible(true);
        email.setVisible(true);
        result.setVisible(true);
        emailLabel.setVisible(true);
        send.setVisible(true);
    };

    ActionListener eventListenerSend = (ActionEvent e) -> {
        if (email.getText().length()==0 ) {
            result.setText("Укажите email пациента.");
            return;
        }
        if (!email.getText().matches("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$") ) {
            result.setText("Укажите верный email пациента.");
            return;
        }

        Mail mail = new Mail();
        mail.send(email.getText(), name.getText());

    };


    public void calc(String number, Document document, String name) {
        double numberCount = Double.parseDouble(number);
        int i;
        int days = 1;
        double all8 = (numberCount - 8 + 0.5) * 6;
        double all6 = (numberCount - 6 + 0.5) * 10;
        double all4 = (numberCount - 4 + 0.25) * 20;
        double all2 = (numberCount - 2 + 0.25) * 12;
        double all15 = (numberCount - 1.5 + 0.25) * 20;
        double all1 = (numberCount - 1.0 + 0.25) * 28;
        if (quater(numberCount)) {
            all6 -= 2.5;
            all8 -= 1.5;
        }
        try {
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Дата                       Количество таблеток в день", font));
            document.add(new Paragraph("----------------------------------------------------------------------------------"));
        }
        catch (DocumentException ex){
        }
        boolean parity =false;
        double check;
        int weeks=1;
        if (numberCount==1.0) weeks = 49;
        if (numberCount==0.75) weeks = 35;
        if (numberCount==0.50) weeks = 21;
        if (numberCount==0.25) weeks = 7;
        while (numberCount > 0 && numberCount <= 1.0) {
            if (!parity) {
                numberCount-=0.25;
                for (i = 1; i < 8; i++) {
                    if (i%2==1) {
                        ReminderEntry reminderEntry = new ReminderEntry();
                        reminderEntry.setDate(now.plusDays(days));
                        reminderEntry.setCount(numberCount);
                        if (weeks-days==14) reminderEntry.setText(weeks2);
                        if (weeks-days==7) reminderEntry.setText(weeks1);
                        if (weeks-days==1) reminderEntry.setText(tomorrow);
                        if (weeks-days==0) reminderEntry.setText("В случае, если вы до сих пор не посетили врача, сделайте это как можно скорее.");
                        if (weeks-days==-1) return;
                        days++;
                        reminder.getDays().add(reminderEntry);
                    }
                    if (i%2==0) {
                        ReminderEntry reminderEntry = new ReminderEntry();
                        reminderEntry.setDate(now.plusDays(days));
                        check=numberCount+0.25;
                        reminderEntry.setCount(check);
                        if (weeks-days==14) reminderEntry.setText(weeks2);
                        if (weeks-days==7) reminderEntry.setText(weeks1);
                        if (weeks-days==1) reminderEntry.setText(tomorrow);
                        if (weeks-days==0) reminderEntry.setText("В случае, если вы до сих пор не посетили врача, сделайте это как можно скорее.");
                        if (weeks-days==-1) return;
                        days++;
                        reminder.getDays().add(reminderEntry);
                    }
                }
                parity=true;
            }
            if (parity) {
                for (i = 1; i < 8; i++) {
                    ReminderEntry reminderEntry = new ReminderEntry();
                    reminderEntry.setDate(now.plusDays(days));
                    reminderEntry.setCount(numberCount);
                    if (weeks-days==14) reminderEntry.setText(weeks2);
                    if (weeks-days==7) reminderEntry.setText(weeks1);
                    if (weeks-days==1) reminderEntry.setText(tomorrow);
                    if (weeks-days==0) reminderEntry.setText("В случае, если вы до сих пор не посетили врача, сделайте это как можно скорее.");
                    if (weeks-days==-1) return;
                    days++;
                    reminder.getDays().add(reminderEntry);
                }
                parity = false;

            }
        }

        while (numberCount >= 1.0 && numberCount <= 1.5) {
            for (i = 1; i < 8; i++) {
                ReminderEntry reminderEntry = new ReminderEntry();
                reminderEntry.setDate(now.plusDays(days));
                reminderEntry.setCount(numberCount);
                if ((all1 - days) == 14) reminderEntry.setText(weeks2);
                if ((all1 - days) == 7)  reminderEntry.setText(weeks1);
                if ((all1 - days) == 1)  reminderEntry.setText(tomorrow);
                if ((all1 - days) == 0)  reminderEntry.setText(todayVisit+ " " + numberCount + " таблеток.");
                reminder.getDays().add(reminderEntry);
                days++;
            }
            numberCount -= 0.25;
        }

        while (numberCount >= 1.5 && numberCount <= 2) {
            for (i = 1; i < 6; i++) {
                ReminderEntry reminderEntry = new ReminderEntry();
                reminderEntry.setDate(now.plusDays(days));
                reminderEntry.setCount(numberCount);
                if ((all15 - days) == 14) reminderEntry.setText(weeks2);
                if ((all15 - days) == 7)  reminderEntry.setText(weeks1);
                if ((all15 - days) == 1)  reminderEntry.setText(tomorrow);
                if ((all15 - days) == 0)  reminderEntry.setText(todayVisit+ " " + numberCount + " таблеток.");
                reminder.getDays().add(reminderEntry);
                days++;
            }
            numberCount -= 0.25;
        }
        while (numberCount >= 2 && numberCount <= 4) {
            for (i = 1; i < 4; i++) {
                ReminderEntry reminderEntry = new ReminderEntry();
                reminderEntry.setDate(now.plusDays(days));
                reminderEntry.setCount(numberCount);
                if ((all2 - days) == 14)reminderEntry.setText(weeks2);
                if ((all2 - days) == 7) reminderEntry.setText(weeks1);
                if ((all2 - days) == 1) reminderEntry.setText(tomorrow);
                if ((all2 - days) == 0) reminderEntry.setText(todayVisit+ " " + numberCount + " таблеток.");
                days++;
                reminder.getDays().add(reminderEntry);
            }
            numberCount -= 0.25;
        }
        while (numberCount >= 4 && numberCount <= 6) {
            for (i = 1; i < 6; i++) {
                ReminderEntry reminderEntry = new ReminderEntry();
                reminderEntry.setDate(now.plusDays(days));
                reminderEntry.setCount(numberCount);
                if ((all4 - days) == 14)reminderEntry.setText(weeks2);
                if ((all4 - days) == 7) reminderEntry.setText(weeks1);
                if ((all4 - days) == 1) reminderEntry.setText(tomorrow);
                if ((all4 - days) == 0) reminderEntry.setText(todayVisit+ " " + numberCount + " таблеток.");
                days++;
                reminder.getDays().add(reminderEntry);
            }
            numberCount -= 0.25;
        }
        while (numberCount >= 6 && numberCount <= 8) {
            for (i = 1; i < 6; i++) {
                ReminderEntry reminderEntry = new ReminderEntry();
                reminderEntry.setDate(now.plusDays(days));
                reminderEntry.setCount(numberCount);
                if ((all6 - days) == 14)reminderEntry.setText(weeks2);
                if ((all6 - days) == 7) reminderEntry.setText(weeks1);
                if ((all6 - days) == 1) reminderEntry.setText(tomorrow);
                if ((all6 - days) == 0) reminderEntry.setText(todayVisit+ " " + numberCount + " таблеток.");
                days++;
                reminder.getDays().add(reminderEntry);
            }
            numberCount -= 0.5;
        }
        while (numberCount >= 8) {

            for (i = 1; i < 4; i++) {
                ReminderEntry reminderEntry = new ReminderEntry();
                reminderEntry.setDate(now.plusDays(days));
                reminderEntry.setCount(numberCount);
                if ((all8 - days) == 14)reminderEntry.setText(weeks2);
                if ((all8 - days) == 7) reminderEntry.setText(weeks1);
                if ((all8 - days) == 1) reminderEntry.setText(tomorrow);
                if ((all8 - days) == 0) reminderEntry.setText(todayVisit+ " " + numberCount + " таблеток.");
                days++;
                reminder.getDays().add(reminderEntry);
            }
            numberCount -= 0.5;
        }
    }

    public boolean quater(double num){

        double dif = num - Math.floor(num);
        return (dif > 0.74 && dif < 0.76) || (dif > 0.24 && dif < 0.26);
    }

    public void addImage(Document document, double numberCount) throws DocumentException, IOException {
        document.add(new Paragraph("\n"));
        String imageFull = "pic\\full.JPG";
        String imageHalf = "pic\\half.JPG";
        String imageOne = "pic\\1.JPG";
        String imageThree = "pic\\31.JPG";
        Image full = Image.getInstance(imageFull);
        Image half = Image.getInstance(imageHalf);
        Image one = Image.getInstance(imageOne);
        Image three = Image.getInstance(imageThree);
        int count = (int)numberCount;
        full.scaleToFit(25,25);
        half.scaleToFit(25,25);
        one.scaleToFit(25,25);
        three.scaleToFit(25,25);
        for (int i=0; i<count; i++){
            document.add(new Chunk(full, 0, 0, true));
        }
        double dif = numberCount - count;
        if (dif>0.74) document.add(new Chunk(three, 0, 0, true));
        if (dif>0.49 & dif <0.74) document.add(new Chunk(half, 0, 0, true));
        if (dif>0.24 & dif <0.49) document.add(new Chunk(one, 0, 0, true));
        document.add(new Paragraph("----------------------------------------------------------------------------------"));
    }


    public void makePDF(String number, String name, String type) throws DocumentException, IOException {
        LocalTime time  = LocalTime.now();
        Document document = new Document();
        filename = "res\\"+ name + " "  + time.format(formatterTime) + " " + now.format(formatter) +".pdf";
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        MyFooter event = new MyFooter();
        writer.setPageEvent(event);
        MyHeader header = new MyHeader();
        writer.setPageEvent(header);
        document.open();
        document.add(new Paragraph("ФИО пациента:    "+ name + "\n" + "Начальное количество таблеток (таб.): " + number, font));
        document.add(new Paragraph("Препарат: "+ type , font));
        calc(number, document, name);
        for (ReminderEntry reminderEntry : reminder.getDays()) {
            document.add(new Paragraph(reminderEntry.getDate().format(formatter) + "             " + reminderEntry.getCount() + "    таб.", font));
            document.add(new Paragraph(reminderEntry.getText(), font));
            addImage(document, reminderEntry.getCount());
        }
        document.close();

    }

    class Mail {

        public String makeEvent(){
            Calendar calendar = new Calendar();
            calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
            calendar.getProperties().add(net.fortuna.ical4j.model.property.Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            reminder.getDays().forEach(rem -> {
                if (rem.getCount()!=0){
                    VEvent event = null;
                    VEvent eventVisit = null;
                    try {
                        event = new VEvent(new Date(rem.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                                "dd-MM-yyyy"),
                                "Принять " + type + " в количестве: " + rem.getCount() + " таб. ");
                        if (rem.getText()!="")
                            eventVisit = new VEvent(new Date(rem.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                                    "dd-MM-yyyy"),
                                    rem.getText());
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                    String uidString = new UID().toString();
                    UidGenerator ug = () -> new net.fortuna.ical4j.model.property.Uid(uidString);
                    event.getProperties().add(ug.generateUid());
                    if (eventVisit != null) {
                        eventVisit.getProperties().add(ug.generateUid());
                    }

                    calendar.getComponents().add(event);
                    if (eventVisit != null) {
                        calendar.getComponents().add(eventVisit);
                    }
                }
            });

            String calendarFileName = "calendar" + ".ics";
            try {
                FileOutputStream fout = new FileOutputStream(calendarFileName);
                CalendarOutputter outputter = new CalendarOutputter();
                outputter.output(calendar, fout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return calendarFileName;
        }

        public void send(String address, String name) {
            final String username = "yourDoctor19@gmail.com";
            final String password = "vov8022018";

            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("yourDoctor19@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(address));
                message.setSubject("Схема лечения");
                Multipart multipart = new MimeMultipart();
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText("Добрый день!"
                        + "\n\n В приложениях в данному сообщению вы можете найти Вашу схему лечения.");
                MimeBodyPart attachmentBodyPart= new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(filename);
                if (needEvent.isSelected()) {
                    String calendarFileName = makeEvent();
                    MimeBodyPart calendarBodyPart = new MimeBodyPart();
                    DataSource calendarSource = new FileDataSource(calendarFileName);
                    calendarBodyPart.setDataHandler(new DataHandler(calendarSource));
                    calendarBodyPart.setFileName(calendarFileName);
                    multipart.addBodyPart(calendarBodyPart);
                }
                multipart.addBodyPart(textBodyPart);
                multipart.addBodyPart(attachmentBodyPart);
                message.setContent(multipart);
                Transport.send(message);
                result.setText("Сообщение отправлено!");

            } catch (MessagingException e) {
                result.setText("Проверьте подключение к интернету и повторите попытку");
            }
        }

    }

    class MyFooter extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase( "Фамилия врача:________________/_______", font);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footer,
                    (document.right()-20)  + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }

    class MyHeader extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase("" + writer.getPageNumber(), font);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    header,
                    document.right() - 30 + document.leftMargin(),
                    document.top() - 10, 0);
        }
    }


    public static void main(String[] args) throws IOException, DocumentException {
        GUI app = new GUI();
        app.setVisible(true);
    }
}