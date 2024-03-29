package ch.so.agi.dox43;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fopconf.Fop;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DocxGenerator {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.templateDirectory}")
    private String templateDirectory;

    private PdfConverter pdfConverter;

    @Value("${app.workDirectory}")
    private String workDirectory;

    @Value("${app.folderPrefix}")
    private String folderPrefix;
    
    @Value("${app.losslessCompression}")
    private boolean losslessCompression;
        
    public DocxGenerator(PdfConverter pdfConverter) {
        this.pdfConverter = pdfConverter;
    }
    
    public byte[] generateFileFromTemplate(String format, String docTemplate, Map<String,String> docVariables) throws Exception {
        File templateFile = Paths.get(templateDirectory, docTemplate).toFile();

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateFile);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        HashMap<String, String> genericVariables = new HashMap<>();
        HashMap<String, String> wmsVariables = new HashMap<>();
        for (Map.Entry<String, String> entry : docVariables.entrySet()) {
            String paramName = entry.getKey().substring(entry.getKey().indexOf(".")+1);
            if (!entry.getKey().toLowerCase().contains("wmsinputparam.")) {
                genericVariables.put(paramName, entry.getValue());
            } else if (entry.getKey().toLowerCase().contains("wmsinputparam.")) {                
                wmsVariables.put(paramName, entry.getValue());   
            }
        }
        
        // replace "simple" variables
        VariablePrepare.prepare(wordMLPackage);
        documentPart.variableReplace(genericVariables);
        
        // replace image
        for (Map.Entry<String, String> entry : wmsVariables.entrySet()) {
           String imageUrl = entry.getValue();
           try(InputStream inputStream = new URL(imageUrl).openStream()) {
               BinaryPart imagePart = (BinaryPart) wordMLPackage.getParts().get(new PartName("/word/media/"+entry.getKey()+".png"));
               imagePart.setBinaryData(inputStream);
           } catch (IOException e) {
               e.printStackTrace();
               logger.error("Error downloading the image: " + e.getMessage());
           }
        }

        if (format.equalsIgnoreCase(AppConstants.PARAM_CONST_PDF)) {
            Path tmpFolder = Files.createTempDirectory(Paths.get(workDirectory), folderPrefix);
            File tmpFile = Paths.get(tmpFolder.toFile().getAbsolutePath(), "document.docx").toFile();
            wordMLPackage.save(tmpFile);
                        
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            return pdfConverter.convert(tmpFile, baos);                  
        } else if (format.equalsIgnoreCase(AppConstants.PARAM_CONST_DOCX)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            wordMLPackage.save(outputStream);
            
            return outputStream.toByteArray();            
        } else {
            throw new IllegalArgumentException("unsupported format <"+format+">");
        }
    }    
}
