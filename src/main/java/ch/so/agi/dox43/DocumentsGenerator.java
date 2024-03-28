package ch.so.agi.dox43;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.FileInputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import ch.so.agi.dox43.config.DataSourceProperties;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;

@Service
public class DocumentsGenerator {
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

    public DocumentsGenerator(JdbcTemplateFactory jdbcTemplateFactory) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
    }

    public byte[] generateFileFromSql(String documentName, Map<String,String> queryParameters) throws IOException, SaxonApiException {
        // In das Verzeichnis wird alles kopiert und die Transformation speichert die 
        // Dokumente hier.
        Path outputDirectory = Files.createTempDirectory(Paths.get(workDirectory), folderPrefix);
        logger.debug(outputDirectory.toString());
        
        // Konfiguration aus ini-Datei lesen. 
        // Man muss definieren welche Datenbank verwendet wird.
        Properties props = new Properties();
        props.load(new FileInputStream(Paths.get(configDirectory, documentName + ".ini").toFile()));
        String dbKey = props.getProperty("db");
        String formats = props.getProperty("formats");
                
        // SQL-Befehl aus Datei lesen
        String stmt = Files.readString(Paths.get(configDirectory, documentName+".sql"));

        // JdbcTemplate aus Map lesen. Die JdbcTemplates werden beim Starten aus allen DB-Konfigs 
        // aus dem application.properties erstellt.
        NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplateFactory.getClient(dbKey);
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("database key not found: " + dbKey);
        }
        
        // Es können beim NamedParameterJdbcTemplate mehr Parameter übergeben werden, 
        // als im SQL-Skript vorhanden sind.
        // Es werden alle Parameter aus dem originalen http-Aufruf übergeben.
        // Achtung: Es wird immer ein String übergeben. Somit muss man in der
        // SQL-Query korrekt casten. Man könnte auch die Parameter schlauer
        // machen, z.B. StringInputParam.bfsnr=2601 oder IntegerInputParam.x=2600000.
        // Und in der Applikation den korrekten Datentyp herstellen.
        Map<String,Object> paramMap = new HashMap<>();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue());
        }

        // SQL-Query ausführen und Input-XML für XSL-Transformation erzeugen.
        String xmlResult = jdbcTemplate.queryForObject(stmt, paramMap, String.class);
        logger.debug(xmlResult);

        // Notwendige Resourcen in das temporäre (pro Report) Verzeichnis kopieren.
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:fonts/*.ttf");
        for (Resource resource : resources) {
            copyResource(resource, outputDirectory);
        }
        
        copyResource("fop.xconf", outputDirectory);
        File xsltFile = copyResource(documentName+".xsl", outputDirectory);
        
        // Transformation: xml -> fo
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(xsltFile));


        
        return null;
        
        
    }
    
    private File copyResource(Resource resource, Path outputFolder) throws IOException {
        Path outFile = Paths.get(outputFolder.toFile().getAbsolutePath(), resource.getFilename());
        InputStream is = resource.getInputStream();
        Files.copy(is, outFile, StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(is);
        logger.debug("copyResource: " + outFile.toString());
        return outFile.toFile();
    }
    
    private File copyResource(String resourceName, Path outputFolder) throws IOException {
        Path outFile = Paths.get(outputFolder.toFile().getAbsolutePath(), resourceName);
        InputStream is = this.getClass().getResourceAsStream("/"+resourceName);
        Files.copy(is, outFile, StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(is);
        logger.debug("copyResource: " + outFile.toString());
        return outFile.toFile();
    }

    
}
