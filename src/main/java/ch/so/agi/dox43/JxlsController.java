package ch.so.agi.dox43;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;

import org.jxls.builder.JxlsOutputFile;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JxlsController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/xls")
    public ResponseEntity<String> ping() throws FileNotFoundException {
        logger.info("jxls");
        
        // IDEE:
        // - Support mehrere SQL-Abfragen: irgendwie über file name convention, z.B. "fubar-xxx.sql", "fubar-yyy.sql".
        // Hier kann man eine Logik definieren wie "startsWith".
        
        List<Employee> employees = new ArrayList<>();
        
        employees.add(new Employee("Stefan Ziegler", new Date(), BigDecimal.valueOf(10000)));
        
        Map<String, Object> data = new HashMap<>();
        data.put("employees", employees);
        JxlsPoiTemplateFillerBuilder.newInstance()
                .withTemplate("/Users/stefan/tmp/EachTest.xlsx")
                .build()
                .fill(data, new JxlsOutputFile( new File("/Users/stefan/tmp/report.xlsx")));

        
        
        return new ResponseEntity<String>("jxls", HttpStatus.OK);
    }

}
