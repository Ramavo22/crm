package site.easy.to.build.crm.controller.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.dtos.LeadDTO;
import site.easy.to.build.crm.dtos.TicketDTO;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.util.DataTransfertObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lead")
public class LeadControllerApi {

    @Autowired
    private LeadRepository leadRepository;


    @GetMapping
    public ResponseEntity<DataTransfertObject> getLead() {
        DataTransfertObject dataTransfertObject = new DataTransfertObject();
        dataTransfertObject.setStatusCode(200);
        dataTransfertObject.setMessage("loaded successfully");
        dataTransfertObject.setErreur(null);
        Map<String,Object> data = new HashMap<>();
        dataTransfertObject.setData(data);

        List<LeadDTO> leads = leadRepository.findLeadDTO();
        data.put("leads", leads);

        return ResponseEntity.ok(dataTransfertObject);
    }
}
