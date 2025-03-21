package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager/data")
public class DataManagerController {


    @GetMapping("/import")
    public String importView(Model model) {

        return "data/import";
    }


    @GetMapping("/generation")
    public String GeneratedDataView(Model model) {
        return "data/generated";
    }
}
