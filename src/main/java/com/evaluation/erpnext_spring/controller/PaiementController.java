package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.data.DataDto;
import com.evaluation.erpnext_spring.dto.employees.EmployeeDto;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.evaluation.erpnext_spring.service.DataService;
import com.evaluation.erpnext_spring.service.EmployeeService;
import com.evaluation.erpnext_spring.service.PaiementService;
import com.evaluation.erpnext_spring.service.SalarySlipService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/salaire")
public class PaiementController {

    

 
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DataService dataService;

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private PaiementService paiementService;

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
            paiementService.genererSalaires(session, employee, startDate, endDate, base);
            
            modelAndView.addObject("success", "Salaires générés avec succès !");
        } catch (Exception e) {
            modelAndView.addObject("error", "Erreur lors de la génération : " + e.getMessage());
        }

        return modelAndView;
    }


  

    @GetMapping("/modifier-composants")
    public ModelAndView afficherFormulaireModification(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("template");
        try {
            modelAndView.addObject("page", "salaires/modification");
            
            // Récupérer les composants salariaux
            List<DataDto> components = dataService.getAllData(session, "Salary Component", null).getData();
            modelAndView.addObject("components", components);
            
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }

    @PostMapping("/traiter-modification")
    public ModelAndView traiterModification(HttpSession session,
                                          @RequestParam("selectedComponent") String componentName,
                                          @RequestParam("comparaisonType") String comparaisonType,
                                          @RequestParam("thresholdValue") Double thresholdValue,
                                          @RequestParam("modificationType") String modificationType,
                                          @RequestParam("percentageValue") Double percentageValue) {
        
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "salaires/modification");
        
        try {
           
            List<DataDto> components = dataService.getAllData(session, "Salary Component", null).getData();
            modelAndView.addObject("components", components);
            
            
            boolean isGreaterThan = "superieur".equals(comparaisonType);
            
            List<SalarySlipDto> filteredSlips = salarySlipService.getSalarySlipsByComponentThreshold(
                session, componentName, thresholdValue, isGreaterThan);
            
           
            boolean isIncrease = "augmentation".equals(modificationType);
            List<SalarySlipDto> updatedSlips = paiementService.cancelAndUpdateSalarySlips(
                session, 
                filteredSlips.stream()
                    .map(slip -> {
                        SalarySlipDto dto = new SalarySlipDto();
                        dto.setName(slip.getName());
                        return dto;
                    })
                    .collect(Collectors.toList()),
                componentName, 
                percentageValue, 
                isIncrease);
            
            
            modelAndView.addObject("selectedComponent", componentName);
            modelAndView.addObject("comparaisonType", comparaisonType);
            modelAndView.addObject("thresholdValue", thresholdValue);
            modelAndView.addObject("modificationType", modificationType);
            modelAndView.addObject("percentageValue", percentageValue);
            
            modelAndView.addObject("success", "Modification appliquée avec succès à " + updatedSlips.size() + " fiches de paie");
            
            List<String> affectedEmployees = updatedSlips.stream()
                .map(SalarySlipDto::getEmployeeName)
                .distinct()
                .collect(Collectors.toList());
            
            modelAndView.addObject("affectedEmployees", affectedEmployees);
            
        } catch (Exception e) {
            modelAndView.addObject("error", "Erreur lors de la modification : " + e.getMessage());
        }
        
        return modelAndView;
    }

}
