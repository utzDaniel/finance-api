package br.com.finance.modules.salary;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.salary.dto.SalaryDetailResponse;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/finance")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping("/salary/{competenceDate}/detail")
    public ResponseEntity<SalaryDetailResponse> getMonthlySalarySummary(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competenceDate")
            @Pattern(regexp = TimestampUtils.COMPETENCE_DATE_REGEX, message = "CompetenceDate deve estar no formato yyyy-MM-dd")
            String competenceDate
    ) {
        return ResponseEntity.ok(salaryService.getSalaryDetail(jwt, TimestampUtils.parseCompetenceDate(competenceDate)));
    }

}
