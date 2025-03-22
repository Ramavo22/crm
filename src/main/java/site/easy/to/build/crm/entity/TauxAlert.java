package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "taux_alert")
public class TauxAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "pourcentage", nullable = false, precision = 2, scale = 2)
    @Positive
    private Double pourcentage;

    @NotNull
    @Column(name = "since", nullable = false)
    private LocalDateTime since;

}