package com.strigalev.projectsservice.scheduling;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class SchedulingService {
    private final Scheduler scheduler;

    @Value("${application.tasks.archiving-period-days}")
    private long tasksArchivingDays;

    public void scheduleTaskArchiving(Long taskId) {

        ZonedDateTime zonedDateTime = ZonedDateTime.now().plusDays(tasksArchivingDays);

        JobDataMap dataMap = new JobDataMap();
        dataMap.put("taskId", taskId);

        JobDetail detail = JobBuilder.newJob(ArchiveJob.class)
                .usingJobData(dataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(Date.from(zonedDateTime.toInstant()))
                .build();

        try {
            scheduler.scheduleJob(detail, trigger);

        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
