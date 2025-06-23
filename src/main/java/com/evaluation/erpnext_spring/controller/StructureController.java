package com.evaluation.erpnext_spring.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evaluation.erpnext_spring.dto.grilles.SalaryStructureDto;
import com.evaluation.erpnext_spring.service.data.DataService;
import com.evaluation.erpnext_spring.service.salary.StructureService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/grids")
public class StructureController {

    @Autowired
    private StructureService salaryGridService;

    @Autowired
    private DataService dataService;

    @GetMapping
    public ModelAndView showSalaryGridForm(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("template");

        try {
            modelAndView.addObject("page", "grids/form");
            modelAndView.addObject("salaryGridDTO", new SalaryStructureDto());
            modelAndView.addObject("companies", dataService.getAllData(session, "Company", null));
            modelAndView.addObject("earnings", dataService.getAllData(session, "Salary Component", "earning"));
            modelAndView.addObject("deductions", dataService.getAllData(session, "Salary Component", "deduction"));
        } catch (Exception e) {
            modelAndView.addObject("error", "Erreur : " + e.getMessage());
        }

        return modelAndView;
    }

    @PostMapping
    public ModelAndView createSalaryGrid(HttpSession session,
                                         @ModelAttribute SalaryStructureDto salaryGridDTO,
                                         RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<Map<String, Object>> response = salaryGridService.createSalaryGrid(session, salaryGridDTO);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                salaryGridService.submitSalaryGrid(session, salaryGridDTO.getName());
                redirectAttributes.addFlashAttribute("success", "Grille de salaire créée avec succès.");
            } else {
                String error = response.getBody() != null ? (String) response.getBody().get("error") : "Erreur inconnue";
                redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + error);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return new ModelAndView("redirect:/grids");
    }
}
