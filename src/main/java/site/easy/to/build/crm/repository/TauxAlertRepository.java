package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.TauxAlert;

import java.util.Optional;

@Repository
public interface TauxAlertRepository extends JpaRepository<TauxAlert, Integer> {

    @Query(value = "SELECT * FROM taux_alert ORDER BY since DESC LIMIT 1", nativeQuery = true)
    Optional<TauxAlert> findLatestTauxAlertNative();


}
