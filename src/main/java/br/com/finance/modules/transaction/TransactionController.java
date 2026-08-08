package br.com.finance.modules.transaction;

import br.com.finance.config.TimestampUtils;
import br.com.finance.modules.transaction.dto.*;
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
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transaction/{competence}")
    public ResponseEntity<Page<TransactionResponse>> getTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @Valid @ModelAttribute TransactionFilter filter
    ) {
        return ResponseEntity.ok(transactionService.getTransaction(jwt, TimestampUtils.parseCompetence(competence), pageable, filter));
    }

    @PostMapping("/transaction/{competence}/transfer")
    public ResponseEntity<Page<TransactionResponse>> transferTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("competence")
            @Pattern(regexp = TimestampUtils.DATA_REGEX, message = "Competence deve estar no formato yyyy-MM-dd")
            String competence,
            @RequestBody @Valid TransferTransactionRequest request,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(transactionService.transferTransaction(jwt, TimestampUtils.parseCompetence(competence), request, pageable));
    }

    @GetMapping("/transaction/method")
    public ResponseEntity<List<TransactionMethodResponse>> getTransactionMethod() {
        return ResponseEntity.ok(transactionService.getTransactionMethod());
    }

}
