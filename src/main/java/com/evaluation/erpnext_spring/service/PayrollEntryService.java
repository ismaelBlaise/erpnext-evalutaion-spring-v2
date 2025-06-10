package com.evaluation.erpnext_spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.payrolls.PayrollEmployeeDetail;
import com.evaluation.erpnext_spring.dto.payrolls.PayrollEntryDTO;

import jakarta.servlet.http.HttpSession;


@Service
public class PayrollEntryService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;

     

    public void createPayrollEntry(HttpSession session, PayrollEntryDTO request) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }
        
        String url = erpnextApiUrl + "/api/resource/Payroll Entry";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", "sid=" + sid);
        
        HttpEntity<PayrollEntryDTO> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de création de la Payroll Entry");
        }
    }

    public void createPayrollEntryFromDetails(HttpSession session, 
                                            String company, 
                                            String posting_date,
                                            String currency,
                                            String costCenter,
                                            String payroll_frequency,
                                            String start_date,
                                            String end_date,
                                            boolean draft,
                                            List<PayrollEmployeeDetail> employeeDetails) {
        for (PayrollEmployeeDetail payrollEmployeeDetail : employeeDetails) {
            if(payrollEmployeeDetail.getIs_salary_withheld()=="true"){
                payrollEmployeeDetail.setIs_salary_withheld("Yes");
            }else{
                payrollEmployeeDetail.setIs_salary_withheld("No");
            }
        }                                        
        
        PayrollEntryDTO payrollEntry = new PayrollEntryDTO();
        payrollEntry.setCompany(company);
        payrollEntry.setPosting_date(posting_date);
        payrollEntry.setCurrency(currency);
        payrollEntry.setPayroll_frequency(payroll_frequency);
        payrollEntry.setStart_date(start_date);
        payrollEntry.setEnd_date(end_date);
        payrollEntry.setDraft(draft);
        payrollEntry.setEmployees(employeeDetails);
        
        createPayrollEntry(session, payrollEntry);
    }
}
