package br.com.finance.modules.summary;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.summary.dto.MonthlySummaryResponse;
import br.com.finance.modules.summary.dto.UpdateMonthlySummaryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/finance")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService salaryService) {
        this.summaryService = salaryService;
    }

    @GetMapping("/summary/{competenceDate}")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySalarySummary(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competenceDate")
            @Pattern(regexp = TimestampUtils.COMPETENCE_DATE_REGEX, message = "CompetenceDate deve estar no formato yyyy-MM-dd")
            String competenceDate
    ) {
        return ResponseEntity.ok(summaryService.getMonthlySummary(jwt, TimestampUtils.parseCompetenceDate(competenceDate)));
    }

    @PutMapping("/summary/{competenceDate}")
    public ResponseEntity<MonthlySummaryResponse> updateMonthlySalarySummary(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competenceDate")
            @Pattern(regexp = TimestampUtils.COMPETENCE_DATE_REGEX, message = "CompetenceDate deve estar no formato yyyy-MM-dd")
            String competenceDate,
            @Valid @RequestBody UpdateMonthlySummaryRequest request
    ) {
        return ResponseEntity.ok(summaryService.updateMonthlySummary(
                jwt,
                TimestampUtils.parseCompetenceDate(competenceDate),
                request
        ));
    }
}