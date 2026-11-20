package SI.RH.service;

import SI.RH.dto.BulletinPaieResponse;
import SI.RH.dto.EtatPaieDto;
import SI.RH.entity.PeriodePaie;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    public byte[] exportEtatPaieExcel(PeriodePaie periode, List<EtatPaieDto> rows, List<BulletinPaieResponse> bulletins) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            // Styles
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 9);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 9);

            XSSFFont regularFont = workbook.createFont();
            regularFont.setFontHeightInPoints((short) 9);

            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 13);

            // Style d'en-tête de colonnes (Vert clair / gris pro comme Excel)
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 235, 220), new DefaultIndexedColorMap()));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(headerStyle, BorderStyle.THIN);

            // Style montant
            DataFormat format = workbook.createDataFormat();
            short moneyFormat = format.getFormat("#,##0.00");

            XSSFCellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setFont(regularFont);
            moneyStyle.setDataFormat(moneyFormat);
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            setBorders(moneyStyle, BorderStyle.THIN);

            XSSFCellStyle moneyBoldStyle = workbook.createCellStyle();
            moneyBoldStyle.setFont(boldFont);
            moneyBoldStyle.setDataFormat(moneyFormat);
            moneyBoldStyle.setAlignment(HorizontalAlignment.RIGHT);
            setBorders(moneyBoldStyle, BorderStyle.THIN);

            XSSFCellStyle textStyle = workbook.createCellStyle();
            textStyle.setFont(regularFont);
            setBorders(textStyle, BorderStyle.THIN);

            XSSFCellStyle textCenterStyle = workbook.createCellStyle();
            textCenterStyle.setFont(regularFont);
            textCenterStyle.setAlignment(HorizontalAlignment.CENTER);
            setBorders(textCenterStyle, BorderStyle.THIN);

            XSSFCellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setFont(regularFont);
            dateStyle.setAlignment(HorizontalAlignment.CENTER);
            dateStyle.setDataFormat(format.getFormat("dd/mm/yyyy"));
            setBorders(dateStyle, BorderStyle.THIN);

            // ==============================================================
            // FEUILLE 1 : ETAT DE PAIE (PHOTO 7)
            // ==============================================================
            XSSFSheet sheetEtat = workbook.createSheet("ETAT DE PAIE");
            sheetEtat.setDisplayGridlines(true);

            // Titre en D2
            Row titleRow = sheetEtat.createRow(1);
            Cell titleCell = titleRow.createCell(3);
            titleCell.setCellValue("ETAT DE PAIE " + periode.getCode() + " (Clôture)");
            XSSFCellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // En-têtes en ligne 4 (index 3)
            Row hRow = sheetEtat.createRow(3);
            String[] headers = {
                    "DATE", "NBRE", "N°MATR", "N° CNAPS", "NOM et Prénoms",
                    "Date d'embauche", "ABSENCE du mois", "CAT", "Fonction",
                    "SALAIRE DE BASE", "RETENUES", "RAPPEL AUTRES", "HR SUP",
                    "TOTAL BRUT", "IRSA", "NET A PAYER"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 4;
            BigDecimal totalBase = BigDecimal.ZERO;
            BigDecimal totalRetenues = BigDecimal.ZERO;
            BigDecimal totalRappel = BigDecimal.ZERO;
            BigDecimal totalHs = BigDecimal.ZERO;
            BigDecimal totalBrut = BigDecimal.ZERO;
            BigDecimal totalIrsa = BigDecimal.ZERO;
            BigDecimal totalNet = BigDecimal.ZERO;

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yy");

            for (EtatPaieDto row : rows) {
                Row r = sheetEtat.createRow(rowIdx++);

                // DATE
                Cell c0 = r.createCell(0);
                c0.setCellValue(row.getDate() != null ? row.getDate().format(df) : "");
                c0.setCellStyle(textCenterStyle);

                // NBRE
                Cell c1 = r.createCell(1);
                c1.setCellValue(row.getNbre());
                c1.setCellStyle(textCenterStyle);

                // N°MATR
                Cell c2 = r.createCell(2);
                c2.setCellValue(row.getMatricule());
                c2.setCellStyle(textCenterStyle);

                // N° CNAPS
                Cell c3 = r.createCell(3);
                c3.setCellValue(row.getNumeroCnaps() != null ? row.getNumeroCnaps() : "");
                c3.setCellStyle(textCenterStyle);

                // NOM et Prénoms
                Cell c4 = r.createCell(4);
                c4.setCellValue(row.getNomPrenoms());
                c4.setCellStyle(textStyle);

                // Date d'embauche
                Cell c5 = r.createCell(5);
                c5.setCellValue(row.getDateEmbauche() != null ? row.getDateEmbauche().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
                c5.setCellStyle(textCenterStyle);

                // ABSENCE du mois
                Cell c6 = r.createCell(6);
                if (row.getAbsenceMois() != null && row.getAbsenceMois().compareTo(BigDecimal.ZERO) > 0) {
                    c6.setCellValue(row.getAbsenceMois().doubleValue());
                    c6.setCellStyle(moneyStyle);
                } else {
                    c6.setCellValue("-");
                    c6.setCellStyle(textCenterStyle);
                }

                // CAT
                Cell c7 = r.createCell(7);
                c7.setCellValue(row.getCategorie());
                c7.setCellStyle(textCenterStyle);

                // Fonction
                Cell c8 = r.createCell(8);
                c8.setCellValue(row.getFonction());
                c8.setCellStyle(textStyle);

                // SALAIRE DE BASE
                Cell c9 = r.createCell(9);
                c9.setCellValue(row.getSalaireBase().doubleValue());
                c9.setCellStyle(moneyStyle);
                totalBase = totalBase.add(row.getSalaireBase());

                // RETENUES
                Cell c10 = r.createCell(10);
                c10.setCellValue(row.getRetenues().doubleValue());
                c10.setCellStyle(moneyStyle);
                totalRetenues = totalRetenues.add(row.getRetenues());

                // RAPPEL AUTRES
                Cell c11 = r.createCell(11);
                if (row.getRappelAutres() != null && row.getRappelAutres().compareTo(BigDecimal.ZERO) > 0) {
                    c11.setCellValue(row.getRappelAutres().doubleValue());
                    c11.setCellStyle(moneyStyle);
                    totalRappel = totalRappel.add(row.getRappelAutres());
                } else {
                    c11.setCellValue("-");
                    c11.setCellStyle(textCenterStyle);
                }

                // HR SUP
                Cell c12 = r.createCell(12);
                if (row.getHeuresSupplementaires() != null && row.getHeuresSupplementaires().compareTo(BigDecimal.ZERO) > 0) {
                    c12.setCellValue(row.getHeuresSupplementaires().doubleValue());
                    c12.setCellStyle(moneyStyle);
                    totalHs = totalHs.add(row.getHeuresSupplementaires());
                } else {
                    c12.setCellValue("-");
                    c12.setCellStyle(textCenterStyle);
                }

                // TOTAL BRUT
                Cell c13 = r.createCell(13);
                c13.setCellValue(row.getTotalBrut().doubleValue());
                c13.setCellStyle(moneyStyle);
                totalBrut = totalBrut.add(row.getTotalBrut());

                // IRSA
                Cell c14 = r.createCell(14);
                c14.setCellValue(row.getTotalIrsa().doubleValue());
                c14.setCellStyle(moneyStyle);
                totalIrsa = totalIrsa.add(row.getTotalIrsa());

                // NET A PAYER
                Cell c15 = r.createCell(15);
                c15.setCellValue(row.getNetAPayer().doubleValue());
                c15.setCellStyle(moneyBoldStyle);
                totalNet = totalNet.add(row.getNetAPayer());
            }

            // Ligne de TOTAL (comme sur Photo 7)
            Row totRow = sheetEtat.createRow(rowIdx);
            Cell totLabel = totRow.createCell(8);
            totLabel.setCellValue("TOTAL");
            totLabel.setCellStyle(moneyBoldStyle);

            Cell totBase = totRow.createCell(9);
            totBase.setCellValue(totalBase.doubleValue());
            totBase.setCellStyle(moneyBoldStyle);

            Cell totRet = totRow.createCell(10);
            totRet.setCellValue(totalRetenues.doubleValue());
            totRet.setCellStyle(moneyBoldStyle);

            Cell totRap = totRow.createCell(11);
            totRap.setCellValue(totalRappel.doubleValue());
            totRap.setCellStyle(moneyBoldStyle);

            Cell totHs = totRow.createCell(12);
            totHs.setCellValue(totalHs.doubleValue());
            totHs.setCellStyle(moneyBoldStyle);

            Cell totBrutCell = totRow.createCell(13);
            totBrutCell.setCellValue(totalBrut.doubleValue());
            totBrutCell.setCellStyle(moneyBoldStyle);

            Cell totIrsaCell = totRow.createCell(14);
            totIrsaCell.setCellValue(totalIrsa.doubleValue());
            totIrsaCell.setCellStyle(moneyBoldStyle);

            Cell totNetCell = totRow.createCell(15);
            totNetCell.setCellValue(totalNet.doubleValue());
            totNetCell.setCellStyle(moneyBoldStyle);

            // Ajustement colonnes
            for (int i = 0; i < headers.length; i++) {
                sheetEtat.autoSizeColumn(i);
            }

            // ==============================================================
            // FEUILLE 2 : calcul hs (PHOTO 9)
            // ==============================================================
            XSSFSheet sheetHs = workbook.createSheet("calcul hs");
            sheetHs.setDisplayGridlines(true);

            int hsRowIdx = 1;
            for (BulletinPaieResponse b : bulletins) {
                // En-tête pour le salarié
                Row rNom = sheetHs.createRow(hsRowIdx++);
                Cell cNom = rNom.createCell(0);
                cNom.setCellValue("HEURES SUPPLEMENTAIRES — " + b.getNom() + " " + b.getPrenom() + " (" + b.getMatricule() + ")");
                cNom.setCellStyle(titleStyle);

                // Ligne BASE
                Row rBase = sheetHs.createRow(hsRowIdx++);
                rBase.createCell(3).setCellValue("BASE");
                Cell cValBase = rBase.createCell(4);
                cValBase.setCellValue(b.getSalaireBase().doubleValue());
                cValBase.setCellStyle(moneyBoldStyle);
                rBase.createCell(5).setCellValue("173,33 HRS");

                // Ligne BASE / HRS
                Row rTaux = sheetHs.createRow(hsRowIdx++);
                rTaux.createCell(3).setCellValue("BASE / HRS");
                Cell cValTaux = rTaux.createCell(4);
                cValTaux.setCellValue(b.getTauxHoraire().doubleValue());
                cValTaux.setCellStyle(moneyBoldStyle);

                hsRowIdx++; // espace

                if (b.getCalculHsSemaines() != null) {
                    for (BulletinPaieResponse.CalculHsDto s : b.getCalculHsSemaines()) {
                        // Semaine X
                        Row rSem = sheetHs.createRow(hsRowIdx++);
                        rSem.createCell(1).setCellValue(s.getNumeroSemaine() + " semaine");
                        rSem.createCell(3).setCellValue("BASE / HRS");
                        Cell cT = rSem.createCell(4);
                        cT.setCellValue(b.getTauxHoraire().doubleValue());
                        cT.setCellStyle(moneyStyle);

                        // 8 premier hrs
                        Row r1 = sheetHs.createRow(hsRowIdx++);
                        r1.createCell(1).setCellValue("8 premier hrs");
                        r1.createCell(2).setCellValue(s.getHeures30().doubleValue());
                        r1.createCell(4).setCellValue(s.getHeures30().doubleValue());
                        r1.createCell(5).setCellValue(1.3);
                        Cell cM1 = r1.createCell(6);
                        cM1.setCellValue(s.getMontant30().doubleValue());
                        cM1.setCellStyle(moneyStyle);

                        // 12 hrs restant
                        Row r2 = sheetHs.createRow(hsRowIdx++);
                        r2.createCell(1).setCellValue("12 hrs restant");
                        r2.createCell(4).setCellValue(s.getHeures50().doubleValue());
                        r2.createCell(5).setCellValue(1.5);
                        Cell cM2 = r2.createCell(6);
                        cM2.setCellValue(s.getMontant50().doubleValue());
                        cM2.setCellStyle(moneyStyle);

                        // week-end
                        Row r3 = sheetHs.createRow(hsRowIdx++);
                        r3.createCell(1).setCellValue("week-end");
                        r3.createCell(4).setCellValue(s.getHeuresWeekend().doubleValue());
                        r3.createCell(5).setCellValue(1.4);
                        Cell cM3 = r3.createCell(6);
                        cM3.setCellValue(s.getMontantWeekend().doubleValue());
                        cM3.setCellStyle(moneyStyle);

                        // Fér
                        Row r4 = sheetHs.createRow(hsRowIdx++);
                        r4.createCell(1).setCellValue("Fér");
                        r4.createCell(4).setCellValue(s.getHeuresFerie().doubleValue());
                        r4.createCell(5).setCellValue(2.0);
                        Cell cM4 = r4.createCell(6);
                        cM4.setCellValue(s.getMontantFerie().doubleValue());
                        cM4.setCellStyle(moneyStyle);

                        // TOTAL HS
                        Row rTot = sheetHs.createRow(hsRowIdx++);
                        rTot.createCell(4).setCellValue("TOTAL HS");
                        rTot.createCell(5).setCellValue(s.getTotalHeures().doubleValue());
                        Cell cTotSem = rTot.createCell(6);
                        cTotSem.setCellValue(s.getMontantSemaine().doubleValue());
                        cTotSem.setCellStyle(moneyBoldStyle);

                        hsRowIdx++; // séparation
                    }
                }

                // Total général HS employé
                Row rFin = sheetHs.createRow(hsRowIdx++);
                rFin.createCell(4).setCellValue("TOTAL MOIS HS");
                Cell cFinM = rFin.createCell(6);
                cFinM.setCellValue(b.getTotalHs().doubleValue());
                cFinM.setCellStyle(moneyBoldStyle);

                hsRowIdx += 2;
            }

            for (int i = 0; i < 8; i++) {
                sheetHs.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du fichier Excel : " + e.getMessage(), e);
        }
    }

    private void setBorders(CellStyle style, BorderStyle border) {
        style.setBorderTop(border);
        style.setBorderBottom(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
    }
}
