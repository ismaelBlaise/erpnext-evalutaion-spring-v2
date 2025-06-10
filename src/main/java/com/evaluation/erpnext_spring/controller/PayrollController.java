package com.evaluation.erpnext_spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.evaluation.erpnext_spring.dto.payrolls.PayrollEmployeeDetail;
import com.evaluation.erpnext_spring.service.DataService;
import com.evaluation.erpnext_spring.service.EmployeeService;
import com.evaluation.erpnext_spring.service.PayrollEntryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/payrolls")
public class PayrollController {

    @Autowired
    private PayrollEntryService structureService;

    @Autowired
    private DataService dataService;
    
    @Autowired
    private EmployeeService employeeService;
    @GetMapping("/payroll-entry")
    public ModelAndView showPayrollEntryForm(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "structures/payroll-entry");
        
        try {
            modelAndView.addObject("companies", dataService.getAllData(session, "Company", null).getData());
            modelAndView.addObject("employees", employeeService.getAllEmployees(session, 0, 0, null).getData());
            modelAndView.addObject("payrollFrequencies", List.of("Monthly", "Weekly", "Fortnightly", "Daily"));
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        
        return modelAndView;
    }

    @PostMapping("/create-payroll-entry")
    @ResponseBody
    public String createPayrollEntry(
            HttpSession session,
            @RequestParam("company") String company,
            @RequestParam("posting_date") String posting_date,
            @RequestParam("currency") String currency,
            @RequestParam("cost_center") String costCenter,
            @RequestParam("payroll_frequency") String payroll_frequency,
            @RequestParam("start_date") String start_date,
            @RequestParam("end_date") String end_date,
            @RequestParam(value = "draft", defaultValue = "false") boolean draft,
            @RequestParam("employeeDetails") String employeeDetailsJson) {
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<PayrollEmployeeDetail> employeeDetails = mapper.readValue(
                employeeDetailsJson, 
                new TypeReference<List<PayrollEmployeeDetail>>() {}
            );
            
            structureService.createPayrollEntryFromDetails(
                session, company, posting_date, currency, costCenter,
                payroll_frequency, start_date, end_date, 
                draft, employeeDetails
            );
            
            return "{\"status\":\"success\", \"message\":\"Payroll Entry créée avec succès\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }
}
