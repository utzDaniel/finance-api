package br.com.finance.modules.event;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.account.AccountRepository;
import br.com.finance.modules.account.dto.AccountEntity;
import br.com.finance.modules.account.dto.AccountLink;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventType;
import br.com.finance.modules.event.dto.PayrollPayload;
import br.com.finance.modules.payroll.PayrollRepository;
import br.com.finance.modules.payroll.dto.EntryType;
import br.com.finance.modules.payroll.dto.PayrollEntity;
import br.com.finance.modules.payroll.dto.PayrollEvent;
import br.com.finance.modules.transaction.TransactionPayrollRepository;
import br.com.finance.modules.transaction.TransactionRepository;
import br.com.finance.modules.transaction.dto.TransactionEntity;
import br.com.finance.modules.transaction.dto.TransactionMethod;
import br.com.finance.modules.transaction.dto.TransactionPayrollEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PayrollEventStrategy implements EventStrategy<PayrollPayload> {

    private final TransactionRepository transactionRepository;
    private final TransactionPayrollRepository transactionPayrollRepository;
    private final AccountRepository accountRepository;
    private final PayrollRepository payrollRepository;

    public PayrollEventStrategy(TransactionRepository transactionRepository,
                                TransactionPayrollRepository transactionPayrollRepository,
                                AccountRepository accountRepository,
                                PayrollRepository payrollRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionPayrollRepository = transactionPayrollRepository;
        this.accountRepository = accountRepository;
        this.payrollRepository = payrollRepository;
    }

    @Override
    public void process(PayrollPayload payload, EventAction action) {
        if (payload.entities().isEmpty()) return;
        if (action == EventAction.INTEGRATED)
            processIntegrated(payload);
        if (action == EventAction.DELETED)
            processDeleted(payload);
    }

    private void processDeleted(PayrollPayload payload) {

        List<TransactionPayrollEntity> transactionPayrolls = this.transactionPayrollRepository.findByPayroll(
                payload.entities().stream().map(PayrollEntity::getId)
                .toList());

        if (transactionPayrolls.isEmpty()) {
            this.payrollRepository.deleteAll(payload.entities());
            return;
        }

        List<TransactionEntity> transactions = this.transactionRepository
                .findAllByIds(transactionPayrolls.stream().map(TransactionPayrollEntity::getTransactionAccount).toList());

        Map<Integer, AccountEntity> accounts = this.accountRepository.findIdByLink(
                        payload.entities().getFirst().getUserId(),
                        payload.entities().getFirst().getCompetence())
                .stream().collect(Collectors.toMap(
                        AccountEntity::getId,
                        value -> value
                ));

        transactions.forEach(v -> {
            AccountEntity account = accounts.get(v.getAccount());
            if (account == null) return;
            account.setBalance(account.getBalance().subtract(v.getAmount()));
        });

        this.transactionPayrollRepository.deleteAll(transactionPayrolls);
        this.transactionRepository.deleteAll(transactions);
        this.payrollRepository.deleteAll(payload.entities());
        this.accountRepository.saveAll(accounts.values());
    }

    private void processIntegrated(PayrollPayload payload) {

        Map<Integer, AccountEntity> accounts = this.accountRepository.findIdByLink(
                payload.entities().getFirst().getUserId(),
                payload.entities().getFirst().getCompetence()
        ).stream().collect(Collectors.toMap(
                AccountEntity::getLink,
                value -> value
        ));

        Set<Long> integrados = this.transactionPayrollRepository.findIdsByPayroll(
                payload.entities().stream().map(PayrollEntity::getId).toList());

        payload.entities().stream()
                .filter(v -> v.getType() != EntryType.DESCONTO.getId() && !integrados.contains(v.getId()))
                .forEach(v -> {
                    AccountEntity account = accounts.get(getLink(v));

                    if (account == null)
                        throw ApiException.badRequest("Conta não cadastrada para " + Objects.requireNonNull(EntryType.get(v.getType())).getDescription());

                    BigDecimal amount = v.getAmount().multiply(BigDecimal.valueOf(v.getQuantity()));

                    TransactionEntity entity = TransactionEntity.builder()
                            .account(account.getId())
                            .method(TransactionMethod.CARTAO.getId())
                            .name(getName(v))
                            .debit(true)
                            .amount(amount)
                            .dateTransaction(v.getEntry())
                            .build();

                    account.setBalance(account.getBalance().add(amount));

                    this.transactionRepository.save(entity);

                    TransactionPayrollEntity origin = TransactionPayrollEntity.builder()
                            .payroll(v.getId())
                            .transactionAccount(entity.getId())
                            .build();

                    this.transactionPayrollRepository.save(origin);
                });
        this.accountRepository.saveAll(accounts.values());
    }

    private int getLink(PayrollEntity entity) {

        if (entity.getType() == EntryType.PROVENTO.getId()) {
            return AccountLink.SALARIO.getId();
        }

        if (entity.getType() == EntryType.BENEFICIO.getId()) {
            return entity.getEvent() == PayrollEvent.FLASH.getId() ? AccountLink.FLASH.getId() : AccountLink.ALELO.getId();
        }

        return 0;
    }

    private String getName(PayrollEntity entity) {

        if (entity.getType() == EntryType.PROVENTO.getId()) {
            return "Salário";
        }

        if (entity.getType() == EntryType.BENEFICIO.getId()) {
            return entity.getEvent() == PayrollEvent.FLASH.getId() ? "Depósito de Flexível" : "Seu Benefício Caiu";
        }

        return "";
    }

    @Override
    public EventType getTypeHandler() {
        return EventType.PAYROLL;
    }
}
