package com.strigalev.reportservice.endpoint;

import com.strigalev.reportservice.dto.UserStatisticDTO;
import com.strigalev.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportEndpoint {
    private final ReportService reportService;

    @GetMapping("/{usersIds}")
    public ResponseEntity<List<UserStatisticDTO>> getUserStatisticBetween(
            @PathVariable Long[] usersIds,
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(reportService.getUserStatisticBetween(usersIds, from, to));
    }

    @GetMapping("/sendToEmail/{usersIds}/{email}")
    public void getUserStatisticInExcelBetween(
            @PathVariable Long[] usersIds,
            @PathVariable String email,
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        reportService.sendExcelFileToEmail(usersIds, from, to, email);
    }

    @PostMapping("/generateReport/{usersIds}")
    public ResponseEntity<String> generateExcelStatistics(
            @PathVariable Long[] usersIds,
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) throws IOException {
        return ResponseEntity.ok(reportService.generateExcelStatistics(usersIds, from, to));
    }

    @GetMapping("/download/{id}")
    public void getExcelStatistics(@PathVariable String id, HttpServletResponse response) throws IOException {
        InputStream myStream = reportService.getExcelStatistics(id);

        response.addHeader("Content-disposition", "attachment; filename=statistics.xls");
        response.setContentType("application/octet-stream");

        IOUtils.copy(myStream, response.getOutputStream());
        response.flushBuffer();
    }
}
