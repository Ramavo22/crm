CREATE VIEW v_budget_customer AS
SELECT
    b.customer_id,
    SUM(b.montant) AS total_budget
FROM
    budget b
GROUP BY
    b.customer_id;