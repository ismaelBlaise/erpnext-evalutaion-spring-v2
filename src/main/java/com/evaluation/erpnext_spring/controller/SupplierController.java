package com.evaluation.erpnext_spring.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.evaluation.erpnext_spring.dto.suppliers.SupplierDto;
import com.evaluation.erpnext_spring.dto.suppliers.SupplierListResponse;
import com.evaluation.erpnext_spring.service.SupplierService;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {
 
    @Autowired
    private  SupplierService supplierService;
    

    @GetMapping
    public ModelAndView selectSupplier(HttpSession session,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,@RequestParam(required = false) String search) {
        ModelAndView modelAndView = new ModelAndView("template");
        SupplierListResponse response =null;
        try {
            modelAndView.addObject("page", "suppliers/list");

            

            if (search == null || search.trim().isEmpty()) {
                int start = page * size;
                response= supplierService.getAllSuppliers(session, start, size);
            
            }else {
                response=supplierService.getSupplierByName(session, search);
            }
            List<SupplierDto> suppliers = response.getData();

            modelAndView.addObject("suppliers", suppliers);
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            

        } catch (Exception e) {
            e.getMessage();
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("page", "error");
        }
        return modelAndView;
    }



    
}