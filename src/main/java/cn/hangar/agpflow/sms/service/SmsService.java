package cn.hangar.agpflow.sms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import cn.hangar.agpflow.sms.model.SMSListInfo;
import cn.hangar.agpflow.sms.repository.SmsRepository;
import cn.hangar.core.log.ILogger;
import cn.hangar.core.log.LoggerFactory;
import cn.hangar.util.GeneralUtil;
import cn.hangar.util.StringUtils;
import cn.hangar.util.XmlUtil;
import cn.hangar.util.config.ConfigManager;
import cn.hangar.util.db.DBTransactionalFactory;

@Service
public class SmsService {
	protected ILogger log = LoggerFactory.getLogger(this.getClass());
	private SmsRepository smsRepository;
	private int maxSize = 500;
	private SmsRepository getSmsRepository()
	{
		if(smsRepository==null)
		{
			smsRepository = new SmsRepository(DBTransactionalFactory.createDataBase("Default"));
			int maxsize = ConfigManager.getIntProperty("smsservice.maxsize");
			if(maxsize>0)
			{
				maxSize = maxsize;
			}
		}
	    return smsRepository;
	}
	
	public List<SMSListInfo> getNeedSendSms()
	{
		return getSmsRepository().getNeedSendSms();
	}
	
	public void saveSms(List<SMSListInfo> sms)
	{
		if(sms==null||sms.isEmpty())
			return;
		List<String> fields = new ArrayList<String>();
		fields.add(SMSListInfo._EndFlag);
		fields.add(SMSListInfo._Results);
		fields.add(SMSListInfo._SendTime);
		fields.add(SMSListInfo._SendFlag);
		try {
			getSmsRepository().update(sms, fields);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public synchronized void autoSendSms()
	{
		List<SMSListInfo> sms = getNeedSendSms();
		if (sms == null)
		{
			log.info("未找到需要发送的短信。");
			return;
		}
		List<SMSListInfo> rsms = new ArrayList<SMSListInfo>();
		try {
			int i=0;
			for (SMSListInfo s : sms) {
				if (i<maxSize&&send(s)) {
					rsms.add(s);
				}
				i++;
			}
		} catch (Exception e) {
			log.error(e);
		}

		saveSms(rsms);
	}
	
	private static final String _OPERATION = "SMS_SEND_MESSAGE_REMEDY";
	
	
	private boolean send(SMSListInfo sms) throws Exception
	{
		String endpoint = ConfigManager.getProperty("smsservice.endpoint");
		String system = ConfigManager.getProperty("smsservice.system");
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version='1.0' encoding='GB2312'?>");
		xml.append("<sms><user>");
		xml.append(sms.Mobile);
		xml.append("</user><smsid>");
		xml.append(sms.RecId);
		xml.append("</smsid><content>");
		xml.append(sms.Contents);
		xml.append("</content>");
		xml.append("</sms>");
		InnerAdapterService proxy = new InnerAdapterService(endpoint);
		//调用查询方法
		String result = proxy.process(_OPERATION, system, xml.toString());
		InputStream ist = new ByteArrayInputStream(result.getBytes());
		Map<String, String> map = XmlUtil.parseXml(ist);
		String code = map.get("rtCode");
		boolean flag = StringUtils.Equals("000", code);
		String msg = map.get("rtMessage");
		msg = new String(msg.getBytes("GB2312"),"utf-8");
		sms.EndFlag = flag?1:2;
		sms.Results = msg;
		sms.SendTime = GeneralUtil.Now();
		return true;
	}
}
