package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.quotations.SupplierQuotationGroupDto;
import com.evaluation.erpnext_spring.dto.quotations.SupplierQuotationItemDto;
import com.evaluation.erpnext_spring.dto.quotations.SupplierQuotationItemListResponse;
import com.evaluation.erpnext_spring.dto.quotations.UpdateItemRateResponseDTO;
import com.evaluation.erpnext_spring.service.SupplierQuotationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/quotation-items")
public class SupplierQuotationItemController {

    @Autowired
    private SupplierQuotationItemService supplierQuotationService;

    @GetMapping
    public ModelAndView getSupplierQuotationItems(HttpSession session,
                                                @RequestParam String parentId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size,
                                                @RequestParam(required = false) String success,
                                                @RequestParam(required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "quotations/items");

        try {
            SupplierQuotationItemListResponse response = supplierQuotationService.getSupplierQuotation(session, parentId);

            SupplierQuotationGroupDto quotationGroup = response.getData();

            List<SupplierQuotationItemDto> items = quotationGroup != null ? quotationGroup.getItems() : null;

            modelAndView.addObject("quotationItems", items);
            modelAndView.addObject("quotationInfo", quotationGroup);
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            modelAndView.addObject("parentId", parentId);

            if (success != null) {
                modelAndView.addObject("success", success);
            }
            if (error != null) {
                modelAndView.addObject("error", error);
            }

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des items du devis : " + e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }


    @PostMapping
    public String updateSupplierQuotationItemRate(HttpSession session,
                                                @RequestParam("itemName") String itemName,
                                                @RequestParam("newRate") double newRate,
                                                @RequestParam("parentId") String parentId,
                                                ModelAndView modelAndView) {
        try {
            UpdateItemRateResponseDTO response = supplierQuotationService.updateSupplierQuotationItemRate(session, itemName, newRate);

            if ("success".equalsIgnoreCase(response.getStatus())) {
                
                return "redirect:/quotation-items?parentId=" + parentId + "&success=" + response.getMessage();
            } else {
                
                return "redirect:/quotation-items?parentId=" + parentId + "&error=" + response.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/quotation-items?parentId=" + parentId + "&error=Erreur interne: " + e.getMessage();
        }
    }


}
