package cn.hangar.agpflow.sms.job;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.hangar.agpflow.sms.service.SmsService;
import cn.hangar.core.ioc.SpringUtil;
import cn.hangar.util.log.Logger;
import cn.hangar.util.log.LoggerFactory;

public class SmsJob implements Job{
	Logger log = LoggerFactory.getLogger(this.getClass());
	SmsService smsService;
	
	volatile FutureTask<Object> task = null;
	
	private void safeAutoStart() {
		if (task == null) {
			try{
			Callable<Object> online = new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					if(smsService==null)
						smsService = SpringUtil.getBean(SmsService.class);
					smsService.autoSendSms();
				    return "success";
				}
			};
			task = new FutureTask<Object>(online);
			task.run();
			} catch (Exception e) {
				log.error("发送短信服务执行异常", e);
			}
		}
		try {
			task.get();
		} catch (Exception e) {
			log.error("发送短信服务执行异常", e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		safeAutoStart();
	}

}
