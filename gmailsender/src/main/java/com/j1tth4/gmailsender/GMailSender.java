package com.j1tth4.gmailsender;

import android.text.TextUtils;
import android.util.Log;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender{

    public static final String TAG = "GMailSender";

    private Session session;

    private String subject;
    private String sender;
    private String recipients;
    private String body;
    private String contentType;
    private File attachment;

    private SMTPTransport connectToSmtp(String host, int port, String userEmail,
                                        String oauthToken, boolean debug) throws MessagingException {
        Log.v(TAG, "connect smtp server");

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "false");

        session = Session.getInstance(props);
        session.setDebug(debug);

        final URLName unusedUrlName = null;
        SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
        // If the password is non-null, SMTP tries to do AUTH LOGIN.
        final String emptyPassword = null;
        transport.connect(host, port, userEmail, emptyPassword);

        byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail, oauthToken).getBytes();
        response = BASE64EncoderStream.encode(response);

        Log.v(TAG, "is connect " + transport.isConnected());
        Log.v(TAG, "response " + new String(response));

        transport.issueCommand("AUTH XOAUTH2 " + new String(response), 235);
        return transport;
    }

    public synchronized void sendMail(String user, String oauthToken) throws MessagingException, IOException {
        SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com", 587, user, oauthToken, true);

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
        smtpTransport.sendMessage(message, message.getAllRecipients());
    }

    public static class Builder{
        private String subject;
        private String sender;
        private String recipients;
        private String body;
        private String contentType;
        private File attachment;

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

        public GMailSender create() throws Exception {
            GMailSender gmailSender = new GMailSender();
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
