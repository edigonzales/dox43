
package ch.so.agi.dox43;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private DocxGenerator docxGenerator;
    
    public MainController(DocxGenerator docxGenerator) {
        this.docxGenerator = docxGenerator;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("ping");        
        return new ResponseEntity<String>("dox43", HttpStatus.OK);
    }
    
    @GetMapping(path = "/reports")
    public ResponseEntity<?> getReport() {
        byte[] result;

        try {
          result = docxGenerator.generateDocxFileFromTemplate();
        } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity
                  .internalServerError()
                  .body("Please contact service provider.");
        }
        
        return ResponseEntity
                .ok().header("content-disposition", "attachment; filename=\"message.docx\"")
                //.contentLength(image.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(result);                

    }

    @GetMapping(path = "/reports-pdf")
    public void getReportPdf() {
        byte[] result;

        try {
          docxGenerator.generatePdfFileFromTemplate();
        } catch (Exception e) {
          e.printStackTrace();
//          return ResponseEntity
//                  .internalServerError()
//                  .body("Please contact service provider.");
        }
        
//        return ResponseEntity
//                .ok().header("content-disposition", "attachment; filename=\"message.pdf\"")
//                //.contentLength(image.length)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(result);                

    }
}
