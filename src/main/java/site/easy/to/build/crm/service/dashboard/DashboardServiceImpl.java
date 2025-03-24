package site.easy.to.build.crm.service.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Integer> getCustomerCountPerMonth(int year) {
        String sql = """
                        SELECT m.month_num,
                               COALESCE(c.total, 0) AS total
                        FROM (
                            SELECT 1 AS month_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
                            UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8
                            UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
                        ) AS m
                        LEFT JOIN (
                            SELECT MONTH(created_at) AS month, COUNT(*) AS total
                            FROM customer
                            WHERE YEAR(created_at) = ?
                            GROUP BY MONTH(created_at)
                        ) AS c ON m.month_num = c.month
                        ORDER BY m.month_num
                """;


        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, year);

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            int monthNum = ((Number) row.get("month_num")).intValue();
            int total = ((Number) row.get("total")).intValue();

            // Convertir en nom de mois français, ex: janvier, février...
            String monthName = Month.of(monthNum)
                    .getDisplayName(TextStyle.SHORT, Locale.FRENCH);  // ou Locale.getDefault()

            result.put(monthName, total);
        }
        return result;
    }


}
