package com.evaluation.erpnext_spring.service.salary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.evaluation.erpnext_spring.dto.grilles.SalaryStructureDto;
import com.evaluation.erpnext_spring.dto.imports.SalaireData;
import com.evaluation.erpnext_spring.dto.salaries.SalaryDeduction;
import com.evaluation.erpnext_spring.dto.salaries.SalaryEarning;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDetail;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.evaluation.erpnext_spring.dto.salaries.SalarySlipListResponse;
import com.evaluation.erpnext_spring.dto.structures.StructureAssignement;
import com.evaluation.erpnext_spring.service.imports.SalaireImportService;

import jakarta.servlet.http.HttpSession;

@Service
public class PaiementService {
    @SuppressWarnings("unused")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    @Value("${erpnext.api.key}")
    private String erpnextApiKey;

    @Value("${erpnext.api.secret}")
    private String erpnextApiSecret;
    
    @Autowired
    private StructureAssignmentService structureService;

    @Autowired
    private SalaireImportService salaireImportService;


    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private StructureService structureServiceGrid;

    public SalaireData genererSalaireData(StructureAssignement lastAssignement, String employeeId, String date, Double base) {
        
          
        SalaireData salaireData = new SalaireData();
        salaireData.setMois(date);
        salaireData.setRefEmploye(employeeId);
        if(base==null){
            salaireData.setSalaireBase(lastAssignement.getBase());
        }
        else{
            salaireData.setSalaireBase(base);
        }
        salaireData.setSalaryStructure(lastAssignement.getSalary_structure());

        return salaireData;
    }

    public List<SalaireData> genererSalaires(HttpSession session, String employee, String startDate, String endDate, Double base) throws Exception {
        List<SalaireData> salaireDatas = new ArrayList<>();
        startDate+="-01";
        endDate+="-01";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter).withDayOfMonth(1);
        LocalDate end = LocalDate.parse(endDate, formatter).withDayOfMonth(1);
        StructureAssignement lastAssignement = structureService.getLastStructureAssignementBeforeDate(session, employee, startDate);

        if (lastAssignement == null && base==null ) {
            throw new RuntimeException("Aucune structure de salaire trouvée pour l’employé " + employee + " avant la date " + startDate);
        }
        else if(lastAssignement==null && base!=null){
            SalaryStructureDto salaryStructureDto=structureServiceGrid.getSalaryStructures(session).getData().get(0);

            lastAssignement=new StructureAssignement();
            lastAssignement.setSalary_structure(salaryStructureDto.getName());
            lastAssignement.setBase(base);
            lastAssignement.setCompany(salaryStructureDto.getCompany());
            lastAssignement.setFrom_date(start.toString());
            lastAssignement.setEmployee(employee);


        }

        while (!start.isAfter(end)) {
            if(salarySlipService.isSalarySlipAlreadyCreatedBack(session, employee, start)==false){
                String dateMois = start.toString(); 
                SalaireData salaireData = genererSalaireData(lastAssignement, employee, dateMois, base);
               
                salaireDatas.add(salaireData);
               
            }
            start = start.plusMonths(1);
        }

        
       if(!salaireDatas.isEmpty()){
         salaireImportService.importSalaireData(session, salaireDatas);
       }
       return salaireDatas;
    }





    
    public List<SalarySlipDto> getSalarySlipsByComponentThreshold(HttpSession session, 
                                                             String componentName, 
                                                             double threshold, 
                                                             boolean isGreaterThan) {
        
        SalarySlipListResponse response = salarySlipService.getSalarySlips(session, 0, 0, null);
        List<SalarySlipDto> allSlips = response.getData();
        
        
        List<SalarySlipDto> filteredSlips = new ArrayList<>();
        
        
        for (SalarySlipDto slip : allSlips) {
            SalarySlipDetail detail = salarySlipService.getSalarySlipByName(session, slip.getName());
            SalarySlipDto fullSlip = detail.getData();
            
            
            if (fullSlip.getEarnings() != null) {
                for (SalaryEarning earning : fullSlip.getEarnings()) {
                    if (earning.getSalaryComponent().equals(componentName)) {
                        
                        if ((isGreaterThan && earning.getAmount() > threshold) ||
                            (!isGreaterThan && earning.getAmount() < threshold)) {
                           
                                filteredSlips.add(fullSlip);
                            break;  
                        }
                    }
                }
            }
            
            
            if (fullSlip.getDeductions() != null && !filteredSlips.contains(fullSlip)) {
                for (SalaryDeduction deduction : fullSlip.getDeductions()) {
                    if (deduction.getSalaryComponent().equals(componentName)) {
                        if ((isGreaterThan && deduction.getAmount() > threshold) ||
                            (!isGreaterThan && deduction.getAmount() < threshold)) {
                           
                            filteredSlips.add(fullSlip);
                            break;
                        }
                    }
                }
            }
        }
        
        return filteredSlips;
    }


    @SuppressWarnings("unused")
    public List<SalarySlipDto> cancelAndUpdateSalarySlips(HttpSession session,
                                                           List<SalarySlipDto> salarySlipDtos,
                                                           String componentName,
                                                           double percentageChange,
                                                           boolean isIncrease) throws Exception {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        List<SalarySlipDto> updatedSlips = new ArrayList<>();
        

        for (SalarySlipDto slipDto : salarySlipDtos) {
            try {
                StructureAssignement structureAssignement=structureService.getLastStructureAssignementBeforeDate(session, slipDto.getEmployee(), slipDto.getStartDate());
               
                StructureAssignement assignement=structureService.cancelOrDeleteStructureAssignment(session, structureAssignement, true);
                double amount = structureAssignement.getBase();
                amount = isIncrease
                            ? amount * (1 + percentageChange / 100.0)
                            : amount * (1 - percentageChange / 100.0);
                amount = Math.round(amount * 100.0) / 100.0;
                structureAssignement.setBase(amount);
                structureAssignement.setName(null);
                structureAssignement.setAmended_from(structureAssignement.getName());

                // structureService.assignSalaryStructure(session, assignement);
               
                SalarySlipDto salarySlipDto=salarySlipService.cancelOrDeleteSalarySlip(session, slipDto,true);
                 
                // salarySlipDto.setName(null);
                // salarySlipDto.setAmended_from(salarySlipDto.getName());
                // salarySlipService.createSalarySlip(session, salarySlipDto);

                List<SalaireData> salaireDatas=new ArrayList<>();
                SalaireData salaireData=new SalaireData();
                salaireData.setMois(structureAssignement.getFrom_date());
                salaireData.setRefEmploye(structureAssignement.getEmployee());
                salaireData.setSalaireBase(structureAssignement.getBase());
                salaireData.setSalaryStructure(structureAssignement.getSalary_structure());
                salaireDatas.add(salaireData);
                salaireImportService.importSalaireData(session, salaireDatas);
                
                

                updatedSlips.add(slipDto);
            } catch (Exception e) {
                throw new Exception("Erreur lors du traitement de la Salary Slip " + slipDto.getName() + " : " + e.getMessage(), e);
            }
        }

        return updatedSlips;
    }

   

    

    

    
}
