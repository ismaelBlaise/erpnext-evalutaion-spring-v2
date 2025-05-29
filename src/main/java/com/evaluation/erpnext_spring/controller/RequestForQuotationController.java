package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.requests_for_quotation.RfqListResponse;
import com.evaluation.erpnext_spring.service.RequestForQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/requests-for-quotation")
public class RequestForQuotationController {

    @Autowired
    private RequestForQuotationService requestForQuotationService;

    @GetMapping
    public ModelAndView getRequestsForQuotation(HttpSession session,
                                                @RequestParam String supplierName,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "5") int size) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "rfq/list");

        try {
            RfqListResponse response = requestForQuotationService.getRequestsForQuotationBySupplier(session, supplierName, page, size);
            System.out.println();
            System.out.println(response.getSuccessMessage());
            System.out.println();
            modelAndView.addObject("requests", response.getData());
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            modelAndView.addObject("supplier", supplierName);
            // modelAndView.addObject("pagination", response.getPagination());

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des demandes de devis : " + e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }
}
