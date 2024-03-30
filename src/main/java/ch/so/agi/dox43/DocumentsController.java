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
    
    // http://localhost:8080/documents/grundstuecksbeschrieb?x=2607708&y=1228739
    // http://localhost:8080/documents/grundstuecksbeschrieb?x=2608026&y=1228149
    // http://localhost:8080/documents/ewsstandortblatt?x=2608026&y=1228149
    @GetMapping(path = "/documents/{document}")
    public ResponseEntity<?> getDocument(@PathVariable("document") String document,
            @RequestParam Map<String, String> queryParameters) throws Exception {

        logger.debug("document: "+document);
        
//        File outFile = documentGenerator.generateFileFromSql(document, queryParameters);
        byte[] outFile = documentGenerator.generateFileFromSql(document, queryParameters);
//        InputStream is = new FileInputStream(outFile);

        return ResponseEntity
                .ok().header("content-disposition", "attachment; filename=" + document + ".pdf")
                .contentLength(outFile.length)
                .contentType(MediaType.APPLICATION_PDF).body(outFile);                
    }
}
