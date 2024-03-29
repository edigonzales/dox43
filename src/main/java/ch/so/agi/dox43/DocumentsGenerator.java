package ch.so.agi.dox43;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

@Service
public class DocumentsGenerator {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JdbcTemplateFactory jdbcTemplateFactory;
    
    @Value("${app.configDirectory}")
    private String configDirectory;

    @Value("${app.workDirectory}")
    private String workDirectory;

    @Value("${app.folderPrefix}")
    private String folderPrefix;

    public DocumentsGenerator(JdbcTemplateFactory jdbcTemplateFactory) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
    }

    public byte[] generateFileFromSql(String documentName, Map<String,String> queryParameters) throws IOException, SaxonApiException, SAXException {
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
        String inputXml = jdbcTemplate.queryForObject(stmt, paramMap, String.class);
        logger.debug(inputXml);

        // Notwendige Resourcen in das temporäre (pro Report) Verzeichnis kopieren.
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:fonts/*.ttf");
        for (Resource resource : resources) {
            copyResource(resource, outputDirectory);
        }
        
        File fopxconfFile = copyResource("fop.xconf", outputDirectory);
        File xsltFile = copyResource(documentName+".xsl", outputDirectory);
        
        // Transformation: xml -> fo
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(xsltFile));

        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new StringReader(inputXml)));
        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);

        // fo -> pdf
        File pdfFile = Paths.get(outputDirectory.toString(), documentName+".pdf").toFile();
        ByteArrayOutputStream outPdf = new ByteArrayOutputStream();

        synchronized(this) {
            FopFactory fopFactory = FopFactory.newInstance(fopxconfFile);
//            OutputStream outPdf = new BufferedOutputStream(new FileOutputStream(pdfFile)); 
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outPdf);

            trans.setDestination(new SAXDestination(fop.getDefaultHandler()));
            trans.transform();
            outPdf.close();
            trans.close();
        }
        
//        return pdfFile;
        return outPdf.toByteArray();
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
