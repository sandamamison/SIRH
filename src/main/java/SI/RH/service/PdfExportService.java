package SI.RH.service;

import SI.RH.dto.BulletinPaieResponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfExportService {

    private final DecimalFormat moneyFormat;

    public PdfExportService() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRENCH);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        this.moneyFormat = new DecimalFormat("#,##0.00", symbols);
    }

    public byte[] exportBulletinPdf(BulletinPaieResponse b) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 25, 25, 25, 25);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Polices
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            Font bodyBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.DARK_GRAY);

            // 1. Titre
            Paragraph title = new Paragraph("BULLETIN DE PAIE / FICHE DE PAIE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // 2. Bloc En-tête Salarié (identique à la photo 6 & 8)
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{22, 28, 22, 28});
            infoTable.setSpacingAfter(8);

            Color headerBg = new Color(240, 243, 246);

            addInfoCell(infoTable, "Matricule :", headerFont, headerBg, false);
            addInfoCell(infoTable, b.getMatricule() != null ? b.getMatricule() : "—", bodyBold, Color.WHITE, false);
            addInfoCell(infoTable, "Classification :", headerFont, headerBg, false);
            addInfoCell(infoTable, b.getClassification() != null ? b.getClassification() : "HC", bodyBold, Color.WHITE, false);

            addInfoCell(infoTable, "Salarié :", headerFont, headerBg, false);
            addInfoCell(infoTable, ((b.getNom() != null ? b.getNom() : "") + " " + (b.getPrenom() != null ? b.getPrenom() : "")).trim(), bodyBold, Color.WHITE, false);
            addInfoCell(infoTable, "Salaire de base :", headerFont, headerBg, false);
            addInfoCell(infoTable, formatMoney(b.getSalaireBase()), bodyBold, Color.WHITE, true);

            addInfoCell(infoTable, "Fonction :", headerFont, headerBg, false);
            addInfoCell(infoTable, b.getFonction() != null ? b.getFonction() : "—", bodyFont, Color.WHITE, false);
            addInfoCell(infoTable, "Taux journaliers :", headerFont, headerBg, false);
            addInfoCell(infoTable, formatMoney(b.getTauxJournalier()), bodyFont, Color.WHITE, true);

            addInfoCell(infoTable, "N° CNaPS :", headerFont, headerBg, false);
            addInfoCell(infoTable, b.getNumeroCnaps() != null ? b.getNumeroCnaps() : "—", bodyFont, Color.WHITE, false);
            addInfoCell(infoTable, "Taux horaires :", headerFont, headerBg, false);
            addInfoCell(infoTable, formatMoney(b.getTauxHoraire()), bodyFont, Color.WHITE, true);

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            addInfoCell(infoTable, "Date d'embauche :", headerFont, headerBg, false);
            addInfoCell(infoTable, b.getDateEmbauche() != null ? b.getDateEmbauche().format(df) : "—", bodyFont, Color.WHITE, false);
            addInfoCell(infoTable, "Indice :", headerFont, headerBg, false);
            addInfoCell(infoTable, b.getIndice() != null ? formatMoney(b.getIndice()) : "1 733,00", bodyFont, Color.WHITE, true);

            addInfoCell(infoTable, "Ancienneté :", headerFont, headerBg, false);
            PdfPCell ancCell = new PdfPCell(new Phrase(b.getAncienneteTexte() != null ? b.getAncienneteTexte() : "—", bodyBold));
            ancCell.setColspan(3);
            ancCell.setPadding(4);
            ancCell.setBorderColor(Color.LIGHT_GRAY);
            infoTable.addCell(ancCell);

            document.add(infoTable);

            // 3. Tableau des Rubriques (forme exacte Photo 6 / 8)
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{52, 14, 16, 18});

            Color colHeadBg = new Color(220, 230, 242);

            addTableHeaderCell(table, "Désignations", headerFont, colHeadBg, Element.ALIGN_LEFT);
            addTableHeaderCell(table, "Nombre", headerFont, colHeadBg, Element.ALIGN_CENTER);
            addTableHeaderCell(table, "Taux", headerFont, colHeadBg, Element.ALIGN_RIGHT);
            addTableHeaderCell(table, "Montant", headerFont, colHeadBg, Element.ALIGN_RIGHT);

            if (b.getLignes() != null) {
                for (BulletinPaieResponse.LigneBulletinDto l : b.getLignes()) {
                    boolean isHighlight = "TOTAL_BRUT".equals(l.getCode()) ||
                            "TOTAL_IRSA".equals(l.getCode()) ||
                            "TOTAL_RETENUES".equals(l.getCode()) ||
                            "NET_A_PAYER".equals(l.getCode());

                    Font fontToUse = isHighlight ? bodyBold : bodyFont;
                    Color rowBg = Color.WHITE;
                    if ("NET_A_PAYER".equals(l.getCode())) {
                        rowBg = new Color(230, 245, 230);
                    } else if (isHighlight) {
                        rowBg = new Color(245, 245, 245);
                    }

                    // Désignation
                    PdfPCell c1 = new PdfPCell(new Phrase(l.getDesignation(), fontToUse));
                    c1.setPadding(3);
                    c1.setBackgroundColor(rowBg);
                    c1.setBorderColor(Color.LIGHT_GRAY);
                    table.addCell(c1);

                    // Nombre
                    String nbrText = "";
                    if (l.getNombre() != null && l.getNombre().compareTo(BigDecimal.ZERO) > 0) {
                        if ("SALAIRE_BASE".equals(l.getCode())) {
                            nbrText = "1 mois";
                        } else {
                            nbrText = l.getNombre().stripTrailingZeros().toPlainString();
                        }
                    }
                    PdfPCell c2 = new PdfPCell(new Phrase(nbrText, fontToUse));
                    c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c2.setPadding(3);
                    c2.setBackgroundColor(rowBg);
                    c2.setBorderColor(Color.LIGHT_GRAY);
                    table.addCell(c2);

                    // Taux
                    String tauxText = "";
                    if (l.getTaux() != null && l.getTaux().compareTo(BigDecimal.ZERO) > 0) {
                        if (l.getCode().startsWith("IRSA_T")) {
                            tauxText = l.getTaux().stripTrailingZeros().toPlainString() + "%";
                        } else {
                            tauxText = formatMoney(l.getTaux());
                        }
                    }
                    PdfPCell c3 = new PdfPCell(new Phrase(tauxText, fontToUse));
                    c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c3.setPadding(3);
                    c3.setBackgroundColor(rowBg);
                    c3.setBorderColor(Color.LIGHT_GRAY);
                    table.addCell(c3);

                    // Montant
                    String montantText = "";
                    if (l.getMontant() != null && (l.getMontant().compareTo(BigDecimal.ZERO) != 0 || isHighlight)) {
                        montantText = formatMoney(l.getMontant().abs());
                        if (l.getMontant().compareTo(BigDecimal.ZERO) < 0) {
                            montantText = "-" + montantText;
                        }
                    }
                    PdfPCell c4 = new PdfPCell(new Phrase(montantText, fontToUse));
                    c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    c4.setPadding(3);
                    c4.setBackgroundColor(rowBg);
                    c4.setBorderColor(Color.LIGHT_GRAY);
                    table.addCell(c4);
                }
            }

            document.add(table);

            // 4. Signatures
            Paragraph sep = new Paragraph(" ");
            sep.setSpacingAfter(15);
            document.add(sep);

            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setWidths(new float[]{50, 50});

            PdfPCell sign1 = new PdfPCell();
            sign1.setBorder(Rectangle.NO_BORDER);
            sign1.addElement(new Paragraph("Signature de l'employeur / DRH :", bodyBold));
            sign1.addElement(new Paragraph("\n\n__________________________", bodyFont));
            signTable.addCell(sign1);

            PdfPCell sign2 = new PdfPCell();
            sign2.setBorder(Rectangle.NO_BORDER);
            sign2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sign2.addElement(new Paragraph("Signature du salarié (émargement) :", bodyBold));
            sign2.addElement(new Paragraph("\n\n__________________________", bodyFont));
            signTable.addCell(sign2);

            document.add(signTable);

            // Mentions légales bas de page
            Paragraph footer = new Paragraph("\nCe bulletin de paie doit être conservé sans limitation de durée (Code du travail de Madagascar).", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage(), e);
        }

        return out.toByteArray();
    }

    private void addInfoCell(PdfPTable table, String text, Font font, Color bg, boolean alignRight) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(bg);
        c.setPadding(3);
        c.setBorderColor(Color.LIGHT_GRAY);
        if (alignRight) c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c);
    }

    private void addTableHeaderCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(bg);
        c.setPadding(4);
        c.setHorizontalAlignment(align);
        c.setBorderColor(Color.GRAY);
        table.addCell(c);
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0,00";
        return moneyFormat.format(amount);
    }
}
