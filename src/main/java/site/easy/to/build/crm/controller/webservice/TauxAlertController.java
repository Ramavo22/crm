package site.easy.to.build.crm.controller.webservice;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.entity.TauxAlert;
import site.easy.to.build.crm.service.TauxAlert.TauxAlertService;
import site.easy.to.build.crm.util.DataTransfertObject;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/budget-alert")
public class TauxAlertController {

    @Autowired
    private TauxAlertService tauxAlertService;

    @PostMapping("/save")
    public ResponseEntity<DataTransfertObject> save(@RequestBody TauxAlertWebService tauxAlertWebService) {
        System.out.println("\n========================FROM WEB SERVICE======================\n");
        System.out.println(tauxAlertWebService);
        System.out.println("\n==============================================================\n");
        TauxAlert tauxAlert = new TauxAlert();
        tauxAlert.setPourcentage(Double.parseDouble(tauxAlertWebService.getPourcentage()));
        tauxAlert.setSince(LocalDateTime.now());
        tauxAlertService.save(tauxAlert);
        DataTransfertObject dataTransfertObject = new DataTransfertObject();
        dataTransfertObject.setMessage("Configuration mis à jour");
        dataTransfertObject.setStatusCode(HttpStatus.CREATED.value());
        dataTransfertObject.setErreur(null);
        return ResponseEntity.ok(dataTransfertObject);
    }
}

@Data
class TauxAlertWebService{
    String pourcentage;
    String date;
}




