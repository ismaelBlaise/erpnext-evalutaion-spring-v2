package com.evaluation.erpnext_spring.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evaluation.erpnext_spring.dto.company.CompanyListResponseDTO;
import com.evaluation.erpnext_spring.dto.invoices.PurchaseInvoice;
import com.evaluation.erpnext_spring.dto.payments.PaymentDTO;
import com.evaluation.erpnext_spring.dto.payments.PaymentResponseDTO;
import com.evaluation.erpnext_spring.dto.payments.PaymentResponseGroupDTO;
import com.evaluation.erpnext_spring.service.CompanyService;
import com.evaluation.erpnext_spring.service.PaymentService;
import com.evaluation.erpnext_spring.service.PurchaseInvoiceService;

import jakarta.servlet.http.HttpSession;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PurchaseInvoiceService purchaseInvoiceService;

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public ModelAndView paymentForm(@RequestParam String facture, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("template");
        
        try {
            PurchaseInvoice invoice = purchaseInvoiceService.getPurchaseInvoiceByName(session, facture);
            
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setInvoiceName(invoice.getName());
            paymentDTO.setCompany(invoice.getCompany());
            paymentDTO.setPostingDate(invoice.getPostingDate());
            paymentDTO.setPaidAmount(invoice.getOutstandingAmount());
            paymentDTO.setAllocatedAmount(invoice.getOutstandingAmount());
            paymentDTO.setParty(invoice.getSupplier());  

            CompanyListResponseDTO companyListResponseDTO=companyService.getCompanies(session);
            
            modelAndView.addObject("page", "invoices/payment");
            modelAndView.addObject("invoice", invoice);
            modelAndView.addObject("companies", companyListResponseDTO.getData());
            modelAndView.addObject("date",invoice.getPostingDate());
            modelAndView.addObject("paymentDTO", paymentDTO); 
            
        } catch (Exception e) {
            modelAndView.addObject("error", "Erreur lors de la récupération de la facture: " + e.getMessage());
            modelAndView.addObject("page","error");
        }
        
        return modelAndView;
    }

    @PostMapping("/process")
    public String processPayment(
            @ModelAttribute PaymentDTO paymentDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        try {
            if(paymentDTO.getReferenceNo().isBlank() || paymentDTO.getReferenceDate().isBlank()){
                throw new IllegalArgumentException("Le N° de Référence et la Date de Référence sont nécessaires pour une Transaction Bancaire");
            }
            if (paymentDTO.getPaidAmount() == null || paymentDTO.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le montant payé doit être supérieur à zéro");
            }
            
            paymentDTO.setReceivedAmount(paymentDTO.getPaidAmount());
            paymentDTO.setSourceExchangeRate(BigDecimal.valueOf(1.0));
            paymentDTO.setAllocatedAmount(paymentDTO.getPaidAmount());
            paymentDTO.setDifferenceAmount(BigDecimal.ZERO);
            PaymentDTO.afficherPaymentDTO(paymentDTO);
            if(paymentDTO.getReferences().size()<=0){
                throw new IllegalArgumentException("Le montant reference doit être definis");
            }
            paymentDTO.getReferences().get(0).setAllocatedAmount(paymentDTO.getPaidAmount());
            PaymentResponseGroupDTO paymentResult = paymentService.processPayment(paymentDTO);
            
            redirectAttributes.addFlashAttribute("paymentName", paymentResult.getData().getName());
            redirectAttributes.addFlashAttribute("paymentDTO", paymentDTO);
            return "redirect:/payments/submit?invoice=" + paymentDTO.getInvoiceName();
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/payments?facture=" + paymentDTO.getInvoiceName();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du traitement du paiement: " + e.getMessage());
            return "redirect:/payments?facture=" + paymentDTO.getInvoiceName();
        }
    }

    @GetMapping("/submit")
    public ModelAndView submitPaymentForm(
            @RequestParam String invoice,
            @ModelAttribute("paymentDTO") PaymentDTO paymentDTO,
            HttpSession session) {
        
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "invoices/payment_submit");
        modelAndView.addObject("invoice", invoice);
        modelAndView.addObject("paymentDTO", paymentDTO);
        session.setAttribute("paymentDTO", paymentDTO);
        return modelAndView;
    }

    @PostMapping("/submit")
    public String submitPayment(
            @RequestParam String paymentName,
            RedirectAttributes redirectAttributes,
            HttpSession session
            ) {
        
        try {
            String paymentSubmitResponse = paymentService.submitPaymentEntry(paymentName);
            redirectAttributes.addFlashAttribute("success", "Paiement validé avec succès");
            return "redirect:/payments/success?invoice=" + paymentName;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la soumission du paiement: " + e.getMessage());
            return "redirect:/payments/submit?invoice=" + paymentName;
        }
    }

    @GetMapping("/success")
    public ModelAndView paymentSuccess(@RequestParam String invoice) {
        ModelAndView modelAndView = new ModelAndView("template");
        modelAndView.addObject("page", "invoices/payment_success");
        modelAndView.addObject("invoiceNumber", invoice);
        return modelAndView;
    }
}