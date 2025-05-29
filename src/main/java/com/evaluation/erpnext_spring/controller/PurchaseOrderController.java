package com.evaluation.erpnext_spring.controller;

import com.evaluation.erpnext_spring.dto.purchase_orders.PcoListResponse;
import com.evaluation.erpnext_spring.enums.PurchaseOrderStatus;
import com.evaluation.erpnext_spring.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ModelAndView getPurchaseOrders(HttpSession session,
                                          @RequestParam(required = false) String status,
                                          @RequestParam String supplierId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "orders/list");
        // PurchaseOrderListResponse response=null;
        PcoListResponse pcoListResponse=null;
        try {
            // if(status == null || status.trim().isEmpty()){
                
            //     // response = purchaseOrderService.getPurchaseOrdersBySupplier(session, supplierId, page, size);
            //     pcoListResponse=purchaseOrderService.getOrdersByStatus(session, null, supplierId, page, size);
                
            // }
            // else{
            //     // response= purchaseOrderService.getPurchaseOrdersBySupplierAndStatus(session,supplierId,status);
            //     pcoListResponse=purchaseOrderService.getOrdersByStatus(session, status, supplierId, page, size);
            // }

            // modelAndView.addObject("purchaseOrders", response.getData());
            
            
            pcoListResponse=purchaseOrderService.getOrdersByStatus(session, status, supplierId);
            // System.out.println(pcoListResponse.getData().size());
            modelAndView.addObject("purchaseOrders", pcoListResponse.getData());

            modelAndView.addObject("purchaseStatus", PurchaseOrderStatus.values());
            modelAndView.addObject("currentPage", page);
            modelAndView.addObject("pageSize", size);
            modelAndView.addObject("supplier", supplierId);

        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des commandes d'achat : " + e.getMessage());
            modelAndView.addObject("page", "error");
        }

        return modelAndView;
    }
}
