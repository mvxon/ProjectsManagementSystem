package com.strigalev.projectsservice.scheduling;

import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ArchiveJob extends QuartzJobBean {
    private final TaskRepository taskRepository;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        Long taskId = (Long) mergedJobDataMap.get("taskId");

        taskRepository.updateTaskStatus(taskId, TaskStatus.ARCHIVED);
    }
}
