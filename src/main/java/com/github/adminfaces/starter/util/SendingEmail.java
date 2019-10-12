/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author SKYWARD DIGITAL
 */
public class SendingEmail {
    private String userEmail;
    private String myHash;
    private String tempPWord;
    private String username;

    public SendingEmail(String userEmail, String myHash, String tempPWord, String username) {
        this.userEmail = userEmail;
        this.myHash = myHash;
        this.tempPWord = tempPWord;
        this.username = username;
    }
    
    public void sendEmail(){
        String email = "vbini21@gmail.com";
        String password = "Acoustica1..";
        
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        
        Session session = Session.getInstance(properties, new javax.mail.Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(email, password);
            }
        });
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            message.setSubject("TEMIS. in Email Verification Link");
            message.setText("Verification Link");
            message.setText("Your username is:"+username+"\nYour temporary password is:"+tempPWord+"\nYour Verification link :: "
                    +"http://localhost:8080/admin-starter/ActivateAccount?key1="+userEmail+"&key2="+myHash);
            Transport.send(message);
        }catch(Exception e){
            System.out.println("SendingEmail.... "+e);
        }
    }
    
}
