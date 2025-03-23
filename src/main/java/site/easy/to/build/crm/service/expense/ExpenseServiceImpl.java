package site.easy.to.build.crm.service.expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Double getTotalExpenseByCustomer(Integer customerId) {
        String sql;
        Object[] params;

        if (customerId != null) {
            sql = """
            SELECT COALESCE(SUM(depense_total), 0) AS total_depense
            FROM (
                SELECT depense AS depense_total FROM trigger_lead WHERE customer_id = ?
                UNION ALL
                SELECT depense AS depense_total FROM trigger_ticket WHERE customer_id = ?
            ) AS all_depenses
            """;
            params = new Object[]{customerId, customerId};
        } else {
            sql = """
            SELECT COALESCE(SUM(depense_total), 0) AS total_depense
            FROM (
                SELECT depense AS depense_total FROM trigger_lead
                UNION ALL
                SELECT depense AS depense_total FROM trigger_ticket
            ) AS all_depenses
            """;
            params = new Object[]{}; // Aucun paramètre
        }

        return jdbcTemplate.queryForObject(sql, params, Double.class);
    }

}
