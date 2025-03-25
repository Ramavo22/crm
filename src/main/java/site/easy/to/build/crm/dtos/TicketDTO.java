package site.easy.to.build.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketDTO {
    Integer id;
    String subject;
    String priority;
    String status;
    String customer;
    String assingedEmployee;
    Double expense;
}
