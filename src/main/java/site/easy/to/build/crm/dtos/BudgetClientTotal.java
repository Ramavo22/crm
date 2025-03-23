package site.easy.to.build.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.easy.to.build.crm.entity.Customer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetClientTotal {
    Customer customer;
    Double montant;
}