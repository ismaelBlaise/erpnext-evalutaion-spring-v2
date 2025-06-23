package com.evaluation.erpnext_spring.dto.structures;

import java.util.List;

public class StructureAssignementResponse {
    
    private List<StructureAssignement> data;

    public List<StructureAssignement> getData() {
        return data;
    }

    public void setData(List<StructureAssignement> data) {
        this.data = data;
    }
}
