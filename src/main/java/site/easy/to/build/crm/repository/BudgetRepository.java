package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.dtos.BudgetClientTotal;
import site.easy.to.build.crm.entity.Budget;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    @Query("SELECT b FROM Budget b WHERE b.customer.user.id = :userId")
    public List<Budget> getBudgetsByUserId(@Param("userId") int userId);


    @Query("""
    SELECT new site.easy.to.build.crm.dtos.BudgetClientTotal(
        b.customer,
        SUM(b.montant)
    )
    FROM Budget b
    GROUP BY b.customer
""")
    public List<BudgetClientTotal> getBudgetClientTotals();

    @Query("""
    SELECT new site.easy.to.build.crm.dtos.BudgetClientTotal(
        b.customer,
        SUM(b.montant)
    )
    FROM Budget b
    GROUP BY b.customer
    HAVING b.customer.user.id = :userId
    """)
    public List<BudgetClientTotal> getBudgetByUsersCustomer(@Param("userId")int userId);

    @Query("SELECT sum (b.montant) FROM Budget b WHERE b.customer.customerId = :customerId")
    public Double getSumOfMontantByCustomer(@Param("customerId")int customerId);

}
