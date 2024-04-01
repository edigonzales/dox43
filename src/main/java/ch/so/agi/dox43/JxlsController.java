package ch.so.agi.dox43;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.io.InputStream;

import org.jxls.builder.JxlsOutputFile;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.InputStreamResource;

@RestController
public class JxlsController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.configDirectory}")
    private String configDirectory;
    
    @Value("${app.workDirectory}")
    private String workDirectory;

    @Value("${app.folderPrefix}")
    private String folderPrefix;
    
    private JdbcTemplateFactory jdbcTemplateFactory;
    
    public JxlsController(JdbcTemplateFactory jdbcTemplateFactory) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
    }

    @GetMapping("/xls")
    public ResponseEntity<?> ping() throws IOException {
        logger.info("jxls");
        
        Path outputDirectory = Files.createTempDirectory(Paths.get(workDirectory), folderPrefix);

        
//        List<Employee> employees = new ArrayList<>();
//        
//        employees.add(new Employee("Stefan Ziegler", new Date(), BigDecimal.valueOf(10000)));
//        
//        Map<String, Object> data = new HashMap<>();
//        data.put("employees", employees);
//        JxlsPoiTemplateFillerBuilder.newInstance()
//                .withTemplate("/Users/stefan/tmp/EachTest.xlsx")
//                .build()
//                .fill(data, new JxlsOutputFile( new File("/Users/stefan/tmp/report.xlsx")));

        NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplateFactory.getClient("pub");
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("database key not found: " + "pub");
        }

        // To be defined. Alle sql-Files eruieren.
        List<Path> sqlFiles = new ArrayList<Path>();
        try (Stream<Path> walk = Files.walk(Paths.get(configDirectory))) {
            sqlFiles = walk
                    .filter(p -> !Files.isDirectory(p))
                    .filter(f -> {
                        if (f.toFile().getAbsolutePath().toLowerCase().endsWith("sql")) {
                            return true;
                        } else {
                            return false;
                        }
                    })
                    .filter(f -> {
                        if (f.toFile().getAbsolutePath().toLowerCase().contains("avmeldewesen-")) {
                            return true;
                        } else {
                            return false;
                        }
                    }) 
                    .collect(Collectors.toList());        
        }
        
        logger.debug(sqlFiles.toString());
        
        Map<String, Object> data = new HashMap<>();

        for (Path sqlFile : sqlFiles) {
            String stmt = Files.readString(sqlFile);
            Map<String, Object> params = Map.of("condition", "foo");
            List<Map<String, Object>> result = jdbcTemplate.queryForList(stmt, params);
            logger.debug(result.toString());
            
            
            String sqlFileName = sqlFile.getFileName().toString().substring(sqlFile.getFileName().toString().lastIndexOf("-")+1);
            String sqlContextName = sqlFileName.replace(".sql", "");
                    
            logger.debug(sqlContextName);
            
            data.put(sqlContextName, result);
        }

        
        
        // Key muss geklärt werden. SQL-Filename? Filename müsste eindeutig sein. 
        // Das ging einfach, wenn jeder Report im eigenen Verzeichnis liegt (mit 
        // dem Reportnamen).
//        Map<String, Object> data = new HashMap<>();
//        {
//            List<Map<String,Object>> result = new ArrayList<>();
//            result.add(Map.of("nummer", "7158", "egrid", "CH407795770985", "flaechenmass", 97.39894549895791));
//            result.add(Map.of("nummer", "975", "egrid", "CH107006353219", "flaechenmass", 2405.949899503765));
//            data.put("grundstuecke", result);            
//        }        
//        {
//            List<Map<String,Object>> result = new ArrayList<>();
//            result.add(Map.of("egid", "191719059", "nachfuehrung", LocalDate.parse("2018-05-05"), "flaeche", 426.60694750318635));
//            result.add(Map.of("egid", "191719055", "nachfuehrung", LocalDate.parse("2018-08-30"), "flaeche", 541.2809085013212));
//            data.put("gebaeude", result);            
//        }
        {
            data.put("singlevalue", "Einzelner Wert. Aber wie generisch machen?");                        
        }
        
        
        File xlsxOutFile = Paths.get(outputDirectory.toString(), "avmeldewesen.xlsx").toFile();

        JxlsPoiTemplateFillerBuilder.newInstance()
                .withTemplate(Paths.get(configDirectory, "avmeldewesen.xlsx").toString())
                .build()
                .fill(data, new JxlsOutputFile(xlsxOutFile));

        
        InputStream is = new FileInputStream(xlsxOutFile);
        return ResponseEntity
                .ok().header("content-disposition", "attachment; filename="+xlsxOutFile.getName())
                .contentLength(xlsxOutFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(is));                
    }
}
