package com.evaluation.erpnext_spring.repository;

import com.evaluation.erpnext_spring.model.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, String> {

    // Trouver tous les bulletins d'un employé donné
    List<SalarySlip> findByEmployee(String employee);

    // Trouver les bulletins d'un employé sur une période donnée
    List<SalarySlip> findByEmployeeAndStartDateBetween(String employee, LocalDate start, LocalDate end);

    // Trouver les bulletins par statut (ex : "Paid", "Draft", etc.)
    List<SalarySlip> findByStatus(String status);

    // Trouver les bulletins actifs (docstatus = 1)
    List<SalarySlip> findByDocstatus(Integer docstatus);

    // Rechercher par entreprise et période
    List<SalarySlip> findByCompanyAndStartDateBetween(String company, LocalDate start, LocalDate end);

    // Requête JPQL personnalisée pour rechercher par salaire brut minimum
    @Query("SELECT s FROM SalarySlip s WHERE s.grossPay >= :minGrossPay")
    List<SalarySlip> findByGrossPayGreaterThanEqual(@Param("minGrossPay") java.math.BigDecimal minGrossPay);

    // Recherche par employé et statut avec tri par date de début décroissante
    List<SalarySlip> findByEmployeeAndStatusOrderByStartDateDesc(String employee, String status);

}
