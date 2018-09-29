package cn.hangar.agpflow.sms;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hangar.core.ioc.SpringUtil;

@Configuration
public class SpringConfig {

	@Bean
	public ApplicationContextAware springUtilInit() {
		return new SpringUtil();
	}
}
