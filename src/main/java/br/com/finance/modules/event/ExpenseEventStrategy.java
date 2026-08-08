package br.com.finance.modules.event;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.account.AccountRepository;
import br.com.finance.modules.account.dto.AccountEntity;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventType;
import br.com.finance.modules.event.dto.ExpensePayload;
import br.com.finance.modules.expense.ExpenseRepository;
import br.com.finance.modules.expense.dto.ExpenseEntity;
import br.com.finance.modules.payroll.dto.EntryType;
import br.com.finance.modules.transaction.TransactionExpenseRepository;
import br.com.finance.modules.transaction.TransactionRepository;
import br.com.finance.modules.transaction.dto.TransactionEntity;
import br.com.finance.modules.transaction.dto.TransactionExpenseEntity;
import br.com.finance.modules.transaction.dto.TransactionMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ExpenseEventStrategy implements EventStrategy<ExpensePayload> {

    private final TransactionRepository transactionRepository;
    private final TransactionExpenseRepository transactionExpenseRepository;
    private final AccountRepository accountRepository;
    private final ExpenseRepository expenseRepository;

    public ExpenseEventStrategy(TransactionRepository transactionRepository,
                                TransactionExpenseRepository transactionExpenseRepository,
                                AccountRepository accountRepository,
                                ExpenseRepository expenseRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionExpenseRepository = transactionExpenseRepository;
        this.accountRepository = accountRepository;
        this.expenseRepository = expenseRepository;
    }

    public void process(ExpensePayload payload, EventAction action) {
        if (payload.entities().isEmpty()) return;
        if (action == EventAction.INTEGRATED)
            processIntegrated(payload);
        if (action == EventAction.DELETED)
            processDeleted(payload);
    }

    private void processDeleted(ExpensePayload payload) {

        List<TransactionExpenseEntity> transactionExpenses = this.transactionExpenseRepository.findByExpense(payload.entities().stream()
                .map(ExpenseEntity::getId)
                .toList());

        if (transactionExpenses.isEmpty()) {
            this.expenseRepository.deleteAll(payload.entities());
            return;
        }

        List<TransactionEntity> transactions = this.transactionRepository
                .findAllByIds(transactionExpenses.stream().map(TransactionExpenseEntity::getTransactionAccount).toList());

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
            account.setBalance(account.getBalance().add(v.getAmount()));
        });

        this.transactionExpenseRepository.deleteAll(transactionExpenses);
        this.transactionRepository.deleteAll(transactions);
        this.expenseRepository.deleteAll(payload.entities());
        this.accountRepository.saveAll(accounts.values());
    }

    public void processIntegrated(ExpensePayload payload) {

        findMethodOrThrow(payload.method());

        AccountEntity account = this.accountRepository.findByUserId(
                payload.entities().getFirst().getUserId(),
                payload.entities().getFirst().getCompetence(),
                payload.account()
        ).orElseThrow(() -> ApiException.badRequest(List.of(
                new Violacao("account", "Conta não encontrado: " + payload.account())
        )));

        Set<Long> integrados = this.transactionExpenseRepository.findIdsByExpense(payload.entities().stream()
                .map(ExpenseEntity::getId).toList());

        BigDecimal total = payload.entities().stream()
                .map(ExpenseEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (account.getBalance().compareTo(total) < 0) {
            throw ApiException.badRequest(List.of(
                    new Violacao("account", "Saldo da conta insuficiente: " + account.getBalance())
            ));
        }

        payload.entities().stream()
                .filter(p -> !integrados.contains(p.getId()))
                .forEach(v -> {

                    TransactionEntity entity = TransactionEntity.builder()
                            .account(account.getId())
                            .method(payload.method())
                            .name(v.getName())
                            .debit(false)
                            .amount(v.getAmount())
                            .dateTransaction(payload.payDue() ? v.getDue() : payload.dateTransaction())
                            .build();

                    account.setBalance(account.getBalance().subtract(v.getAmount()));

                    this.transactionRepository.save(entity);

                    TransactionExpenseEntity origin = TransactionExpenseEntity.builder()
                            .expense(v.getId())
                            .transactionAccount(entity.getId())
                            .build();

                    this.transactionExpenseRepository.save(origin);
                });
        this.accountRepository.save(account);

    }

    private void findMethodOrThrow(int method) {
        if (!TransactionMethod.isValid(method)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("method", "Metodo não encontrado: " + method)
            ));
        }
    }

    @Override
    public EventType getTypeHandler() {
        return EventType.EXPENSE;
    }
}
