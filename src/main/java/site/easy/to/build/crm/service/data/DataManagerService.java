package site.easy.to.build.crm.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Documented;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
* @Author Fanantenana
* */
@Service
public class DataManagerService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PlatformTransactionManager transactionManager;

    /*
    * Used to add data from a csv file
    * */
    public void importData(MultipartFile dataCsv) {
        String sql = "INSERT INTO employee (id, username, first_name, last_name, email, password, provider) VALUE (?,?,?,?,?,?,?)";
        List<Object[]> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(dataCsv.getInputStream(), StandardCharsets.UTF_8))) {

            // Skip the header row
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] dataLine = line.split(",",-1);


                // Ajouter les arguments dans le batch
                data.add(new Object[]{
                        Integer.parseInt(dataLine[0]),
                        dataLine[1],
                        dataLine[2],
                        dataLine[3],
                        dataLine[4],
                        dataLine[5],
                        dataLine[6]
                });

                // Si le batch atteint une taille de 1000, on exécute
                if (data.size() == 1000) {
                    jdbcTemplate.batchUpdate(sql, data);
                   data.clear();
                }
            }

            // Insérer le dernier lot
            if (!data.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'importation du fichier CSV : " + e.getMessage(), e);
        }
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
