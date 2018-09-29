package cn.hangar.agpflow.sms.job;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import cn.hangar.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eoge E-mail:18802012501@139.com
 * @version 创建时间：2017年11月22日 下午3:49:29
 *          类说明
 */
@Service("defaultIJobScheduler")
public class QuartzJobScheduler {

    private static int THREAD_COUNT = 2;
    private static int THREADPRIORITY = 2;

    private SchedulerFactory gSchedulerFactory;


    public QuartzJobScheduler() {
        this(getSchedulerFactoryProperties(THREAD_COUNT));
    }

    public QuartzJobScheduler( Properties pros) {
        try {
            gSchedulerFactory = new StdSchedulerFactory(pros);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private static Properties getSchedulerFactoryProperties(int threadCount) {
        Properties pros = new Properties();
        pros.put("org.quartz.scheduler.instanceName", "QuartzManager");
        pros.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        pros.put("org.quartz.threadPool.threadCount", threadCount + "");
        pros.put("org.quartz.threadPool.threadPriority",THREADPRIORITY+"");
        //pros.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        return pros;
    }

    @SuppressWarnings("rawtypes")
    public void addJob(String jobName, Class cls, String time,Map<String,Object> pars) 
    {
    	addJob(jobName,getJobGroup(cls),getTriggerGroup(cls),cls,time,pars);
    }
    
    public String getJobGroup(Class cls)
    {
    	return cls.getSimpleName();
    }
    
    public String getTriggerGroup(Class cls)
    {
    	return cls.getSimpleName()+"Trigger";
    }

    /**
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     *
     * @param jobName 任务名
     * @param cls     任务
     * @param time    时间设置
     */
    @SuppressWarnings("rawtypes")
    public void addJob(String jobName,String jobGroup,String triggerGroup, Class cls, String time,Map<String,Object> pars) {
        try {
        	 Scheduler sched = gSchedulerFactory.getScheduler();
        	if(this.checkExists(jobName, jobGroup))
        	{
        		if (!sched.isShutdown()) {
                    sched.start();
                }
        		return;
        	}
            // 任务名，任务组，任务执行类  
            JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(jobName, jobGroup)
                    .build();
            //可以传递参数  
            if(pars!=null)
               jobDetail.getJobDataMap().putAll(pars);
            // 触发器  
            CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger()
                    .withIdentity(jobName, triggerGroup)
                    .withSchedule(CronScheduleBuilder.cronSchedule(time))
                    .build();
            // 触发器时间设定  
            Date ft = sched.scheduleJob(jobDetail, trigger);
            System.out.println("定时任务:"+ jobName + " 已经在 : " + StringUtils.toString(ft) + " 时运行，Cron表达式：" + trigger.getCronExpression()+" 下次触发时间:"+trigger.getNextFireTime());
            // 启动  
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     * @param time
     */
    @SuppressWarnings("rawtypes")
    public void modifyJobTime(String jobName,String jobGroup,String triggerGroup, String time,Map<String,Object> pars) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(jobName, triggerGroup));
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                JobDetail jobDetail = sched.getJobDetail(new JobKey(jobName, jobGroup));
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName,jobGroup,triggerGroup);
                addJob(jobName,jobGroup,triggerGroup, objJobClass, time,pars);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     */
    public void removeJob(String jobName,String jobGroup,String triggerGroup) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            TriggerKey tkey = new TriggerKey(jobName, triggerGroup);
            sched.pauseTrigger(tkey);// 停止触发器  
            sched.unscheduleJob(tkey);// 移除触发器  
            sched.deleteJob(new JobKey(jobName, jobGroup));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void removeJob(String jobName,Class cls)
    {
    	removeJob(jobName,getJobGroup(cls),getTriggerGroup(cls));
    }

    public boolean checkExists(String jobName,Class cls) 
    {
    	return checkExists(jobName,getJobGroup(cls));
    }
    
    public boolean checkExists(String jobName,String jobGroup) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            return sched.checkExists(new JobKey(jobName, jobGroup));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
