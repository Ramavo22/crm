package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.dtos.BudgetClientTotal;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.Budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee/budget")
public class BudgetController {

    private final AuthenticationUtils authenticationUtils;
    private UserService userService;
    LeadService leadService;
    CustomerService customerService;
    BudgetService budgetService;

    @Autowired
    public BudgetController(AuthenticationUtils authenticationUtils, UserService userService,
                            LeadService leadService, CustomerService customerService, BudgetService budgetService) {
        this.authenticationUtils = authenticationUtils;
        this.userService = userService;
        this.leadService = leadService;
        this.customerService = customerService;
        this.budgetService = budgetService;
    }

    @GetMapping
    public String budgetView(Authentication authentication,Model model) {
        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);

        if(user.isInactiveUser()) {
            return "error/account-inactive";
        }

        List<Customer> customers = new ArrayList<>();
        if(AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            customers = customerService.findAll();
        } else {
            customers = customerService.findByUserId(user.getId());
        }
        model.addAttribute("customers", customers);
        model.addAttribute("budget", new Budget());
        return "budget/add-budget";
    }


    @PostMapping("/create")
    public String create(@ModelAttribute("budget") @Validated Budget budget, BindingResult bindingResult,
                         @RequestParam("customerId") Integer customerId,
                         Authentication authentication, Model model) {

        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User manager = userService.findById(userId);
        if(manager.isInactiveUser()) {
            return "error/account-inactive";
        }

        if(bindingResult.hasErrors()) {
            return "redirect:/employee/budget";
        }

        Customer customer = customerService.findByCustomerId(customerId);
        budget.setCustomer(customer);
        budget.setDtAjoutBudget(LocalDateTime.now());
        budgetService.save(budget);
        return "redirect:/employee/budget/show-my-customer-budget";
    }

    @GetMapping("/show-my-customer-budget")
    public String showMyBudget(Authentication authentication, Model model) {
        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);
        if(user.isInactiveUser()) {
            return "error/account-inactive";
        }
        List<BudgetClientTotal> budgets = new ArrayList<>();
        if(AuthorizationUtil.hasRole(authentication, "ROLE_MANAGER")) {
            budgets = budgetService.getBudgetClientTotals();
        }
        else {
            budgets = budgetService.getBudgetClientTotalsByUserId(userId);
        }
        model.addAttribute("budgets", budgets);
        return "budget/show-my-budget";

    }


}
