package com.strigalev.reportservice.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.strigalev.reportservice.dto.CompletedTaskDTO;
import com.strigalev.reportservice.dto.UserStatisticDTO;
import com.strigalev.reportservice.exporter.StatisticExcelExporter;
import com.strigalev.reportservice.feign.FeignClientService;
import com.strigalev.starter.dto.AuditDTO;
import com.strigalev.starter.dto.DateIntervalDTO;
import com.strigalev.starter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final FeignClientService feignClientService;
    private final StatisticExcelExporter exporter;
    private final JavaMailSender mailSender;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations operations;


    public List<UserStatisticDTO> getUserStatisticBetween(
            Long[] usersIds,
            LocalDateTime from,
            LocalDateTime to
    ) {
        List<UserStatisticDTO> usersStatistics = new ArrayList<>();

        Arrays.sort(usersIds);
        Arrays.stream(usersIds).forEach(id -> {

            List<AuditDTO> completedTasksAudit =
                    feignClientService.getUserCompletedTasks(
                            id,
                            DateIntervalDTO.builder()
                                    .from(from)
                                    .to(to)
                                    .build()
                    ).getBody();

            if (completedTasksAudit == null || completedTasksAudit.isEmpty()) {
                return;
            }


            double rate =
                    (double) completedTasksAudit.size() / ChronoUnit.DAYS.between(from.toLocalDate(), to.toLocalDate());
            double scale = Math.pow(10, 4);
            double resultRate = Math.ceil(rate * scale) / scale;

            usersStatistics.add(
                    UserStatisticDTO.builder()
                            .user(completedTasksAudit.get(0).getActionUser())
                            .completedTasksStatistic(mapAuditToCompletedTasks(completedTasksAudit))
                            .completedTasksCount(completedTasksAudit.size())
                            .completionRate(resultRate)
                            .build());
        });
        if (usersStatistics.isEmpty()) {
            throw new ResourceNotFoundException("STATISTICS NOT FOUND");
        }

        return usersStatistics;
    }

    public void sendExcelFileToEmail(
            Long[] usersIds,
            LocalDateTime from,
            LocalDateTime to,
            String toEmail
    ) {
        MimeMessage message = mailSender.createMimeMessage();

        File file = exporter.export(getUserStatisticBetween(usersIds, from, to));
        if (file.exists() && !file.isDirectory()) {

            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(toEmail);
                helper.setSubject("PMS | User statistics");
                helper.setText("Statistics for users on " + from + " to " + to + " period.");

                helper.addAttachment("statistics.xlsx", file);

            } catch (Exception ex) {
                System.out.println("Error");
            }
        }

        mailSender.send(message);
    }

    private List<CompletedTaskDTO> mapAuditToCompletedTasks(List<AuditDTO> statistics) {

        return statistics.stream()
                .map(audit -> {
                    CompletedTaskDTO task = CompletedTaskDTO.builder()
                            .taskId(audit.getTaskId())
                            .completionDate(audit.getDate())
                            .dateOfDevStarted(audit.getDateOfDevStart())
                            .projectId(audit.getProjectId())
                            .build();

                    Duration hoursAndMinutes =
                            Duration.between(audit.getDateOfDevStart().toLocalTime(), audit.getDate().toLocalTime());


                    task.setDaysTook(ChronoUnit.DAYS.between(audit.getDateOfDevStart().toLocalDate(),
                            audit.getDate().toLocalDate()));
                    task.setHoursTook(hoursAndMinutes.toHoursPart());
                    task.setMinutesTook(hoursAndMinutes.toMinutesPart());
                    return task;
                })
                .toList();
    }

    public String generateExcelStatistics(Long[] usersIds, LocalDateTime from, LocalDateTime to) throws IOException {
        File file = exporter.export(getUserStatisticBetween(usersIds, from, to));

        try (InputStream stream = new FileInputStream(file)) {
            return gridFsTemplate.store(stream, file.getName(), null, null).toString();
        }
    }

    public InputStream getExcelStatistics(String id) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (gridFSFile == null) {
            throw new ResourceNotFoundException("Document with statistics not found");
        }
        return operations.getResource(gridFSFile).getInputStream();
    }
}

