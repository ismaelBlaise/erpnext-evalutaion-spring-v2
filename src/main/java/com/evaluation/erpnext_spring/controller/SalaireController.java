package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.employees.EmployeeDto;
import com.evaluation.erpnext_spring.service.EmployeeService;
import com.evaluation.erpnext_spring.service.GenerationPaiementService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/salaire")
public class SalaireController {

    @Autowired
    private GenerationPaiementService generationPaiementService;

 
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/generation")
    public ModelAndView afficherFormulaireGeneration(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("template");
        try {
            modelAndView.addObject("page", "salaires/generation");

            
            List<EmployeeDto> employees = employeeService.getAllEmployees(session, 0, 0, null).getData();

            modelAndView.addObject("employees", employees);
            
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }

    @PostMapping("/generer")
    public ModelAndView genererSalaires(HttpSession session,
                                        @RequestParam("employee") String employee,
                                        @RequestParam("startDate") String startDate,
                                        @RequestParam("endDate") String endDate,
                                        @RequestParam("base") Double base) {
        ModelAndView modelAndView = new ModelAndView("template");
        try {
            modelAndView.addObject("page", "salaires/generation");
            List<EmployeeDto> employees = employeeService.getAllEmployees(session, 0, 0, null).getData();

            modelAndView.addObject("employees", employees);
            generationPaiementService.genererSalaires(session, employee, startDate, endDate, base);
            
            modelAndView.addObject("success", "Salaires générés avec succès !");
        } catch (Exception e) {
            modelAndView.addObject("error", "Erreur lors de la génération : " + e.getMessage());
        }

        return modelAndView;
    }
}
