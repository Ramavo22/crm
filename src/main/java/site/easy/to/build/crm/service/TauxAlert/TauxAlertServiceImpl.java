package site.easy.to.build.crm.service.TauxAlert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.TauxAlert;
import site.easy.to.build.crm.repository.TauxAlertRepository;
import site.easy.to.build.crm.util.exception.EntityNotFoundException;


@Service
public class TauxAlertServiceImpl implements TauxAlertService {


    @Autowired
    private TauxAlertRepository tauxAlertRepository;


    public void save(TauxAlert tauxAlert) {tauxAlertRepository.save(tauxAlert);}

    public TauxAlert findLastTauxAlert() {
        return tauxAlertRepository.findLatestTauxAlertNative()
                .orElseThrow(() -> new EntityNotFoundException("Taux Alerte du budget pas encore initialiser"));
    }
}
