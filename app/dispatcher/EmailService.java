package dispatcher;

import com.google.inject.Inject;
import play.Configuration;
import play.libs.concurrent.HttpExecution;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Http;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by Fahad on 23-10-2017.
 */
public class EmailService {
    final String userName;
    final String pwd;
    @Inject MailerClient mailerClient;

    @Inject
    public EmailService(Configuration configuration) {
        this.userName = configuration.getString("email.user");
        this.pwd = configuration.getString("email.pwd");
    }
    public void sendEmail() {
        String cid = "1234";
        Email email = new Email()
                .setSubject("Simple email")
                .setFrom("Mister FROM <fahadcv@gmail.com>")
                .addTo("Miss TO <fahadcv@gmail.com>")
                // adds attachment
//                .addAttachment("attachment.pdf", new File("/some/path/attachment.pdf"))
//                // adds inline attachment from byte array
//                .addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
//                // adds cid attachment
//                .addAttachment("image.jpg", new File("/some/path/image.jpg"), cid)
                // sends text, HTML or both...
                .setBodyText("A text message")
                .setBodyHtml("<html><body><p>An <b>html</b> message with cid <img src=\"cid:" + cid + "\"></p></body></html>");
        mailerClient.send(email);
    }
    public CompletionStage<Void> sendEmail(String to, String from, String subject, String messageBody) {
        return sendEmail(to, from, subject, messageBody, null, null);
    }

//    public CompletionStage<Void> sendEmail(String to, String from, String subject, String messageBody, Http.MultipartFormData.FilePart<File> attachment) {
//        System.out.println("Starting sendEmail At " + Runtime.getRuntime().toString());
//        return CompletableFuture.supplyAsync(() -> {
//
//            Properties props = new Properties();
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.port", "587");
//
//            Session session = javax.mail.Session.getInstance(props,
//                    new javax.mail.Authenticator() {
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication(username, password);
//                        }
//                    });
//
//            try {
//
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress(from));
//                message.setRecipients(Message.RecipientType.TO,
//                        InternetAddress.parse(to));
//                message.setSubject(subject);
//
//                if(attachment != null) {
////                    MimeMessage messageBodyPart = new MimeMessage(session);
////                    MultiPartEmail
////                    DataSource source = new FileDataSource(attachment);
////                    messageBodyPart.setDataHandler(new DataHandler(source));
////                    messageBodyPart.setFileName(filename);
////                    message.setContent(messageBodyPart);
//
//
//                    Multipart multipart = new MimeMultipart();
//// creates body part for the message
//                    MimeBodyPart messageBodyPart = new MimeBodyPart();
//                    messageBodyPart.setText(messageBody);
//// creates body part for the attachment
//                    MimeBodyPart attachPart = new MimeBodyPart();
//                    attachPart.attachFile(attachment);
//// code to add attachment...will be revealed later
//// adds parts to the multipart
//                    multipart.addBodyPart(messageBodyPart);
//                    multipart.addBodyPart(attachPart);
//// sets the multipart as message's content
//                    message.setContent(multipart);
////                } else {
////                    message.setText(messageBody);
//                    System.out.println("attachment -> " + attachment);
//                    System.out.println("message -> " + message);
//                } else {
//                    System.out.println("attachment -> " + attachment);
//                    message.setText(messageBody);
//                }
//
//                Transport transport = session.getTransport("smtps");
//                transport.send(message);
//
//                System.out.println("Done At " + Runtime.getRuntime().toString());
//
//            } catch (MessagingException | IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            return null;
//            //TODO remove the dependency by injection : HttpExecution.defaultContext()
//        }, HttpExecution.defaultContext());
//    }
    public CompletionStage<Void> sendEmail(String to, String from, String subject, String messageBody, File attachment, String fileName) {
        System.out.println("Starting sendEmail At " + Runtime.getRuntime().toString());
        return CompletableFuture.supplyAsync(() -> {

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = javax.mail.Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(userName, pwd);
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));
                message.setSubject(subject);

                if(attachment != null) {
//                    MimeMessage messageBodyPart = new MimeMessage(session);
//                    MultiPartEmail
//                    DataSource source = new FileDataSource(attachment);
//                    messageBodyPart.setDataHandler(new DataHandler(source));
//                    messageBodyPart.setFileName(filename);
//                    message.setContent(messageBodyPart);


                    Multipart multipart = new MimeMultipart();
// creates body part for the message
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText(messageBody);
// creates body part for the attachment
                    MimeBodyPart attachPart = new MimeBodyPart();
                    attachPart.attachFile(attachment);//, contentType, encoding);
                    attachPart.setFileName(fileName);
// code to add attachment...will be revealed later
// adds parts to the multipart
                    multipart.addBodyPart(messageBodyPart);
                    multipart.addBodyPart(attachPart);
// sets the multipart as message's content
                    message.setContent(multipart);
//                } else {
//                    message.setText(messageBody);
                    System.out.println("attachment -> " + attachment);
                    System.out.println("message -> " + message);
                } else {
                    System.out.println("attachment -> " + attachment);
                    message.setText(messageBody);
                }

                Transport transport = session.getTransport("smtps");
                transport.send(message);

                System.out.println("Done At " + Runtime.getRuntime().toString());

            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }

            return null;
            //TODO remove the dependency by injection : HttpExecution.defaultContext()
        }, HttpExecution.defaultContext());
    }

}
