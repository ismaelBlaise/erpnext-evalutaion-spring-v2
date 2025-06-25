package com.evaluation.erpnext_spring;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.evaluation.erpnext_spring.model.SalarySlip;
import com.evaluation.erpnext_spring.repository.SalarySlipRepository;


@SpringBootTest
class ErpnextSpringApplicationTests {
	@Autowired
	SalarySlipRepository salarySlipRepository;
	@Test
	void contextLoads() {
		List<SalarySlip> salarySlips=salarySlipRepository.findAll();
		for (SalarySlip salarySlip : salarySlips) {
			System.out.println(salarySlip.getName());
		}
	}

}
