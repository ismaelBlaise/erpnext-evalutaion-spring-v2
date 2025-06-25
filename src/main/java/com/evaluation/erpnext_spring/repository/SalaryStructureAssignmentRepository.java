package com.evaluation.erpnext_spring.repository;

import com.evaluation.erpnext_spring.model.SalaryStructureAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SalaryStructureAssignmentRepository extends JpaRepository<SalaryStructureAssignment, String> {

    // Trouver toutes les assignations d'un employé
    List<SalaryStructureAssignment> findByEmployee(String employee);

    // Trouver les assignations d'un employé actives
    List<SalaryStructureAssignment> findByEmployeeAndIsActive(String employee, Integer isActive);

    // Trouver les assignations valides à une date donnée (entre fromDate et toDate)
    @Query("SELECT s FROM SalaryStructureAssignment s WHERE s.employee = :employee AND :date BETWEEN s.fromDate AND s.toDate")
    List<SalaryStructureAssignment> findValidAssignmentByEmployeeAndDate(@Param("employee") String employee, @Param("date") LocalDate date);

    // Trouver les assignations pour une entreprise donnée
    List<SalaryStructureAssignment> findByCompany(String company);

    // Trouver les assignations par fréquence de paie
    List<SalaryStructureAssignment> findByPayrollFrequency(String payrollFrequency);

    // Trouver les assignations actives
    List<SalaryStructureAssignment> findByIsActive(Integer isActive);

}
