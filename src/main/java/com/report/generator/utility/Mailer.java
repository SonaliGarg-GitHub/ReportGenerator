package com.report.generator.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 *  {@summary This utility handles Message creation and sending the message to all its recipients. }
 *
 * @see	   javax.mail.Message
 * @see    Mailer
 * @author Sonali Garg
 *
 */
@Slf4j
@Component
public class Mailer {

    private  String SMTP_AUTH_USER;
    private  String SMTP_AUTH_PASSWORD;
    private  Properties properties;

    public Mailer(
            @Value("${auth.user:er.sonaligarg@gmail.com}") String smtpAuthUser, //sonaligarg170796@gmail.com : bbusfyolcfqwawln
            @Value("${auth.pwd:ppyxacyjvgjanymk}") String smtpAuthPassword,  //sstagmpwsuzqrmok //er.sonaligarg@gmail.com : ppyxacyjvgjanymk
            @Value("${mail.smtp.host:smtp.gmail.com}") String smtpHost,
            @Value("${mail.smtp.port:587}") String smtpPort,
            @Value("${mail.smtp.auth:true}") boolean smtpAuth,
            @Value("${mail.smtp.starttls.enable:true}") boolean smtpStartTLS) {
        this.SMTP_AUTH_USER = smtpAuthUser;
        this.SMTP_AUTH_PASSWORD = smtpAuthPassword;

        this.properties = new Properties();
        this.properties.put("mail.smtp.host", smtpHost);
        this.properties.put("mail.smtp.port", smtpPort);
        this.properties.put("mail.smtp.auth", smtpAuth);
        this.properties.put("mail.smtp.starttls.enable", smtpStartTLS);
        this.properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
    }

    public void init() {
        Session session = getSession(SMTP_AUTH_USER, SMTP_AUTH_PASSWORD);
        session.setDebug(true);
    }

    /**
     * Creates a new {@link Session} object for sending mail.
     *
     * @param authUser the SMTP authentication user
     * @param authPassword the SMTP authentication password
     * @return a new {@link Session
     * **/
    private Session getSession(String authUser, String authPassword) {
        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUser, authPassword);
            }
        });
    }

    /**
     * Set the recipient addresses to the {@link Message }
     *
     * @param message
     * @param emailTos the recipients to whom the message is supposed to be sent.
     * @param emailCcs the recipients in Carbon copy.
     * @param emailBccs the recipients in BCC
     * @return a new {@link Message } with valid recipient address
     * **/
    Message addRecipients(Message message, Map<Message.RecipientType, InternetAddress[]> recipients) throws MessagingException{

            message.setRecipients(Message.RecipientType.TO, recipients.get(Message.RecipientType.TO));
            message.setRecipients(Message.RecipientType.CC, recipients.get(Message.RecipientType.CC));
            message.setRecipients(Message.RecipientType.BCC, recipients.get(Message.RecipientType.BCC));
        return message;
    }

    /** Update the Sender Address & password - used for sending the mails.
     * this method allows you to update the server details too. But once the prop & Auth has been changed,
     * the same auth will be used to send all new emails.
     * @param smtpAuthUser
     * @param smtpAuthPassword
     * @param properties
     */
    void updateSenderDetails( String smtpAuthUser,String smtpAuthPassword, Optional<Properties> properties) {
        setupNewSender(smtpAuthUser, smtpAuthPassword);
        updatePropertyConfigIfNotNull(properties.orElse(null));
    }

    /** Update just the login details(on same server) - used for sending the mails.
     * @param smtpAuthUser
     * @param smtpAuthPassword
     */
    void setupNewSender(String smtpAuthUser,String smtpAuthPassword){
        this.SMTP_AUTH_USER = smtpAuthUser;
        this.SMTP_AUTH_PASSWORD = smtpAuthPassword;
    }
    /** Updates the mail server config'ed for sending messages, only if new properties are not null.
     * @param properties
     */
    void updatePropertyConfigIfNotNull(Properties properties){
        if(properties!=null)
        this.properties =properties;
    }

    /** Send a message. The message will be sent to all recipient addresses specified in the message.
     * @param emailFrom
     * @param recipients
     * @param attachment
     * @return
     */

    public boolean sendMail(InternetAddress emailFrom, Map<Message.RecipientType, InternetAddress[]> recipients, File attachment) {
        boolean result = false;

        try {
            //Create an empty message object.
            Message message = new MimeMessage(getSession(SMTP_AUTH_USER, SMTP_AUTH_PASSWORD));

            message.setSubject(getSubject());
            setBody(message,attachment);
            message.setFrom(emailFrom);
            addRecipients(message, recipients);

            //Send the message
            Transport.send(message);
            result = true;

        } catch (MessagingException e) {
            log.error("Error creating Message " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private String getSubject() {
        return "TEST MESSAGE";
    }

    private void setBody( Message message, File attachment) throws MessagingException, IOException {
        // Create the message body
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        //set Body content
        messageBodyPart.setContent(getMessageBody(), "text/html");
        multipart.addBodyPart(messageBodyPart);

        // Attach the file
        if (attachment != null) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(attachment);
            multipart.addBodyPart(attachmentBodyPart);
        }
        // Set the content of the message
        message.setContent(multipart);

    }

    String getMessageBody(){
        return "<html>\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <style>\n" +
                "      body {\n" +
                "        font-family: Calibri, sans-serif;\n" +
                "        font-size: 11pt;\n" +
                "        line-height: 1.5;\n" +
                "        color: #333;\n" +
                "      }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<p>Dear Team Supervisor,</p>\n" +
                "<p>Attached please find daily performance report of your team members for above-mentioned duration. The attached document is Excel file.</p> <p>File includes:</p>\n" +
                "<ul>\n" +
                "  <li>Productivity details</li>\n" +
                "  <li>Utilization details</li>\n" +
                "  <li>Attendance details</li>\n" +
                "  <li>Process Performance Summary</li>\n" +
                "</ul>\n" +
                "<p>This information should be distributed to all team members on daily basis and coached if need be. If you continue to see non-performance or non-adherence please escalation to your manager and seek his/her guidance on next steps.</p>\n" +
                "<p>If you have any questions regarding details published, please write to MitraBIteam@annovasolutions.com.</p>\n" +
                "<p>Sincerely,<br>MitraBIteam</p>\n" +
                "</body>\n" +
                "</html>";


    }}