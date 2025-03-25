package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.service.data.DataManagerService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/manager/data")
public class DataManagerController {

    @Autowired
    private DataManagerService dataManagerService;

    @GetMapping("/import")
    public String importView(Model model) {

        return "data/import";
    }

    @PostMapping("/import")
    public String handleFileUpload(@RequestParam("file1") MultipartFile file,
                                   @RequestParam("file2") MultipartFile file2,
                                   @RequestParam("file3") MultipartFile file3,
                                   RedirectAttributes redirectAttributes) {
        List<String> errors = new ArrayList<>();
        // Validation des fichiers
        if (file.isEmpty()) {
            errors.add("Please select a file 1");
        }
        if (file2.isEmpty()) {
            errors.add("Please select a file 2");
        }
        if (file3.isEmpty()) {
            errors.add("Please select a file 3");
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", errors);
            return "redirect:/manager/data/import";
        }

        // Validation du format CSV
        if (!file.getOriginalFilename().endsWith(".csv")) {
            errors.add("Please select a .csv file for file 1");
        }
        if (!file2.getOriginalFilename().endsWith(".csv")) {
            errors.add("Please select a .csv file for file 2");
        }
        if (!file3.getOriginalFilename().endsWith(".csv")) {
            errors.add("Please select a .csv file for file 3");
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", errors);
            return "redirect:/manager/data/import";
        }

        try {
            // Appel du service pour l'import
            dataManagerService.importDataWithTransaction(file, file2, file3, errors);
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/manager/data/import";
            }
            redirectAttributes.addFlashAttribute("message", "Successfully imported data");
            return "redirect:/manager/data/import";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("st", "error");
            return "redirect:/manager/data/import";
        }
    }


    @GetMapping("/reinit")
    public String reinit(RedirectAttributes redirectAttributes) {
        try {
            dataManagerService.reinitializeData();
            redirectAttributes.addFlashAttribute("status", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            return "error/500";
        }
        return "redirect:/manager/data/import";
    }


    @GetMapping("/generation")
    public String GeneratedDataView(Model model) {
        return "data/generated";
    }
}
