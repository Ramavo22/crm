package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager/budget")
public class BudgetController {

    @GetMapping
    public String budgetView(Model model) {
        return "budget/add-budget";
    }


}
