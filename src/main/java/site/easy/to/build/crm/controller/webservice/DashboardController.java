package site.easy.to.build.crm.controller.webservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.service.Budget.BudgetService;
import site.easy.to.build.crm.service.dashboard.DashboardService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.util.DataTransfertObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final TicketService ticketService;
    private final LeadService leadService;
    private final BudgetService budgetService;

    public DashboardController(DashboardService dashboardService, TicketService ticketService, LeadService leadService,BudgetService budgetService) {
        this.dashboardService = dashboardService;
        this.ticketService = ticketService;
        this.leadService = leadService;
        this.budgetService = budgetService;
    }

    // add year for filter
    @GetMapping
    public ResponseEntity<DataTransfertObject> getDashboardData(){

        LocalDateTime now = LocalDateTime.now();
        DataTransfertObject dto = new DataTransfertObject();
        dto.setStatusCode(200);
        dto.setErreur(null);
        dto.setMessage("dto loaded successfully");
        Map<String,Object> data = new HashMap<>();
        dto.setData(data);

        Map<String,Integer> clientCreatedPerMonth = dashboardService.getCustomerCountPerMonth(now.getYear());
        data.put("clientCreatedPerMonth", clientCreatedPerMonth);

        Map<String,Double> leadExpensePerMonth = dashboardService.getLeadExpensePerMonth(now.getYear());
        data.put("leadExpensePerMonth", leadExpensePerMonth);

        Map<String,Double> ticketExpensePerYear = dashboardService.getTicketExpensePerYear(now.getYear());
        data.put("ticketExpensePerYear", ticketExpensePerYear);

        double totalLeadYear = leadService.getLeadCountByYear(now.getYear());
        data.put("totalLeadYear", totalLeadYear);

        double totalTicketYear = ticketService.getTicketCountByYear(now.getYear());
        data.put("totalTicketYear", totalTicketYear);

        double totalBudget = budgetService.getTotalBudget();
        data.put("totalBudget", totalBudget);

        return ResponseEntity.ok(dto);

    }
}
