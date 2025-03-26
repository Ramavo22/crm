package site.easy.to.build.crm.service.Budget;

import site.easy.to.build.crm.dtos.BudgetClientTotal;
import site.easy.to.build.crm.entity.Budget;

import java.util.List;

public interface BudgetService {

    public List<Budget> getAllBudgets();

    public Budget getBudgetById(int id);

    public void save(Budget budget);

    public List<Budget> getBudgetsByUserId(int userId);

    public List<BudgetClientTotal> getBudgetClientTotalsByUserId(int userId);

    public List<BudgetClientTotal> getBudgetClientTotals();

    public Double getSumOfMontantByCustomerId(int userId);

    public Double getTotalBudget();

}
