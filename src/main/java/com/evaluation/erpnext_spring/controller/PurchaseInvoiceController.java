package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.invoices.PurchaseInvoiceListResponse;
import com.evaluation.erpnext_spring.enums.PurchaseInvoiceStatus;
import com.evaluation.erpnext_spring.service.PurchaseInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/invoices")
public class PurchaseInvoiceController {

    @Autowired
    private PurchaseInvoiceService purchaseInvoiceService;

    @GetMapping
    public ModelAndView getPurchaseInvoices(HttpSession session,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "invoices/list");
        PurchaseInvoiceListResponse response=null;
        try {
            if(status == null || status.trim().isEmpty()){
                response = purchaseInvoiceService.getPurchaseInvoices(session, page, size);
            }
            else {
                response=purchaseInvoiceService.getPurchaseInvoicesByStatus(session, status);
            }
            
            modelAndView.addObject("purchaseInvoices", response.getData());
            modelAndView.addObject("purchaseInvoiceStatus", PurchaseInvoiceStatus.values());
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);

        } catch (Exception e) {
            
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des factures d'achat : " + e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }
}
