package ch.so.agi.dox43;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import ch.so.agi.dox43.config.DataSourceProperties;

@Service
public class DocumentGenerator {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

//    private final DataSourceProperties dataSourceProperties;

    private final JdbcTemplateFactory jdbcTemplateFactory;
    
    @Value("${app.configDirectory}")
    private String configDirectory;

    @Value("${app.workDirectory}")
    private String workDirectory;

    @Value("${app.folderPrefix}")
    private String folderPrefix;
    
//    public DocumentGenerator(DataSourceProperties dataSourceProperties) {
//        this.dataSourceProperties = dataSourceProperties;
//    }

    public DocumentGenerator(JdbcTemplateFactory jdbcTemplateFactory) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
    }

    public byte[] generateFileFromSql(String documentName) throws IOException {
        
        // TODO: Parameter aus Controller. Achtung: Da ist alles ein String, weil sie beliebig sein können.
        // Siehe Beispiel bfsnr=XXXX. Entweder muss es vorher behandeln, oder im SQL, wenn man als SQL-
        // Entwickler davon ausgeht, dass immer ein String kommt. Dann kann ich es casten.
        
        // TODO: read db from *.ini
        
        Path outputDirectory = Files.createTempDirectory(Paths.get(workDirectory), folderPrefix);
        logger.debug(outputDirectory.toString());
        
        //https://github.com/sogis/ilivalidator-web-service/blob/7eaba5f787a0f24e17f323e8c2b1e3b74af57d17/src/main/java/ch/so/agi/ilivalidator/IlivalidatorWebServiceApplication.java#L75        
        
        
        String stmt = Files.readString(Paths.get(configDirectory, documentName+".sql"));
        //System.out.println(stmt);

        NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplateFactory.getClient("pub");
        
        // Es können mehr Parameter übergeben werden, als im SQL-Skript vorhanden sind.
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("xcoord", 2608026);
        paramMap.put("ycoord", 1228149);

        String xmlResult = jdbcTemplate.queryForObject(stmt, paramMap, String.class);


        
        
        return null;
        
    }

    
}
