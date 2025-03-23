package site.easy.to.build.crm.service.TauxAlert;


import org.springframework.validation.annotation.Validated;
import site.easy.to.build.crm.entity.TauxAlert;

public interface TauxAlertService {

    public void save(TauxAlert tauxAlert);

    public TauxAlert findLastTauxAlert();

}
