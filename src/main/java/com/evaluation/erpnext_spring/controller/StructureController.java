package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.structures.StructureDetail;
import com.evaluation.erpnext_spring.service.DataService;
import com.evaluation.erpnext_spring.service.EmployeeService;
import com.evaluation.erpnext_spring.service.StructureService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/structures")
public class StructureController {

    @Autowired
    private StructureService structureService;

    @Autowired
    private DataService dataService;
    
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/assignment")
    public ModelAndView showAssignmentForm(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "structures/assignment");
        
        try {
            modelAndView.addObject("structureDetails", new StructureDetail());
            modelAndView.addObject("companies", dataService.getAllData(session, "Company", null).getData());
            modelAndView.addObject("structures", dataService.getAllData(session, "Salary Structure", null).getData());
            modelAndView.addObject("employees", employeeService.getAllEmployees(session, 0, 0, null).getData());
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        
        return modelAndView;
    }

    @PostMapping("/assign")
    @ResponseBody
    public String assignSalaryStructures(
            HttpSession session,
            @RequestParam("salaryStructure") String salaryStructure,
            @RequestParam("company") String company,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("currency") String currency,
            @RequestParam("structureDetails") String structureDetailsJson) {
        
        try {
            // Convertir le JSON en liste d'objets
            ObjectMapper mapper = new ObjectMapper();
            List<StructureDetail> structureDetails = mapper.readValue(
                structureDetailsJson, 
                new TypeReference<List<StructureDetail>>() {}
            );
            
            structureService.assignToSalaryStructures(session, salaryStructure, company, 
                                                    fromDate, currency, structureDetails);
            return "{\"status\":\"success\", \"message\":\"Assignation réussie\"}";
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }
}