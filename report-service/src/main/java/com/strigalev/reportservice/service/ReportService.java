package com.strigalev.reportservice.service;

import com.strigalev.reportservice.dto.CompletedTaskDTO;
import com.strigalev.reportservice.dto.UserStatisticDTO;
import com.strigalev.reportservice.exporter.StatisticExcelExporter;
import com.strigalev.reportservice.feign.FeignClientService;
import com.strigalev.starter.dto.AuditDTO;
import com.strigalev.starter.dto.DateIntervalDTO;
import com.strigalev.starter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final FeignClientService feignClientService;
    private final StatisticExcelExporter exporter;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

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
                usersStatistics.add(getUserStatisticDtoOnFault(id));
                return;
            }

            String userEmail = completedTasksAudit.get(0).getActionUserEmail();

            double rate =
                    (double) completedTasksAudit.size() / ChronoUnit.DAYS.between(from.toLocalDate(), to.toLocalDate());
            double scale = Math.pow(10, 4);
            double resultRate = Math.ceil(rate * scale) / scale;

            usersStatistics.add(
                    UserStatisticDTO.builder()
                            .userEmail(userEmail)
                            .userId(id)
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
                helper.setSubject("Statistics");
                helper.setText("test");

                helper.addAttachment("statistics.xlsx", file);

            } catch (Exception ex) {
                System.out.println("Error");
            }
        }

        mailSender.send(message);
    }

    private UserStatisticDTO getUserStatisticDtoOnFault(Long userId) {
        return UserStatisticDTO.builder()
                .userId(userId)
                .message("THERE IS NO TASKS AT THAT PERIOD")
                .build();
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
}
