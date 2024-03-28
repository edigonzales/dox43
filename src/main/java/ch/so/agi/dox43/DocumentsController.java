package ch.so.agi.dox43;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentsController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    // https://geo.so.ch/api/v1/document/kantonsgrenzsteine?feature=22239&x=2620446.1060485328&y=1257669.3676100764&crs=EPSG%3A2056
    // https://geo.so.ch/api/v1/document/grundstuecksbeschrieb?feature=21396731&x=2607902.4078048863&y=1228274.0228574278&crs=EPSG%3A2056
    // https://geo.so.ch/api/v1/document/grundstuecksbeschrieb?feature=21361869&x=2608068.8582796236&y=1228108.4535374695&crs=EPSG%3A2056
    @GetMapping(path = "/documents/{document}")
    public ResponseEntity<?> getDocument(@PathVariable("document") String document,
            @RequestParam Map<String, String> queryParameters) throws Exception {

        
        return null;
    }
}
