package site.easy.to.build.crm.controller.webservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.service.dashboard.DashboardService;
import site.easy.to.build.crm.util.DataTransfertObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping
    public ResponseEntity<DataTransfertObject> getDashboardData(){
        DataTransfertObject dto = new DataTransfertObject();
        dto.setStatusCode(200);
        dto.setErreur(null);
        dto.setMessage("dto loaded successfully");
        Map<String,Object> data = new HashMap<>();
        dto.setData(data);

        Map<String,Integer> clientCreatedPerMonth = dashboardService.getCustomerCountPerMonth(LocalDateTime.now().getYear());
        data.put("clientCreatedPerMonth", clientCreatedPerMonth);
        return ResponseEntity.ok(dto);

    }
}
