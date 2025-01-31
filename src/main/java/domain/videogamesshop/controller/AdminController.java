package domain.videogamesshop.controller;

import domain.videogamesshop.model.Transaction;
import domain.videogamesshop.service.PdfReportService;
import domain.videogamesshop.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PdfReportService pdfReportService;

    @GetMapping("/transactions/report")
    public ResponseEntity<byte[]> generateReport() {
        // Get all transactions (you can add filters if needed)
        List<Transaction> transactions = transactionService.getAllTransactions();

        // Generate the PDF report
        byte[] pdf = pdfReportService.generateTransactionReport(transactions);

        // Return the PDF as a downloadable file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
