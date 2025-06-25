package com.evaluation.erpnext_spring.repository;

import com.evaluation.erpnext_spring.model.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, String> {

    // Trouver les structures salariales actives
    List<SalaryStructure> findByIsActive(Integer isActive);

    // Trouver la structure salariale par entreprise
    List<SalaryStructure> findByCompany(String company);

    // Trouver la structure salariale par fréquence de paie
    List<SalaryStructure> findByPayrollFrequency(String payrollFrequency);

    // Recherche par nom partiel de la structure salariale
    List<SalaryStructure> findBySalaryComponentContainingIgnoreCase(String salaryComponent);

    // Requête JPQL pour rechercher par montant minimum net à payer
    @Query("SELECT s FROM SalaryStructure s WHERE s.netPay >= :minNetPay")
    List<SalaryStructure> findByNetPayGreaterThanEqual(@Param("minNetPay") java.math.BigDecimal minNetPay);

    // Trouver par mode de paiement
    List<SalaryStructure> findByModeOfPayment(String modeOfPayment);

}
