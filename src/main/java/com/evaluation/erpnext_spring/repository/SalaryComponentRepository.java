package com.evaluation.erpnext_spring.repository;

import com.evaluation.erpnext_spring.model.SalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, String> {

    // Trouver par type de composant (ex: "Earning", "Deduction")
    List<SalaryComponent> findByType(String type);

    // Trouver par nom partiel (ignore case)
    List<SalaryComponent> findBySalaryComponentContainingIgnoreCase(String keyword);

    // Trouver les composants actifs (disabled = 0 ou null)
    List<SalaryComponent> findByDisabledIsNullOrDisabled(Integer disabled);

    // Trouver les composants applicables à la taxe
    List<SalaryComponent> findByIsTaxApplicable(Integer isTaxApplicable);

}
