package com.evaluation.erpnext_spring.repository;

import com.evaluation.erpnext_spring.model.SalaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryDetailRepository extends JpaRepository<SalaryDetail, String> {

    // Trouver tous les détails (earnings/deductions) par bulletin de salaire (parent)
    List<SalaryDetail> findByParent(String parent);

    // Trouver les détails triés par idx pour un bulletin donné
    List<SalaryDetail> findByParentOrderByIdxAsc(String parent);

    // Trouver les détails filtrés par composant salarial
    List<SalaryDetail> findBySalaryComponent(String salaryComponent);

}
