package br.com.finance.modules.salary.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salary_detail_item_type", schema = "dbo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryDetailItemTypeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

}
