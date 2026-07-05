package br.com.finance.modules.summary.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "salary", schema = "dbo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "competence_date", nullable = false, columnDefinition = "DATETIME2")
    private LocalDate competenceDate;

    @Column(name = "gross_salary", nullable = false, precision = 19, scale = 2)
    private BigDecimal grossSalary = BigDecimal.ZERO;

    @Column(name = "net_salary", nullable = false, precision = 19, scale = 2)
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}