package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    @Positive(message = "Valeur negatif donnée, doit etre positif")
    private Double montant;

    @Column(name = "dt_ajout_budget", nullable = false)
    private LocalDateTime dtAjoutBudget;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}