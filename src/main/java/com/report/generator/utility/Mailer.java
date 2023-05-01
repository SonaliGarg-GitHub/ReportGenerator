package com.report.generator.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.mail.*;
import javax.mail.internet.*;
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
            @Value("${auth.user}") String smtpAuthUser, //sonaligarg170796@gmail.com : bbusfyolcfqwawln
            @Value("${auth.pwd}") String smtpAuthPassword,  //sstagmpwsuzqrmok //er.sonaligarg@gmail.com : ppyxacyjvgjanymk
            @Value("${mail.smtp.host}") String smtpHost,
            @Value("${mail.smtp.port}") String smtpPort,
            @Value("${mail.smtp.auth}") boolean smtpAuth,
            @Value("${mail.smtp.starttls.enable}") boolean smtpStartTLS) {
        this.SMTP_AUTH_USER = smtpAuthUser;
        this.SMTP_AUTH_PASSWORD = smtpAuthPassword;

        this.properties = new Properties();
        this.properties.put("mail.smtp.host", smtpHost);
        this.properties.put("mail.smtp.port", smtpPort);
        this.properties.put("mail.smtp.auth", smtpAuth);
        this.properties.put("mail.smtp.starttls.enable", smtpStartTLS);
        this.properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
    }

    public  Message getMessageTemplate() {
        return new MimeMessage(getSession());
    }

    public Session getSession() {
        Session session = getSession(SMTP_AUTH_USER, SMTP_AUTH_PASSWORD);
        session.setDebug(true);
        return session;
    }

    /**
     * Creates a new {@link Session} object for sending mail.
     *
     * @param authUser the SMTP authentication user
     * @param authPassword the SMTP authentication password
     * @return a new {@link Session
     * **/
    private Session  getSession(String authUser, String authPassword) {
        return Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUser, authPassword);
            }
        });
    }

    /**
     * Set the recipient addresses to the {@link Message }
     *
     * @param message
     * @param recipients : to, cc, bcc.

     * @return a new {@link Message } with valid recipient address
     * **/
    static Message addRecipients(Message message, Map<Message.RecipientType, InternetAddress[]> recipients) throws MessagingException{

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
}
