package com.evaluation.erpnext_spring.service;

import java.io.OutputStream;

import org.springframework.stereotype.Service;

import com.evaluation.erpnext_spring.dto.salaries.SalarySlipDto;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.*;

@Service
public class ExportPdfService {
    public void exporterFicheDePaiePDF(SalarySlipDto salaire, OutputStream out) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Titre
        Font titreFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titre = new Paragraph("FICHE DE PAIE", titreFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        document.add(titre);

        // Table principale
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new float[]{1, 2});

        // Ajout des champs principaux
        ajouterLigne(table, "Employé :", salaire.getEmployeeName());
        ajouterLigne(table, "Matricule :", salaire.getEmployee());
        ajouterLigne(table, "Département :", salaire.getDepartment());
        ajouterLigne(table, "Poste :", salaire.getDesignation());
        ajouterLigne(table, "Période :", salaire.getStartDate() + " au " + salaire.getEndDate());
        ajouterLigne(table, "Salaire brut :", salaire.getGrossPay() + " " + salaire.getCurrency());
        ajouterLigne(table, "Total déductions :", salaire.getTotalDeduction() + " " + salaire.getCurrency());
        ajouterLigne(table, "Salaire net :", salaire.getNetPay() + " " + salaire.getCurrency());
        ajouterLigne(table, "Net en lettres :", salaire.getTotalInWords());
        ajouterLigne(table, "Mode de paiement :", salaire.getModeOfPayment());

        document.add(table);

        // Signature
        Paragraph signature = new Paragraph("\n\nSignature de l’employeur : ____________________", new Font(Font.HELVETICA, 10));
        signature.setAlignment(Element.ALIGN_LEFT);
        document.add(signature);

        document.close();
    }

    private void ajouterLigne(PdfPTable table, String label, String valeur) {
        Font fontLabel = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font fontValeur = new Font(Font.HELVETICA, 11);
        PdfPCell cell1 = new PdfPCell(new Phrase(label, fontLabel));
        PdfPCell cell2 = new PdfPCell(new Phrase(valeur != null ? valeur : "", fontValeur));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        table.addCell(cell2);
    }
}
