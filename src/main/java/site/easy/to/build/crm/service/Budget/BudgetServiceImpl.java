package site.easy.to.build.crm.service.Budget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.dtos.BudgetClientTotal;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.service.TauxAlert.TauxAlertService;
import site.easy.to.build.crm.util.exception.EntityNotFoundException;

import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TauxAlertService tauxAlertService;

    @Override
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @Override
    public Budget getBudgetById(int id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
    }

    @Override
    public void save(Budget budget) {
        budgetRepository.save(budget);
    }

    @Override
    public List<Budget> getBudgetsByUserId(int userId) {
        return budgetRepository.getBudgetsByUserId(userId);
    }

    @Override
    public List<BudgetClientTotal> getBudgetClientTotalsByUserId(int userId) {
        return budgetRepository.getBudgetByUsersCustomer(userId);
    }

    @Override
    public List<BudgetClientTotal> getBudgetClientTotals() {
        return budgetRepository.getBudgetClientTotals();
    }

    @Override
    public Double getSumOfMontantByCustomerId(int userId) {
        return budgetRepository.getSumOfMontantByCustomer(userId);
    }


}
