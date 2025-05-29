package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.quotations.SupplierQuotationListResponse;
import com.evaluation.erpnext_spring.dto.supplier_quotations.SpqListResponse;
import com.evaluation.erpnext_spring.service.SupplierQuotationService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/quotations")
public class SupplierQuotationController {

    @Autowired
    private SupplierQuotationService supplierQuotationService;

    
    @GetMapping
    public ModelAndView getSupplierQuotations(HttpSession session, 
                                              @RequestParam String supplierId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "quotations/list");

        try {
            SupplierQuotationListResponse response = supplierQuotationService.getQuotationsBySupplier(session, supplierId, page, size);

            modelAndView.addObject("quotations", response.getData());
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            modelAndView.addObject("supplier", supplierId);
           
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des devis : " + e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }


    @GetMapping("/v2")
    public ModelAndView getSupplierQuotationsV2(HttpSession session,
                                                @RequestParam String requestForQuotationName,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "5") int size) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "spq/list");

        try {
            SpqListResponse response = supplierQuotationService.getSupplierQuotationByRqf(session, requestForQuotationName, page, size);
            System.out.println();
            System.out.println(response.getData());
            System.out.println();
            modelAndView.addObject("requests", response.getData());
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            modelAndView.addObject("request", requestForQuotationName);
            modelAndView.addObject("pagination", response.getPagination());

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des demandes de devis : " + e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }
    

    @PostMapping("/submit")
    public ModelAndView validateSupplierQuotation(
            HttpSession session,
            @RequestParam String quotationName,
            @RequestParam(required = false) String redirectUrl,
            RedirectAttributes redirectAttributes) {
        
        ModelAndView modelAndView = new ModelAndView();
        
        try {
            Map<String, Object> validationResult = supplierQuotationService.validateSupplierQuotation(session, quotationName);
            
            redirectAttributes.addFlashAttribute("success", "Devis validé avec succès");
            redirectAttributes.addFlashAttribute("quotationStatus", validationResult.get("status"));
            
            modelAndView.setViewName("redirect:/quotation-items?parentId=" + quotationName);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la validation");
            modelAndView.setViewName("redirect:/quotation-items?parentId=" + quotationName);
        }
        
        return modelAndView;
    }
}
