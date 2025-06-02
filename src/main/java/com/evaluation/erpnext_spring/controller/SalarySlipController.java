package com.evaluation.erpnext_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDetail;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipFilter;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipListResponse;
import com.evaluation.erpnext_spring.service.ExportPdfService;
import com.evaluation.erpnext_spring.service.SalarySlipService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/payrolls")
public class SalarySlipController {

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private ExportPdfService export;

    @GetMapping
    public ModelAndView listSalarySlips(HttpSession session,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam String employee,
                                       @RequestParam(required = false) String month) {
        ModelAndView modelAndView = new ModelAndView("template");
        SalarySlipListResponse response = null;

        try {
            modelAndView.addObject("page", "payrolls/list");

            SalarySlipFilter filter = new SalarySlipFilter();
            filter.setEmployee(employee);
             String startDate = null;
            String endDate = null;

            if (month != null && !month.isEmpty()) {
                java.time.YearMonth ym = java.time.YearMonth.parse(month);
                startDate = ym.atDay(1).toString();        
                endDate = ym.atEndOfMonth().toString();    
            }

            filter.setStartDate(startDate);
            filter.setEndDate(endDate);

            int start = page * size;

            
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                response = salarySlipService.getSalarySlips(session, 0, 0, filter); 
            } else {
                
                response = salarySlipService.getSalarySlips(session, start, size, filter);
            }

            List<SalarySlipDto> salarySlips = response.getData();

            modelAndView.addObject("salarySlips", salarySlips);
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);

           
            modelAndView.addObject("filter", filter);

        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }

    @GetMapping("/view")
    public ModelAndView ficheDePaie(@RequestParam("id") String id, HttpSession session){
         ModelAndView modelAndView = new ModelAndView("template");
        SalarySlipDetail response = null;

        try {
            modelAndView.addObject("page", "payrolls/view");

            response=salarySlipService.getSalarySlipByName(session, id);
            SalarySlipDto salarySlipDto=response.getData();
            modelAndView.addObject("salary", salarySlipDto);
            
           

        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }

    @GetMapping("/export")
    public void exportFicheDePaie(@RequestParam("id") String id, HttpSession session, HttpServletResponse response) {
        try {
            SalarySlipDto salaire = salarySlipService.getSalarySlipByName(session, id).getData();
            response.setContentType("application/pdf");

            // Changer attachment en inline pour afficher dans le navigateur
            response.setHeader("Content-Disposition", "inline; filename=fiche_de_paie_" + id + ".pdf");

            export.exporterFicheDePaiePDF(salaire, response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }



}
