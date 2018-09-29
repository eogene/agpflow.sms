package cn.hangar.agpflow.sms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.hangar.agpflow.sms.job.QuartzJobScheduler;
import cn.hangar.agpflow.sms.job.SmsJob;
import cn.hangar.core.ioc.SpringUtil;
import cn.hangar.util.AssertHelper;
import cn.hangar.util.StringUtils;
import cn.hangar.util.config.ConfigManager;
import cn.hangar.util.db.DBTransactionalFactory;
import cn.hangar.util.db.DBTransactionalFactory.TransactionalConnectStringElement;
import cn.hangar.util.ioc.ContextManager;
@Component
public class SmsAppInit implements CommandLineRunner {


	
	@Override
	public void run(String... args) throws Exception {
		initialize();
		startSendSMS();
	}

	private static void initialize() {
		String driver = ConfigManager.getProperty("jdbc.driver");
		String url = ConfigManager.getProperty("jdbc.url");
		if(StringUtils.isEmpty(url))
			return;
		String username = ConfigManager.getProperty("jdbc.username");
		String password = ConfigManager.getProperty("jdbc.password");
		Integer maxActive = ConfigManager.getIntProperty("jdbc.max.active");
		Integer maxIdle = ConfigManager.getIntProperty("jdbc.max.idle");
		AssertHelper.notNull(driver);
		AssertHelper.notNull(url);
		AssertHelper.notNull(username);
		AssertHelper.notNull(password);
		TransactionalConnectStringElement cse = new TransactionalConnectStringElement();
		cse.setName(DBTransactionalFactory.DefaultDatabase);
		cse.setDriver(driver);
		cse.setUrl(url);
		cse.setUsername(username);
		cse.setPassword(password);
		if(maxActive>0)
		cse.setMaxActive(maxActive);
		if(maxIdle>0)
		cse.setMaxIdle(maxIdle);
		DBTransactionalFactory.registerConnectionString(cse);
	}
	
	private void startSendSMS()
	{
		QuartzJobScheduler scheduler = SpringUtil.getBean(QuartzJobScheduler.class);
		String time = ConfigManager.getProperty("smsjob.time");
		if(StringUtils.isEmpty(time))
			time = "0 0/1 * * * ?";
		scheduler.addJob(SmsJob.class.getSimpleName(), SmsJob.class, time, null);
	}
}
