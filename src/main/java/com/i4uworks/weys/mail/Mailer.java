package com.i4uworks.weys.mail;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class Mailer {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private JavaMailSender mailSender;
	@Resource(name="velocityEngine")
	private VelocityEngine velocityEngine;
	
	public void sendEmail(EmailVO mailVO){
		
		MimeMessage msg = mailSender.createMimeMessage();

		try{
			MimeMessageHelper helper = new MimeMessageHelper(msg, false);
			String template = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/" + mailVO.getVeloTmp(), "UTF-8", mailVO.getEmailMap());

			InternetAddress from = new InternetAddress("웨이즈<"+ mailVO.getFrom() + ">");
			String personal = from.getPersonal();
			if(personal != null)
				from.setPersonal(personal, "UTF-8");
            
			helper.setFrom(from);
			helper.setTo(mailVO.getReceiver());
			helper.setSubject(mailVO.getSubject());
			helper.setText(template, true);
			
			mailSender.send(msg);
		} catch(Exception e){
			logger.info("mailer error ::: " + e.getMessage());
		}
		
	}
}
