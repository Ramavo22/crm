package site.easy.to.build.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeadDTO{
    Integer id;
    String lead;
    String phone;
    String status;
    String customer;
    String assignedEmployee;
    Double expense;
}
