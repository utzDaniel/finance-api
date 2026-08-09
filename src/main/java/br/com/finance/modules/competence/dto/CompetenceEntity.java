package br.com.finance.modules.competence.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "competence",
        schema = "dbo",
        indexes = {
                @Index(name = "idx_competence_user_month_year", columnList = "user_id, month_year")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "month_year", nullable = false)
    private LocalDate monthYear;

    @Column(name = "status")
    private int status;

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
