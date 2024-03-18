package ch.so.agi.dox43;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.IdentityPlusMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class DocxGenerator {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String TEMPLATE_NAME = "template_frutiger_v3.docx";
    
    public byte[] generateDocxFileFromTemplate() throws Exception {
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

            Docx4J.toPDF(wordMLPackage,os);
            os.flush();
            os.close();
        } catch (Throwable e) {

            e.printStackTrace();
        } 


      }

    
}
