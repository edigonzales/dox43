
package ch.so.agi.dox43;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    
    /* https://dox42.so.ch/dox42restservice.ashx?Operation=GenerateDocument
     &ReturnAction.Format=pdf&DocTemplate=c%3a%5cdox42Server%5ctemplates%5cAFU%5cEWS_moeglich.docx
     &InputParam.p_koordinate_x=2607908&InputParam.p_koordinate_y=1228275
     &InputParam.p_grundstueck=1125%20(Solothurn)&InputParam.p_gemeinde=Solothurn
     &InputParam.p_tiefe=100&InputParam.p_tiefe_gruende=Instabiler_UG&InputParam.p_gw=true
    */
    // http://localhost:8080/reports/docx?DocTemplate=template_frutiger_bild.docx&StringInputParam.salutation=Herr&StringInputParam.firstName=Stefan&StringInputParam.lastName=Ziegler&StringInputParam.message=Hallo Welt&WmsInputParam.Image1=https%3A%2F%2Fgeo.so.ch%2Fows%2Fsomap%3FSERVICE%3DWMS%26VERSION%3D1.3.0%26REQUEST%3DGetMap%26FORMAT%3Dimage%252Fpng%26TRANSPARENT%3Dtrue%26LAYERS%3Dch.so.agi.av.bodenbedeckung_einzelobjekte%26STYLES%3D%26SRS%3DEPSG%253A2056%26CRS%3DEPSG%253A2056%26TILED%3Dfalse%26OPACITIES%3D255%26__t%3D1711045775028%26DPI%3D180%26WIDTH%3D3471%26HEIGHT%3D1318%26BBOX%3D2607777.2060933253%252C1228230.6308076198%252C2608006.798280825%252C1228317.811015953
    @GetMapping(path = "/reports/{format}")
    public ResponseEntity<?> getReport(@PathVariable("format") String format,
            @RequestParam(name = "DocTemplate", required = true) String docTemplate,
            @RequestParam Map<String, String> queryParameters) {
        
        HashMap<String, String> docVariables = new HashMap<>();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            if (entry.getKey().toLowerCase().contains("inputparam.")) {
                
                // TODO: substring logik erst im Service, da ich mit WMS auch dealen muss.
                String paramName = entry.getKey().substring(entry.getKey().indexOf(".")+1);
                docVariables.put(paramName, entry.getValue());
            } 
        }
        
        byte[] result = null;
        try {
            result = docxGenerator.generateDocxFileFromTemplate(docTemplate, docVariables);
          } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body("Please contact service provider.");
          }
          
          return ResponseEntity
                  .ok().header("content-disposition", "attachment; filename=\"document.docx\"")
                  .contentLength(result.length)
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
