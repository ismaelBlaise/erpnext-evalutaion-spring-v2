package com.evaluation.erpnext_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.evaluation.erpnext_spring.dto.data.DataDto;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDetail;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipFilter;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipListResponse;
import com.evaluation.erpnext_spring.dto.salaries.SalaryTotalsResponse;
import com.evaluation.erpnext_spring.service.DataService;
import com.evaluation.erpnext_spring.service.PdfService;
import com.evaluation.erpnext_spring.service.SalarySlipService;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payrolls")
public class SalarySlipController {

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private PdfService pdfGeneratorService;

    @Autowired
    private DataService dataService;

    @GetMapping("/summary")
    public ModelAndView summarySlip(HttpSession session,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(required = false) String month) {
        ModelAndView modelAndView = new ModelAndView("template");
        SalarySlipListResponse response = null;

        try {
            modelAndView.addObject("page", "payrolls/summary");

            SalarySlipFilter filter = new SalarySlipFilter();
            
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
                response =salarySlipService.getRapport(session, response);
            } else {
                
                response = salarySlipService.getSalarySlips(session, start, size, filter);
                response =  salarySlipService.getRapport(session, response);
            }

            List<DataDto> salaryComponents=dataService.getAllData(session,"Salary Component").getData();

            List<SalarySlipDto> salarySlips = salarySlipService.getComponents(response.getData(), salaryComponents);
            
            
            
            modelAndView.addObject("salaryComponents", salaryComponents);
            modelAndView.addObject("salarySlips", salarySlips);
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            modelAndView.addObject("totalSalarySlip", new SalaryTotalsResponse(salarySlips,salaryComponents));

           
            modelAndView.addObject("filter", filter);
            modelAndView.addObject("month",month);

        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
           
        }

        return modelAndView;
    }

    @GetMapping("/stat")
    public ModelAndView groupedSalarySummary(HttpSession session,
                                        @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") String year) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "payrolls/stat");
        modelAndView.addObject("selectedYear", year);

        try {
            List<DataDto> salaryComponents = dataService.getAllData(session, "Salary Component").getData();
            Map<String, SalarySlipDto> groupedSalarySlips = salarySlipService.getSalarySlipsGroupedByMonth(session, year, salaryComponents);
            List<SalarySlipDto> salarySlipDtos=new ArrayList<>();
            for (Map.Entry<String, SalarySlipDto> entry : groupedSalarySlips.entrySet()) {
               
                SalarySlipDto salarySlipDto = entry.getValue(); // Les données pour ce mois
                
                salarySlipDtos.add(salarySlipDto);
               
                
            }
            modelAndView.addObject("salaryComponents", salaryComponents);
            modelAndView.addObject("groupedSalarySlips", groupedSalarySlips);
            modelAndView.addObject("totalSalarySlip", new SalaryTotalsResponse(salarySlipDtos,salaryComponents));

            
            if (groupedSalarySlips.isEmpty()) {
                modelAndView.addObject("info", "Aucune donnée disponible pour l'année " + year);
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des données: " + e.getMessage());
        }

        return modelAndView;
    }


    @GetMapping("/graph")
    public ModelAndView groupedSalarySummaryGraph(HttpSession session,
                                        @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") String year) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "payrolls/graph");
        modelAndView.addObject("selectedYear", year);

        try {
            List<DataDto> salaryComponents = dataService.getAllData(session, "Salary Component").getData();
            Map<String, SalarySlipDto> groupedSalarySlips = salarySlipService.getSalarySlipsGroupedByMonth(session, year, salaryComponents);
            List<SalarySlipDto> salarySlipDtos=new ArrayList<>();
            for (Map.Entry<String, SalarySlipDto> entry : groupedSalarySlips.entrySet()) {
                
                SalarySlipDto salarySlipDto = entry.getValue(); 
                
                salarySlipDtos.add(salarySlipDto);
               
                
            }
            
            modelAndView.addObject("salaryComponents", salaryComponents);
            modelAndView.addObject("groupedSalarySlips", salarySlipDtos);

           
            
            if (groupedSalarySlips.isEmpty()) {
                modelAndView.addObject("info", "Aucune donnée disponible pour l'année " + year);
            }
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des données: " + e.getMessage());
        }

        return modelAndView;
    }


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
           
        }

        return modelAndView;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportFichePaiePdf(
             @RequestParam("id") String id, HttpSession session) {

        String sessionCookie = (String) session.getAttribute("sid");
        if (sessionCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SalarySlipDetail response = null;
        response=salarySlipService.getSalarySlipByName(session, id);
        SalarySlipDto salarySlipDto=response.getData();

        Map<String, Object> data = new HashMap<>();
        data.put("salary",salarySlipDto );

        byte[] pdfBytes = pdfGeneratorService.generatePdfFromThymeleaf("export/salary", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Fiche_Paie_" + salarySlipDto.getEmployeeName() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }



}
