package domain.videogamesshop.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import domain.videogamesshop.model.Transaction;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class PdfReportService {

    public byte[] generateTransactionReport(List<Transaction> transactions) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Paragraph title = new Paragraph("Game Purchase Statistics");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Empty line for spacing

            // Create a table with transaction details
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add table headers
            table.addCell("Transaction ID");
            table.addCell("Date");
            table.addCell("User");
            table.addCell("Amount");
            table.addCell("Games");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Populate table rows
            for (Transaction transaction : transactions) {
                table.addCell(String.valueOf(transaction.getId()));
                table.addCell(dateFormat.format(convertToDate(transaction.getTransactionDate())));
                table.addCell(transaction.getUser().getLogin());
                table.addCell(String.valueOf(transaction.getAmount()));

                // Concatenate game names
                StringBuilder gameNames = new StringBuilder();
                transaction.getGames().forEach(game -> gameNames.append(game.getName()).append(", "));
                if (gameNames.length() > 0) {
                    gameNames.setLength(gameNames.length() - 2); // Remove trailing comma and space
                }
                table.addCell(gameNames.toString());
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out.toByteArray();
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
