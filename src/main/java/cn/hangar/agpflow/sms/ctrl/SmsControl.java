package cn.hangar.agpflow.sms.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hangar.agpflow.sms.service.SmsService;


@Controller
public class SmsControl {
	@Autowired
	SmsService smsService;
	
	@RequestMapping(value = "sendsms")
	public ResponseEntity<?> sendSMS(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(smsService!=null)
		{
			smsService.autoSendSms();
		}
		return new ResponseEntity<String>("success",HttpStatus.OK);
	}
}
