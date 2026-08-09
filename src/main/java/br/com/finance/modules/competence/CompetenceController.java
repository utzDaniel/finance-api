package br.com.finance.modules.competence;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.competence.dto.CompetenceResponse;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/finance")
public class CompetenceController {

    private final CompetenceService competenceService;

    public CompetenceController(CompetenceService competenceService) {
        this.competenceService = competenceService;
    }

    @GetMapping("/competence")
    public ResponseEntity<List<CompetenceResponse>> getCompetences(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(competenceService.getCompetences(jwt));
    }

    @PostMapping("/competence/{competence}/initialize")
    public ResponseEntity<List<CompetenceResponse>> initializeCompetence(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence
    ) {
        return ResponseEntity.ok(competenceService.initializeCompetence(jwt, TimestampUtils.parseCompetence(competence)));
    }

    @PostMapping("/competence/{competence}/close")
    public ResponseEntity<List<CompetenceResponse>> closeCompetence(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence
    ) {
        return ResponseEntity.ok(competenceService.closeCompetence(jwt, TimestampUtils.parseCompetence(competence)));
    }

}
