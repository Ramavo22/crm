package site.easy.to.build.crm.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.annotation.Documented;
import java.util.List;

@Service
public class DataManagerService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PlatformTransactionManager transactionManager;


    /*
    * Used to add data from a csv file
    * */
    public void importData(){

    }
    /*
    *   Used When you want to clean your database expect users for testing withing an empty data
    * */
    public void reinitializeData(){
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.executeWithoutResult(status -> {
            try {
                // 1. Désactiver les contraintes FK
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

                // 2. Récupérer les tables à truncater
                String sql = """
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'crm'
                  AND table_name NOT IN ('users', 'user_roles', 'user_profile', 'oauth_users','roles')
            """;
                List<String> tableNames = jdbcTemplate.queryForList(sql, String.class);

                // 3. Préparer les commandes TRUNCATE
                List<String> truncateSQLs = tableNames.stream()
                        .map(name -> "TRUNCATE TABLE `" + name + "`")
                        .toList();

                // 4. Exécuter en batch
                jdbcTemplate.batchUpdate(truncateSQLs.toArray(new String[0]));

                // 5. Réactiver les FK
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

            } catch (Exception ex) {
                status.setRollbackOnly();  // En cas d'erreur : rollback
                throw new RuntimeException("Erreur lors du truncate en batch", ex);
            }
        });
    }
}
