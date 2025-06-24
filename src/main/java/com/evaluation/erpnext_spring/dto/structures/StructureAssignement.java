package com.evaluation.erpnext_spring.dto.structures;

// import jakarta.persistence.Entity;
import lombok.Data;

@Data
// @Entity(name = "tabSalary Structure Assignment")
public class StructureAssignement {
    private String name;
    private String company;
    private String salary_structure;
    private String currency;
    private String employee;
    private String employee_name; 
    private String from_date;
    private String to_date;
    private Double base;
    private Double variable;
    private Double total;  
    private String pay_frequency;  
    private String department;
    private String cost_center;
    private String branch;  
    private Boolean is_active;
    private String created_by;
    private String creation_date;
    private String modified_by;
    private String modified_date;
    private String notes;  
    private String amended_from;

    
    public StructureAssignement(
            String company,
            String salary_structure,
            String currency,
            String employee,
            String from_date,
            Double base,
            Double variable,
            String pay_frequency,
            String department,
            String cost_center) {
        this.company = company;
        this.salary_structure = salary_structure;
        this.currency = currency;
        this.employee = employee;
        this.from_date = from_date;
        this.base = base;
        this.variable = variable;
        this.pay_frequency = pay_frequency;
        this.department = department;
        this.cost_center = cost_center;
        
        this.is_active = true; 
    }

    
    public StructureAssignement() {}

    
    public StructureAssignement(String company, String salary_structure, String currency, String employee,
            String from_date, Double base, Double variable) {
        this.company = company;
        this.salary_structure = salary_structure;
        this.currency = currency;
        this.employee = employee;
        this.from_date = from_date;
        this.base = base;
        this.variable = variable;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StructureAssignement that = (StructureAssignement) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (company != null ? !company.equals(that.company) : that.company != null) return false;
        if (salary_structure != null ? !salary_structure.equals(that.salary_structure) : that.salary_structure != null)
            return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (employee != null ? !employee.equals(that.employee) : that.employee != null) return false;
        if (from_date != null ? !from_date.equals(that.from_date) : that.from_date != null) return false;
        if (base != null ? !base.equals(that.base) : that.base != null) return false;
        if (variable != null ? !variable.equals(that.variable) : that.variable != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (salary_structure != null ? salary_structure.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        result = 31 * result + (from_date != null ? from_date.hashCode() : 0);
        result = 31 * result + (base != null ? base.hashCode() : 0);
        result = 31 * result + (variable != null ? variable.hashCode() : 0);
        return result;
    }

}
