package br.com.finance.modules.salary;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.salary.dto.AddSalaryDetailRequest;
import br.com.finance.modules.salary.dto.DeleteSalaryDetailRequest;
import br.com.finance.modules.salary.dto.SalaryDetailResponse;
import br.com.finance.modules.salary.dto.UpdateSalaryDetailRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<Page<SalaryDetailResponse>> getSalaryDetail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competenceDate")
            @Pattern(regexp = TimestampUtils.COMPETENCE_DATE_REGEX, message = "CompetenceDate deve estar no formato yyyy-MM-dd")
            String competenceDate,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(salaryService.getSalaryDetail(jwt, TimestampUtils.parseCompetenceDate(competenceDate), pageable));
    }

    @PostMapping("/salary/{competenceDate}/detail")
    public ResponseEntity<Page<SalaryDetailResponse>> addSalaryDetail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competenceDate")
            @Pattern(regexp = TimestampUtils.COMPETENCE_DATE_REGEX, message = "CompetenceDate deve estar no formato yyyy-MM-dd")
            String competenceDate,
            @RequestBody @Valid AddSalaryDetailRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(salaryService.addSalaryDetail(jwt, TimestampUtils.parseCompetenceDate(competenceDate), request, pageable));
    }

    @PutMapping("/salary/{competenceDate}/detail")
    public ResponseEntity<Page<SalaryDetailResponse>> updateSalaryDetail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competenceDate")
            @Pattern(regexp = TimestampUtils.COMPETENCE_DATE_REGEX, message = "CompetenceDate deve estar no formato yyyy-MM-dd")
            String competenceDate,
            @RequestBody @Valid UpdateSalaryDetailRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(salaryService.updateSalaryDetail(jwt, TimestampUtils.parseCompetenceDate(competenceDate), request, pageable));
    }

    @DeleteMapping("/salary/{competenceDate}/detail")
    public ResponseEntity<Page<SalaryDetailResponse>> deleteSalaryDetail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competenceDate")
            @Pattern(regexp = TimestampUtils.COMPETENCE_DATE_REGEX, message = "CompetenceDate deve estar no formato yyyy-MM-dd")
            String competenceDate,
            @RequestBody @Valid DeleteSalaryDetailRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(salaryService.deleteSalaryDetail(jwt, TimestampUtils.parseCompetenceDate(competenceDate), request, pageable));
    }
}
