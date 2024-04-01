package ch.so.agi.dox43;

import java.io.File;
import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentsController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private DocumentsGenerator documentGenerator;
    
    public DocumentsController(DocumentsGenerator documentGenerator) {
        this.documentGenerator = documentGenerator;
    }
    
    // https://geo.so.ch/api/v1/document/kantonsgrenzsteine?feature=22239&x=2620446.1060485328&y=1257669.3676100764&crs=EPSG%3A2056
    // https://geo.so.ch/api/v1/document/grundstuecksbeschrieb?feature=21396731&x=2607902.4078048863&y=1228274.0228574278&crs=EPSG%3A2056
    // https://geo.so.ch/api/v1/document/grundstuecksbeschrieb?feature=21361869&x=2608068.8582796236&y=1228108.4535374695&crs=EPSG%3A2056
    
    // http://localhost:8080/report/grundstuecksbeschrieb?x=2607708&y=1228739&format=pdf
    // http://localhost:8080/report/grundstuecksbeschrieb?x=2608026&y=1228149&format=pdf
    // http://localhost:8080/report/ewsstandortblatt?x=2608026&y=1228149&format=pdf // At the moment statisches SQL 
    // http://localhost:8080/report/avmeldewesen?x=2608026&y=1228149&format=xlsx // At the moment statisches SQL
    
    // Umgang mit unterschiedlichen Formaten pro Report noch offen. Jetzt steckt das Format
    // auch noch im ini-File. Scheint jetzt momentan überflüssig, da ich es aus der URL
    // auslesen. Könnte aber verwendet werden, wenn mehrere Formate pro Report unterstützt
    // wird.
    @GetMapping(path = "/report/{report}")
    public ResponseEntity<?> getDocument(@PathVariable("report") String report,
            @RequestParam(name = "format", required = true) String format,
            @RequestParam Map<String, String> queryParameters) throws Exception {

        logger.debug("report: "+report);
        
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        String fileExtension = "unknow";
        if (format.equalsIgnoreCase(AppConstants.PARAM_CONST_PDF)) {
            mediaType = MediaType.APPLICATION_PDF;
            fileExtension = AppConstants.PARAM_CONST_PDF;
        } else if (format.equalsIgnoreCase(AppConstants.PARAM_CONST_XLSX)) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
            fileExtension = AppConstants.PARAM_CONST_XLSX;
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        
        byte[] outFile = documentGenerator.generateFile(report, queryParameters);

        return ResponseEntity
                .ok().header("content-disposition", "attachment; filename=" + report + "." + fileExtension)
                .contentLength(outFile.length)
                .contentType(mediaType).body(outFile);                
    }
}
