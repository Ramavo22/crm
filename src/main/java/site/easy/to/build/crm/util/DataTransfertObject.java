package site.easy.to.build.crm.util;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DataTransfertObject {
    int statusCode;
    String message;
    List<String> erreur;
    Map<String, Object> data;
}
