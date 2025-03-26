package site.easy.to.build.crm.dtos.importdata;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepenseImport {
    String customerEmail;
    String subjectName;
    String type;
    String status;
    @Positive
    Double depense;
    long lineNumber;

}
