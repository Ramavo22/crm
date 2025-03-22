package site.easy.to.build.crm.controller.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.entity.TauxAlert;
import site.easy.to.build.crm.service.TauxAlert.TauxAlertService;

@RestController
@RequestMapping("/api/taux-alert")
public class TauxAlertController {

    @Autowired
    private TauxAlertService tauxAlertService;

    @PostMapping("/save")
    public void save(@Validated TauxAlert tauxAlert) {
        tauxAlertService.save(tauxAlert);
    }
}
