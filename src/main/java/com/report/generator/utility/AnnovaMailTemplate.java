package com.report.generator.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.report.generator.utility.Mailer.*;

/**
 * @author gargS
 * Created on 30-04-2023.
 */
@Slf4j
@Component
public class AnnovaMailTemplate {

    @Autowired
    Mailer mailer;


    /** Send a message. The message will be sent to all recipient addresses specified in the message.
     * @param report
     * @param replacements
     * @return
     */
    public  boolean sendMail(String report, Map<String, Object> replacements) throws MessagingException, IOException {

        try {

            Map<String, Object>  config = loadEmailTemplate("email.templates."+report);

            Message message = mailer.getMessageTemplate();
            String emailFrom = replaceFields(replacements, config.get( "from"));
            String senderName = replaceFields(replacements, config.get( "sender_name"));
            InternetAddress sender = new InternetAddress(emailFrom, senderName);
            message.setFrom(sender);
            String to = replaceFields(replacements, config.get( "to"));
            List<String> tos = Lists.newArrayList();
            tos.add(to);
            List<String> cc = (List<String>) config.get( "cc");
            addRecipients(message, getRecipients(tos,cc));

            message.setSubject(replaceFields(replacements, config.get( "subject")));
            message.setContent(getBody(config,replacements));

            //Send the message
            Transport.send(message);

            return true;
        } catch (MessagingException e ) {
            log.error("Error creating Message " + e.getMessage());
            throw e;
        }catch ( IOException e){
            log.error("Unable to attach File " + e.getMessage());
            throw e;
        }
    }

    public  Map<String, Object> loadEmailTemplate(String templateName) {

        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("application-messgae.yaml"); // Replace with your YAML file path
        return (Map<String, Object>) getTemplate( inputStream,  templateName);
    }

    private Map<Message.RecipientType, InternetAddress[]> getRecipients(List<String> emailTos,  List<String> emailCcs ) {

            Map<Message.RecipientType, InternetAddress[]> recipients = new HashMap<>();

            InternetAddress[] tos = getInternetAddresses(emailTos);
            InternetAddress[] ccs = getInternetAddresses(emailCcs);

            recipients.put(Message.RecipientType.TO, tos);
            recipients.put(Message.RecipientType.CC, ccs);

            return recipients;
    }


    private InternetAddress[] getInternetAddresses(List<String> emails) {
        int i = 0;
        InternetAddress[] validEmails = null;
        if (emails != null) {
            validEmails = new InternetAddress[emails.size()];
            for (String mail : emails) {
                try {
                    validEmails[i++] = new InternetAddress(mail);
                } catch (AddressException ex) {
                    log.warn(" Skipping Invalid Email address : {},  {}", mail, ex.getMessage());
                }
            }
        }
        return validEmails;
    }

    private MimeMultipart getBody( Map<String, Object> config, Map<String, Object> replacements) throws MessagingException, IOException  {

        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        //set Body content
        String body = replaceFields(replacements, config.get( "body"));
        messageBodyPart.setContent(body, "text/html");
        multipart.addBodyPart(messageBodyPart);
        String path = replaceFields(replacements,   getField(config, "attachment.file"));
        String filename =  replaceFields(replacements, getField(config, "attachment.filename"));

        // Attach the file
        if (replacements.get("attachment") != null) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile((String) replacements.get("attachment"));
            attachmentBodyPart.setFileName(filename);
            multipart.addBodyPart(attachmentBodyPart);
        }
        return multipart;

    }

    public static String replaceFields(Map<String, Object> replacements, Object value) {

        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            value = ((String) value).replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
        }
        return value.toString();
    }

    String getField(Object value, String fieldPath){
        String[] fields = fieldPath.split("\\.");
        for (String field : fields) {
            int index = -1;
            if (field.contains("[")) {
                index = Integer.parseInt(field.substring(field.indexOf("[") + 1, field.indexOf("]")));
                field = field.substring(0, field.indexOf("["));
            }
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(field);
            } else if (value instanceof List) {
                value = ((List<?>) value).get(index);
            }
        }

        return value==null? null :value.toString();

    }
    public Object getTemplate(InputStream template, String fieldPath) {
        String[] fields = fieldPath.split("\\.");
        Object value = null;
        for (String field : fields) {
            int index = -1;
            if (field.contains("[")) {
                index = Integer.parseInt(field.substring(field.indexOf("[") + 1, field.indexOf("]")));
                field = field.substring(0, field.indexOf("["));
            }
            if (value == null) {
                value = new Yaml().load(template);
            }
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(field);
            } else if (value instanceof List) {
                value = ((List<?>) value).get(index);
            }
        }
        return value;
    }

}
