package br.com.finance.modules.payroll;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.payroll.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/v1/finance")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/payroll/{competence}")
    public ResponseEntity<Page<PayrollResponse>> getPayroll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @RequestParam Map<String, String> filters
    ) {
        return ResponseEntity.ok(payrollService.getPayroll(jwt, TimestampUtils.parseCompetence(competence), pageable));
    }

    @PostMapping("/payroll/{competence}")
    public ResponseEntity<Page<PayrollResponse>> addPayroll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid AddPayrollRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(payrollService.addPayroll(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @PutMapping("/payroll/{competence}")
    public ResponseEntity<Page<PayrollResponse>> updatePayroll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid UpdatePayrollRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(payrollService.updatePayroll(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @DeleteMapping("/payroll/{competence}")
    public ResponseEntity<Page<PayrollResponse>> deletePayroll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid DeletePayrollRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(payrollService.deletePayroll(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @PostMapping("/payroll/{competence}/integrated")
    public ResponseEntity<Page<PayrollResponse>> integratedPayroll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid IntegratedPayrollRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(payrollService.integratedPayroll(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @GetMapping("/payroll/event")
    public ResponseEntity<List<PayrollEventResponse>> getPayrollEvent() {
        return ResponseEntity.ok(payrollService.getPayrollEvent());
    }

    @GetMapping("/payroll/type")
    public ResponseEntity<List<EntryTypeResponse>> getPayrollType() {
        return ResponseEntity.ok(payrollService.getPayrollType());
    }

}
