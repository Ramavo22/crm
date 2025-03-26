package site.easy.to.build.crm.controller.webservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.dtos.TicketDTO;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.util.DataTransfertObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ticket")
public class TicketControllerApi {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public ResponseEntity<DataTransfertObject> getTicket(){
        DataTransfertObject dataTransfertObject = new DataTransfertObject();
        dataTransfertObject.setStatusCode(200);
        dataTransfertObject.setMessage("loaded successfully");
        dataTransfertObject.setErreur(null);
        Map<String,Object> data = new HashMap<>();
        dataTransfertObject.setData(data);

        List<TicketDTO> ticketDTOS = this.ticketService.getTicketsDtos();
        data.put("ticket", ticketDTOS);

        return ResponseEntity.ok(dataTransfertObject);

    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<DataTransfertObject> deleteTicket(@PathVariable("id") int id) {
        // Créer une réponse par défaut
        DataTransfertObject dataTransfertObject = new DataTransfertObject();

        try {
            // Trouver le ticket à supprimer
            Ticket ticket = ticketService.findByTicketId(id);

            if (ticket != null) {
                // Si le ticket existe, le supprimer
                ticketService.delete(ticket);

                // Réponse succès
                dataTransfertObject.setStatusCode(200);
                dataTransfertObject.setMessage("Ticket deleted successfully");
                dataTransfertObject.setErreur(null);
                dataTransfertObject.setData(null);

                return ResponseEntity.ok(dataTransfertObject);
            } else {
                // Si le ticket n'existe pas
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
    public ResponseEntity<DataTransfertObject> updateTicket(@RequestBody ticketDepense ticketDepense) {
        DataTransfertObject dataTransfertObject = new DataTransfertObject();

        // Validation de la requête : vérifier que les données sont présentes et valides
        if (ticketDepense.getId() == null || ticketDepense.getDepense() == null) {
            dataTransfertObject.setStatusCode(400);
            dataTransfertObject.setMessage("ID et dépense sont requis");
            dataTransfertObject.setErreur(null);
            return ResponseEntity.status(400).body(dataTransfertObject);
        }

        try {
            // Trouver le ticket par son ID
            Ticket ticket = ticketService.findByTicketId(ticketDepense.getId());
            if (ticket != null) {
                // Mettre à jour la dépense
                ticket.setDepense(ticketDepense.getDepense());
                ticketService.save(ticket);

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
static class ticketDepense{
        Integer id;
        Double depense;
}

}
