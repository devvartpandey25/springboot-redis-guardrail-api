package com.grid07.assignment.config;

import com.grid07.assignment.jobs.ViralityJob;
import com.grid07.assignment.jobs.CleanupJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // NOTE: This sets up the job for tracking virality stuff. Might want to tweak job name later.
    @Bean
    public JobDetail makeViralityJob() {
        // Just making sure we keep the job durable for now
        JobDetail viralityJob = JobBuilder.newJob(ViralityJob.class)
                .withIdentity("virality_job_detail") // not super creative with the name
                .storeDurably()
                .build();
        return viralityJob;
    }

    // This trigger runs the virality job every minute. Could make this configurable?
    @Bean
    public Trigger startViralityJob(@Qualifier("makeViralityJob") JobDetail job) {
        // Not sure if this identity needs to be unique
        String triggerName = "virality_trigger";
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(60) // 1 min interval
                .repeatForever();
        Trigger t = TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity(triggerName)
                .withSchedule(schedule)
                .build();
        return t;
    }

    // Setting up the cleanup job so old data doesn't pile up.
    @Bean
    public JobDetail getCleanupJobDetail() {
        // Just to keep naming a bit different from above
        JobDetail jd = JobBuilder.newJob(CleanupJob.class)
                .withIdentity("cleanup_job")
                .storeDurably()
                .build();
        // Could maybe add more config here later
        return jd;
    }

    // Triggers cleanup job once a day
    @Bean
    public Trigger runCleanupJob(@Qualifier("getCleanupJobDetail") JobDetail cleanJobDetail) {
        // TODO: Should we randomize the start time a bit?
        SimpleScheduleBuilder sched = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(24) // daily
                .repeatForever();
        String name = "cleanup_trigger";
        Trigger cleanupTrig = TriggerBuilder.newTrigger()
                .forJob(cleanJobDetail)
                .withIdentity(name)
                .withSchedule(sched)
                .build();
        return cleanupTrig;
    }
}