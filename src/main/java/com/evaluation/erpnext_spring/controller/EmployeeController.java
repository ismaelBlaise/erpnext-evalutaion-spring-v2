package com.evaluation.erpnext_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.evaluation.erpnext_spring.dto.data.DataDto;
import com.evaluation.erpnext_spring.dto.data.DataListReponse;
import com.evaluation.erpnext_spring.dto.employees.EmployeeDto;
import com.evaluation.erpnext_spring.dto.employees.EmployeeFilter;
import com.evaluation.erpnext_spring.dto.employees.EmployeeListResponse;
import com.evaluation.erpnext_spring.service.data.DataService;
import com.evaluation.erpnext_spring.service.employee.EmployeeService;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DataService designationService;
    
    @GetMapping
    public ModelAndView listEmployees(HttpSession session,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    EmployeeFilter filter) {
        ModelAndView modelAndView = new ModelAndView("template");
        EmployeeListResponse response = null;
        DataListReponse dataListReponse=null;
        
        try {
            modelAndView.addObject("page", "employees/list");

            
            int start = page * size;
            response =  employeeService.getAllEmployees(session, start, size, filter);
           

            dataListReponse=designationService.getAllData(session,"Designation", null);
            List<DataDto> designations=dataListReponse.getData();
            dataListReponse=designationService.getAllData(session,"Department", null);
            List<DataDto> departments=dataListReponse.getData();
            dataListReponse=designationService.getAllData(session,"Company", null);
            List<DataDto> companies=dataListReponse.getData();
            
            List<EmployeeDto> employees = response.getData();

           
            
            modelAndView.addObject("designations",designations );
            modelAndView.addObject("departments",departments );
            modelAndView.addObject("companies",companies );

            modelAndView.addObject("employees", employees);
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            modelAndView.addObject("filter", filter);
            
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
            
        }
        
        return modelAndView;
    }


    @GetMapping("/view")
    public ModelAndView getEmployeeDetails(HttpSession session, @RequestParam("name") String employeeName) {
        ModelAndView modelAndView = new ModelAndView("template");

        try {
            modelAndView.addObject("page", "employees/view");

            
            EmployeeDto employee = employeeService.getEmployeeByName(session, employeeName).getData();

            
            modelAndView.addObject("employee", employee);

        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
            
        }

        return modelAndView;
    }

}