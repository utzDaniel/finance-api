package br.com.finance.modules.salary.dto;

import jakarta.persistence.*;
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
public class SalaryDetailItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code", nullable = false)
    private int code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

}
