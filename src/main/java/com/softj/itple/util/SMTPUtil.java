package com.softj.itple.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class SMTPUtil {
	@Autowired
	private Environment env;
	
    public void sendmail(String id, String subject, String htmlMessage){
        
    	Properties props = new Properties(); 
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "25"); 
        props.put("mail.debug", "true"); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable","true"); 
        props.put("mail.smtp.EnableSSL.enable","true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
        props.setProperty("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.port", "465");   
        props.setProperty("mail.smtp.socketFactory.port", "465"); 
    
        Session session = Session.getInstance(props, 
        		new javax.mail.Authenticator() { 
			        protected PasswordAuthentication getPasswordAuthentication() { 
			        	return new PasswordAuthentication(env.getProperty("emailId"), env.getProperty("emailPassword")); 
			        }
        		});

        try{
        	Message message = new MimeMessage(session); 
        	message.setFrom(new InternetAddress(env.getProperty("emailId")));
        	message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(id));
        	message.setSubject(subject);
        	message.setContent(htmlMessage,"text/html; charset=utf-8");//글내용을 html타입 charset설정
        	System.out.println("send!!!");
        	Transport.send(message);
        } catch(Exception e){
            e.printStackTrace();
        }
    }    
}