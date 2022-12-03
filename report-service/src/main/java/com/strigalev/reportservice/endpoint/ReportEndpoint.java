package com.strigalev.reportservice.endpoint;

import com.strigalev.reportservice.dto.UserStatisticDTO;
import com.strigalev.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportEndpoint {
    private final ReportService reportService;

    @GetMapping("/statistic/{ids}")
    public ResponseEntity<List<UserStatisticDTO>> getUserStatisticBetween(
            @PathVariable Long[] ids,
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(reportService.getUserStatisticBetween(ids, from, to));
    }

    @GetMapping("/statistic/{ids}/excel/{email}")
    public void getUserStatisticInExcelBetween(
            @PathVariable Long[] ids,
            @PathVariable String email,
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        reportService.sendExcelFileToEmail(ids, from, to, email);
    }
}
