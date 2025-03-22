package site.easy.to.build.crm.service.TauxAlert;


import site.easy.to.build.crm.entity.TauxAlert;

public interface TauxAlertService {

    public void save(TauxAlert tauxAlert);

    public TauxAlert findLastTauxAlert();

}
