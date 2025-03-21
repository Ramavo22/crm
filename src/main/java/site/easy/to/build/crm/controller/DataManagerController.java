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

@Controller
@RequestMapping("/manager/data")
public class DataManagerController {

    @Autowired
    private DataManagerService dataManagerService;

    @GetMapping("/import")
    public String importView(Model model) {

        return "data/import";
    }

    @PostMapping("/import" +
            "")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "File not uploaded");
            redirectAttributes.addFlashAttribute("st", "error");
            return "redirect:/manager/data/import";
        }
        if (!file.getOriginalFilename().endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("message", "File not uploaded");
            redirectAttributes.addFlashAttribute("st", "error");
            return "redirect:/manager/data/import";
        }
        try {
            dataManagerService.importData(file);
            redirectAttributes.addFlashAttribute("message", "Data imported successfully");
            redirectAttributes.addFlashAttribute("st", "success");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("st", "error");
            return "redirect:/manager/data/import";
        }
        return "redirect:/manager/data/import";
    }

    @GetMapping("/reinit")
    public String reinit(RedirectAttributes redirectAttributes) {
        try {
            dataManagerService.reinitializeData();
            redirectAttributes.addFlashAttribute("status", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            throw new RuntimeException(e);
        }
        return "redirect:/manager/data/import";
    }


    @GetMapping("/generation")
    public String GeneratedDataView(Model model) {
        return "data/generated";
    }
}
