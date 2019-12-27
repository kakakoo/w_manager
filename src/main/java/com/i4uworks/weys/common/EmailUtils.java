package com.i4uworks.weys.common;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtils {

	protected static Logger logger = LoggerFactory.getLogger(EmailUtils.class);
	
	public static void sendEmail(Map<String, Object> info){
		Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");     // gmail은 무조건 true 고정
        p.put("mail.smtp.host", "smtp.gmail.com");      // smtp 서버 주소
        p.put("mail.smtp.auth","true");                 // gmail은 무조건 true 고정
        p.put("mail.smtp.port", "587");                 // gmail 포트
           
        String sender = MapUtils.getString(info, "senderId");
        String senderPw = MapUtils.getString(info, "senderPw");
        Authenticator auth = new MyAuthentication(sender, senderPw);
        
        //session 생성 및  MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);
        
        try{
        	
            //편지보낸시간
            msg.setSentDate(new Date());
             
            InternetAddress from = new InternetAddress() ;

            // 이메일 발신자
            from = new InternetAddress("WEYS<"+ sender + ">");
            msg.setFrom(from);

            String to_email = MapUtils.getString(info, "email");
            String subject = MapUtils.getString(info, "subject");
            String content = MapUtils.getString(info, "content");
            
            // 이메일 수신자
            InternetAddress to = new InternetAddress(to_email);
            msg.setRecipient(Message.RecipientType.TO, to);
            msg.setSubject(subject, "UTF-8");			// 이메일 제목
            msg.setContent(content, "text/html; charset=utf-8");				// 이메일 내용 
            msg.setHeader("content-Type", "text/html");	// 이메일 헤더 
            
            javax.mail.Transport.send(msg);				//메일보내기
             
        }catch (AddressException addr_e) {
            logger.info("AddressException ::: " + addr_e.getLocalizedMessage());
        }catch (MessagingException msg_e) {
            logger.info("MessagingException ::: " + msg_e.getLocalizedMessage());
        }
	}
}


class MyAuthentication extends Authenticator {
    
    PasswordAuthentication pa;
 
    public MyAuthentication(String id, String pw){
        // ID와 비밀번호를 입력한다.
        pa = new PasswordAuthentication(id, pw);
    }
 
    // 시스템에서 사용하는 인증정보
    public PasswordAuthentication getPasswordAuthentication() {
        return pa;
    }
}