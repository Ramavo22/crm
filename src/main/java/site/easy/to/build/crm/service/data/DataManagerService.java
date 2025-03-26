package site.easy.to.build.crm.service.data;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.dtos.importdata.CustomerImport;
import site.easy.to.build.crm.dtos.importdata.DepenseImport;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.EmailTokenUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
* @Author Fanantenana
* */
@Service
public class DataManagerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;
    // Transaction globale gérée par Spring
    public void importDataWithTransaction(MultipartFile dataCsv,MultipartFile dataCsv2,MultipartFile dataCsv3, List<String> errors) {
        // TransactionTemplate pour gérer explicitement la transaction
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(status -> {
            try {
                // Appel à importData
                importData(dataCsv, errors);

                // Appel à importData2
                importData2(dataCsv2, errors);

                importData3(dataCsv3,errors);

            } catch (Exception e) {
                status.setRollbackOnly(); // Annulation de la transaction si une erreur survient
                errors.add("Erreur lors de l'importation des données : " + e.getMessage());
            }
            if (!errors.isEmpty()) {
                status.setRollbackOnly();
            }
            return null;
        });
    }

    public void importData(MultipartFile dataCsv, List<String> errors) {
        String sql = "INSERT INTO customer (email,name, country, phone,user_id,created_at) VALUES (?, ?, ?, ?, ?, ?)";
        List<CustomerImport> customers = new ArrayList<>();
        List<User> users = userService.findAll();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(dataCsv.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                CustomerImport customerImport = new CustomerImport();
                customerImport.setCustomerEmail(record.get("customer_email"));
                customerImport.setCustomerName(record.get("customer_name"));
                customers.add(customerImport);
                Collections.shuffle(users);
                customerImport.setUser(users.get(0));

            }

            // Insertion des données avec transaction
            jdbcTemplate.batchUpdate(sql, customers, customers.size(), (ps, customer) -> {
                ps.setString(1, customer.getCustomerEmail());
                ps.setString(2, customer.getCustomerName());
                ps.setString(3, customer.getCountry());
                ps.setString(4, customer.getPhoneNumber());
                ps.setInt(5, customer.getUser().getId());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            });



        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'importation du fichier CSV : " + e.getMessage(), e);
        }
    }

    public void importData2(MultipartFile dataCsv, List<String> errors) {
        List<Customer> customers = customerService.findAll();
        String sql = "INSERT INTO budget (montant, dt_ajout_budget, customer_id) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(dataCsv.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                String volaString = record.get("Budget");
                String email = record.get("customer_email");

                volaString = volaString.replace(",", ".");

                Double montant;
                try {
                    montant = Double.parseDouble(volaString);
                } catch (NumberFormatException e) {
                    long lineNumber = record.getRecordNumber() + 1;
                    errors.add("Fichier: " + dataCsv.getOriginalFilename() + ", ligne: " + lineNumber
                            + ". Valeur du budget invalide: " + volaString);
                    continue; // Passer à la ligne suivante si erreur
                }

                Customer customer = customers.stream()
                        .filter(c -> email.equals(c.getEmail()))
                        .findFirst()
                        .orElse(null);

                if (customer == null) {
                    long lineNumber = record.getRecordNumber() + 1;
                    errors.add("Fichier: " + dataCsv.getOriginalFilename() + ", ligne: " + lineNumber
                            + ". L'email : " + email + " n'existe pas !");
                    continue;
                }

                Budget budget = new Budget();
                budget.setMontant(montant);
                budget.setDtAjoutBudget(LocalDateTime.now());
                budget.setCustomer(customer);

                // Validation
                Set<ConstraintViolation<Budget>> constraintViolations = validator.validate(budget);
                if (!constraintViolations.isEmpty()) {
                    for (ConstraintViolation<Budget> violation : constraintViolations) {
                        long lineNumber = record.getRecordNumber() + 1;
                        errors.add("Fichier: " + dataCsv.getOriginalFilename() + ", ligne: " + lineNumber
                                + ". " + violation.getMessage() + " sur " + violation.getPropertyPath());
                    }
                    continue;
                }

                // Ajout des valeurs dans le batch
                batchArgs.add(new Object[]{budget.getMontant(), budget.getDtAjoutBudget(), budget.getCustomer().getCustomerId()});
            }

            // Exécution du batch uniquement si on a des données valides
            if (!batchArgs.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'importation du fichier CSV : " + e.getMessage(), e);
        }
    }

    public void importData3(MultipartFile csv, List<String> errors) {
        String sqlLead = """
        INSERT INTO trigger_lead 
            (customer_id, user_id, name, phone, employee_id, status, created_at, depense) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        String sqlTicket = """
        INSERT INTO trigger_ticket 
            (subject, description, status, priority, customer_id, manager_id, employee_id, created_at,depense) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)
    """;

        List<Object[]> leadParams = new ArrayList<>();
        List<Object[]> ticketParams = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csv.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<DepenseImport> depenseImports = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                String email = record.get("customer_email");
                String subjectOrName = record.get("subject_or_name");
                String type = record.get("type");
                String status = record.get("status");
                String volaString = record.get("expense").replace(",", ".");


                Double montant;
                try {
                    montant = Double.parseDouble(volaString);
                } catch (NumberFormatException e) {
                    long lineNumber = record.getRecordNumber() + 1;
                    errors.add("Fichier: " + csv.getOriginalFilename() + ", ligne: " + lineNumber
                            + ". Valeur du budget invalide: " + volaString);
                    continue; // Passer à la ligne suivante si erreur
                }

                depenseImports.add(new DepenseImport(email, subjectOrName, type, status, montant, record.getRecordNumber() + 1));
            }

            List<Customer> customers = customerService.findAll();
            List<User> users = userService.findAll();
            String[] priorities = {"low", "medium", "high", "closed", "urgent", "critical"};

            Random random = new Random();

            for (DepenseImport depenseImport : depenseImports) {
                Customer customer = customers.stream()
                        .filter(c -> depenseImport.getCustomerEmail().equals(c.getEmail()))
                        .findFirst()
                        .orElse(null);

                if (customer == null) {
                    errors.add("Fichier: " + csv.getOriginalFilename() + ", ligne: " + depenseImport.getLineNumber()
                            + ". L'email : " + depenseImport.getCustomerEmail() + " n'existe pas !");
                    continue;
                }

                User manager = users.stream()
                        .filter(u -> u.getRoles().get(0).getName().equals("ROLE_MANAGER"))
                        .findFirst()
                        .orElse(null);

                Collections.shuffle(users);
                User employee = users.get(0);


                // Validation des statuts et priorités
                boolean validLeadStatus = depenseImport.getType().equals("lead") &&
                        depenseImport.getStatus().matches("^(meeting-to-schedule|scheduled|archived|success|assign-to-sales)$");

                boolean validTicketStatus = depenseImport.getType().equals("ticket") &&
                        depenseImport.getStatus().matches("^(open|assigned|on-hold|in-progress|resolved|closed|reopened|pending-customer-response|escalated|archived)$");


                if (!validLeadStatus && depenseImport.getType().equals("lead")) {
                    errors.add("Fichier: " + csv.getOriginalFilename() + ", ligne: " + depenseImport.getLineNumber()
                            + ". Statut de Lead invalide : " + depenseImport.getStatus());
                    continue;
                }

                if (!validTicketStatus && depenseImport.getType().equals("ticket")) {
                    errors.add("Fichier: " + csv.getOriginalFilename() + ", ligne: " + depenseImport.getLineNumber()
                            + ". Statut de Ticket invalide : " + depenseImport.getStatus());
                    continue;
                }

                switch (depenseImport.getType()) {
                    case "lead":
                        leadParams.add(new Object[]{
                                customer.getCustomerId(),
                                manager != null ? manager.getId() : null,
                                depenseImport.getSubjectName(),
                                "+1" + (1000000000L + random.nextInt(900000000)),
                                employee.getId(),
                                depenseImport.getStatus(),
                                LocalDateTime.now(),
                                depenseImport.getDepense()
                        });
                        break;

                    case "ticket":
                        ticketParams.add(new Object[]{
                                depenseImport.getSubjectName(),
                                "", // description vide
                                depenseImport.getStatus(),
                                priorities[random.nextInt(priorities.length)],
                                customer.getCustomerId(),
                                manager != null ? manager.getId() : null,
                                employee.getId(),
                                LocalDateTime.now(),
                                depenseImport.getDepense()
                        });
                        break;

                    default:
                        errors.add("Fichier: " + csv.getOriginalFilename() + ", ligne: " + depenseImport.getLineNumber()
                                + ". Type invalide : " + depenseImport.getType());
                }
            }

            //  Exécution des batchs après la boucle
            if (!leadParams.isEmpty()) {
                jdbcTemplate.batchUpdate(sqlLead, leadParams);
            }
            if (!ticketParams.isEmpty()) {
                jdbcTemplate.batchUpdate(sqlTicket, ticketParams);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'importation du fichier CSV : " + e.getMessage(), e);
        }
    }

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
                status.setRollbackOnly();
                ex.printStackTrace();// En cas d'erreur : rollback
                throw new RuntimeException("Erreur lors du truncate en batch "+ex.getCause()+" : " + ex.getMessage(), ex);
            }
        });
    }

}



