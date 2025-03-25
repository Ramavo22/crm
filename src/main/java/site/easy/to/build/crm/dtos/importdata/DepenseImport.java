package site.easy.to.build.crm.dtos.importdata;

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
    Double depense;
    long lineNumber;





}
