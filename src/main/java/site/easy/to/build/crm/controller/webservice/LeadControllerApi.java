package site.easy.to.build.crm.controller.webservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.dtos.LeadDTO;
import site.easy.to.build.crm.dtos.TicketDTO;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
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

    @GetMapping("/delete/{id}")
    public ResponseEntity<DataTransfertObject> deleteTicket(@PathVariable("id") int id) {
        // Créer une réponse par défaut
        DataTransfertObject dataTransfertObject = new DataTransfertObject();

        try {
            // Trouver le lead à supprimer
            Lead lead = leadRepository.findByLeadId(id);

            if (lead != null) {
                // Si le lead existe, le supprimer
                leadRepository.delete(lead);

                // Réponse succès
                dataTransfertObject.setStatusCode(200);
                dataTransfertObject.setMessage("Ticket deleted successfully");
                dataTransfertObject.setErreur(null);
                dataTransfertObject.setData(null);

                return ResponseEntity.ok(dataTransfertObject);
            } else {
                // Si le lead n'existe pas
                dataTransfertObject.setStatusCode(404);
                dataTransfertObject.setMessage("Ticket with ID " + id + " not found");
                dataTransfertObject.setErreur(null);
                dataTransfertObject.setData(null);

                return ResponseEntity.status(404).body(dataTransfertObject);
            }

        } catch (Exception e) {
            // Si une erreur se produit, renvoyer une erreur
            dataTransfertObject.setStatusCode(500);
            dataTransfertObject.setMessage("Internal server error");
            dataTransfertObject.setErreur(null);
            dataTransfertObject.setData(null);

            return ResponseEntity.status(500).body(dataTransfertObject);
        }
    }

    // Utilisation de @PutMapping pour la mise à jour
    @PutMapping("/update")
    public ResponseEntity<DataTransfertObject> updateTicket(@RequestBody LeadDepense depense) {
        DataTransfertObject dataTransfertObject = new DataTransfertObject();

        // Validation de la requête : vérifier que les données sont présentes et valides
        if (depense.getId() == null || depense.getDepense() == null) {
            dataTransfertObject.setStatusCode(400);
            dataTransfertObject.setMessage("ID et dépense sont requis");
            dataTransfertObject.setErreur(null);
            return ResponseEntity.status(400).body(dataTransfertObject);
        }

        try {
            // Trouver le lead par son ID
            Lead lead = leadRepository.findByLeadId(depense.getId());
            if (lead != null) {
                // Mettre à jour la dépense
                lead.setDepense(depense.getDepense());
                leadRepository.save(lead);

                // Réponse de succès
                dataTransfertObject.setStatusCode(200);
                dataTransfertObject.setMessage("Ticket updated successfully");
                dataTransfertObject.setErreur(null);
                dataTransfertObject.setData(null);

                return ResponseEntity.ok(dataTransfertObject);
            } else {
                // Ticket non trouvé
                dataTransfertObject.setStatusCode(404);
                dataTransfertObject.setMessage("Ticket not found");
                dataTransfertObject.setErreur(null);
                dataTransfertObject.setData(null);

                return ResponseEntity.status(404).body(dataTransfertObject);
            }

        } catch (Exception e) {
            // Gestion d'erreur
            dataTransfertObject.setStatusCode(500);
            dataTransfertObject.setMessage("Internal server error");
            dataTransfertObject.setErreur(null);
            dataTransfertObject.setData(null);

            return ResponseEntity.status(500).body(dataTransfertObject);
        }
    }

@Data
@AllArgsConstructor
@NoArgsConstructor
static class LeadDepense{
        Integer id;
        Double depense;
}
}
