package com.evaluation.erpnext_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evaluation.erpnext_spring.model.SalarySlip;

@Repository
public interface SalarySlipRepository extends JpaRepository<SalarySlip,String> {
    
}
