package com.j1tth4.gmailsender;

import android.text.TextUtils;

import com.j1tth4.gmailsender.provider.JSSEProvider;

import java.io.File;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
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

public class GMailSender2 extends javax.mail.Authenticator {

    public static final String TAG = "GMailSender2";

    private String user;
    private String password;
    private Session session;
    private String subject;
    private String sender;
    private String recipients;
    private String body;
    private String contentType;
    private File attachment;

    static {
        Security.addProvider(new JSSEProvider());
    }

    private GMailSender2(String user, String password) throws MessagingException {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.quitwait", "false");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.debug", "true");
        session = Session.getDefaultInstance(props, this);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail() throws Exception {
        Multipart multipart = new MimeMultipart();

        MimeMessage message = new MimeMessage(session);
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));

        if(!TextUtils.isEmpty(body)) {
            BodyPart bodyPart = new MimeBodyPart();
            DataHandler handler = new DataHandler(body, contentType);
            bodyPart.setDataHandler(handler);
            multipart.addBodyPart(bodyPart);
        }

        if(attachment != null) {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.attachFile(attachment);
            multipart.addBodyPart(mimeBodyPart);
        }

        message.setContent(multipart);
        Transport.send(message);
    }

    public static class Builder{
        private String user;
        private String password;
        private String subject;
        private String sender;
        private String recipients;
        private String body;
        private String contentType;
        private File attachment;

        public Builder(String user, String password) {
            this.user = user;
            this.password = password;
        }

        public Builder subject(String subject){
            this.subject = subject;
            return this;
        }

        public Builder sender(String sender){
            this.sender = sender;
            return this;
        }

        public Builder recipients(String recipients){
            this.recipients = recipients;
            return this;
        }

        public Builder body(String body){
            this.body = body;
            return this;
        }

        public Builder contentType(String contentType){
            this.contentType = contentType;
            return this;
        }

        public Builder attachFile(File attachment){
            this.attachment = attachment;
            return this;
        }

        public GMailSender2 create() throws Exception {
            GMailSender2 gmailSender = new GMailSender2(user, password);
            gmailSender.subject = subject;
            gmailSender.sender = sender;
            gmailSender.recipients = recipients;
            gmailSender.body = body;
            gmailSender.contentType = TextUtils.isEmpty(contentType) ? "text/plain" : contentType;
            gmailSender.attachment = attachment;
            return gmailSender;
        }
    }
}
