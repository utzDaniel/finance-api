package br.com.finance.modules.account;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.account.dto.*;
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

@Validated
@RestController
@RequestMapping("/api/v1/finance")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account/{competence}")
    public ResponseEntity<Page<AccountResponse>> getAccount(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.getAccount(jwt, TimestampUtils.parseCompetence(competence), pageable));
    }

    @PostMapping("/account/{competence}")
    public ResponseEntity<Page<AccountResponse>> addAccount(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid AddAccountRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.addAccount(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @GetMapping("/account/{competence}/user")
    public ResponseEntity<List<AccountUserResponse>> getAccountUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence
    ) {
        return ResponseEntity.ok(accountService.getAccountUser(jwt, TimestampUtils.parseCompetence(competence)));
    }

    @GetMapping("/account/{competence}/family")
    public ResponseEntity<List<AccountUserResponse>> getAccountFamily(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence
    ) {
        return ResponseEntity.ok(accountService.getAccountFamily(jwt, TimestampUtils.parseCompetence(competence)));
    }

    @GetMapping("/account/bank")
    public ResponseEntity<List<AccountBankResponse>> getAccountBank() {
        return ResponseEntity.ok(accountService.getAccountBank());
    }

    @GetMapping("/account/type")
    public ResponseEntity<List<AccountTypeResponse>> getAccountType() {
        return ResponseEntity.ok(accountService.getAccountType());
    }

    @GetMapping("/account/link")
    public ResponseEntity<List<AccountLinkResponse>> getAccountLink() {
        return ResponseEntity.ok(accountService.getAccountLink());
    }

}
