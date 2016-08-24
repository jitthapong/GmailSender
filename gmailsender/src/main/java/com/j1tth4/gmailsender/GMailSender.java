package com.j1tth4.gmailsender;

import android.text.TextUtils;

import com.j1tth4.gmailsender.provider.JSSEProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

public class GMailSender extends javax.mail.Authenticator {

    public static final String TAG = "GMailSender";

    private String user;
    private String password;
    private Session session;
    private String subject;
    private String sender;
    private String recipients;
    private String body;
    private String contentType;
    private Multipart multipart;

    static {
        Security.addProvider(new JSSEProvider());
    }

    private GMailSender(String user, String password) {
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

        session = Session.getDefaultInstance(props, this);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail() throws Exception {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), contentType));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);
        if(multipart != null && multipart.getCount() > 0) {
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
        }
        if (recipients.indexOf(',') > 0) {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        }else {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        }
        Transport.send(message);
    }

    public void attachFile(String filename) throws Exception
    {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        multipart.addBodyPart(messageBodyPart);
    }

    public static class Builder{
        private String user;
        private String password;
        private String subject;
        private String sender;
        private String recipients;
        private String body;
        private String contentType;
        private String fileName;

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

        public Builder attachFile(String fileName){
            this.fileName = fileName;
            return this;
        }

        public GMailSender create() throws Exception {
            GMailSender gmailSender = new GMailSender(user, password);
            gmailSender.subject = subject;
            gmailSender.sender = sender;
            gmailSender.recipients = recipients;
            gmailSender.body = body;
            gmailSender.contentType = TextUtils.isEmpty(contentType) ? "text/plain" : contentType;
            if(!TextUtils.isEmpty(fileName))
                gmailSender.attachFile(fileName);
            return gmailSender;
        }
    }

    public static class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
