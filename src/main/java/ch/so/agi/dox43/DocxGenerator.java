package ch.so.agi.dox43;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.IdentityPlusMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DocxGenerator {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.templateDirectory}")
    private String templateDirectory;

    //private static final String TEMPLATE_NAME = "template_frutiger_v3.docx";
    private static final String TEMPLATE_NAME = "template_frutiger_bild_V2.docx";
    
    public byte[] generateDocxFileFromTemplate(String docTemplate, Map<String,String> docVariables) throws Exception {
        File templateFile = Paths.get(templateDirectory, docTemplate).toFile();

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateFile);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        HashMap<String, String> genericVariables = new HashMap<>();
        for (Map.Entry<String, String> entry : docVariables.entrySet()) {
            if (!entry.getKey().toLowerCase().contains("wmsinputparam.")) {
                String paramName = entry.getKey().substring(entry.getKey().indexOf(".")+1);
                genericVariables.put(paramName, entry.getValue());
            } 
        }

        
        VariablePrepare.prepare(wordMLPackage);
        documentPart.variableReplace(docVariables);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wordMLPackage.save(outputStream);

        return outputStream.toByteArray();
    }
    
    
    public byte[] generateDocxFileFromTemplate_1() throws Exception {
        InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream(TEMPLATE_NAME);

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        VariablePrepare.prepare(wordMLPackage);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("firstName", "Stefan");
        variables.put("lastName", "Ziegler");
        variables.put("salutation", "Herr");
        variables.put("message", "Top of the Pops.");

        documentPart.variableReplace(variables);
        
        
        System.out.println("**11111111111111111111111111");
        System.out.println("**"+wordMLPackage.getParts().getParts());
        
        BinaryPart imagePart = (BinaryPart) wordMLPackage.getParts().get(new PartName("/word/media/image1.png"));
        InputStream newImageStream = this.getClass().getClassLoader().getResourceAsStream("replacement.png");//new FileInputStream(new java.io.File("new_image.png"));
        imagePart.setBinaryData(newImageStream);

        
        

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        wordMLPackage.save(outputStream);

        return outputStream.toByteArray();
      }

    public void generatePdfFileFromTemplate() throws Exception {
        InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream(TEMPLATE_NAME);

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        VariablePrepare.prepare(wordMLPackage);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("firstName", "Stefan");
        variables.put("lastName", "Ziegler");
        variables.put("salutation", "Herr");
        variables.put("message", "Top of the Pops.");
        documentPart.variableReplace(variables);

        // https://stackoverflow.com/questions/26598730/how-to-save-images-from-a-word-document-in-docx4j
        // Enzippen und schauen, wie das Bild im Zip heisst. Der Name des Bildes müsste dann der Parametername 
        // in der API sein.
        BinaryPart imagePart = (BinaryPart) wordMLPackage.getParts().get(new PartName("/word/media/image1.png"));
        InputStream newImageStream = this.getClass().getClassLoader().getResourceAsStream("replacement.png");//new FileInputStream(new java.io.File("new_image.png"));
        imagePart.setBinaryData(newImageStream);

        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        wordMLPackage.save(outputStream);

        try {

            String outputfilepath = "/Users/stefan/tmp/message.pdf";
            FileOutputStream os = new FileOutputStream(outputfilepath);

//            Mapper fontMapper = new IdentityPlusMapper();
            Mapper fontMapper = new BestMatchingMapper();
            wordMLPackage.setFontMapper(fontMapper);
            
            // Um docx4j-konforme Font-Namen herauszufinden.
            System.out.println("***********");
            System.out.println(PhysicalFonts.getPhysicalFonts());
            System.out.println("***********");
            
            // May be different on Linux (in Docker container).
            // Oder so: https://github.com/plutext/docx4j/blob/master/docx4j-samples-resources/src/main/resources/fop-substitutions.xml
            PhysicalFont font = PhysicalFonts.get("frutiger lt com roman");
//            PhysicalFont font = PhysicalFonts.get("frutiger lt com 55 roman");
            PhysicalFont fontBold = PhysicalFonts.get("frutiger lt com black");
            PhysicalFont fontItalic = PhysicalFonts.get("frutiger lt com italic");
            PhysicalFont fontBoldItalic = PhysicalFonts.get("frutiger lt com black italic");
            System.out.println("*************************" + font.getName() + "************");
            System.out.println("*************************" + font.getEmbeddedURI() + "************");

//            PhysicalFont font = PhysicalFonts.getPhysicalFont(wordMLPackage, "Cadastra");
//            fontMapper.put("Frutiger LT Com 55 Roman", font);
            fontMapper.registerRegularForm("Frutiger LT Com 55 Roman", font);
            fontMapper.registerBoldForm("Frutiger LT Com 55 Roman", fontBold);
            fontMapper.registerItalicForm("Frutiger LT Com 55 Roman", fontItalic);
            fontMapper.registerBoldItalicForm("Frutiger LT Com 55 Roman", fontBoldItalic);
                        
            //PhysicalFont font2 = PhysicalFonts.getPhysicalFonts().get("Arial");
            PhysicalFont font2 = PhysicalFonts.get("Arial");
//            System.out.println("*************************" + font2 + "************");
            fontMapper.put("Calibri", font2);
            
//            final FOSettings foSettings = Docx4J.createFOSettings();
//            foSettings.setWmlPackage(wordMLPackage);

            
//            FOSettings foSettings = Docx4J.createFOSettings();
//            foSettings.setfo
            
//            FOSettings foSettings = new FOSettings(wordMLPackage);
//            foSettings.setApacheFopMime(FOSettings.INTERNAL_FO_MIME);
//            foSettings.setFoDumpFile(new java.io.File("/Users/stefan/tmp/fo.fo"));
            
//            FileOutputStream fos = new FileOutputStream(new java.io.File("/Users/stefan/tmp/output.xslfo"));
//            Docx4J.toFO(foSettings, fos, Docx4J.FLAG_EXPORT_PREFER_XSL);
            
            Docx4J.toPDF(wordMLPackage,os);
            os.flush();
            os.close();
        } catch (Throwable e) {

            e.printStackTrace();
        } 


      }

    
}
