package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
