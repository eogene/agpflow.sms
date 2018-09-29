package cn.hangar.agpflow.sms.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hangar.agpflow.sms.model.SMSListInfo;
import cn.hangar.util.db.IDataBase;

/**
* @author Eoge E-mail:18802012501@139.com
* @version 创建时间：2018年2月6日 下午5:11:47
* 通用查询 
*/
public class SmsRepository extends BaseRepository<SMSListInfo>{

	public SmsRepository(IDataBase db) {
		super(db);
	}
	public List<SMSListInfo> getNeedSendSms()
	{
		Map<String,Object> arg = new HashMap<String,Object>();
		arg.put(SMSListInfo._EndFlag,0);
		try {
			return this.executeSelectList(SMSListInfo.class,arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
