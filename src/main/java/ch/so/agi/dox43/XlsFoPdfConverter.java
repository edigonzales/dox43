package ch.so.agi.dox43;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Paths;

import org.docx4j.Docx4J;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.Mapper;

//@ConditionalOnProperty(name = "app.pdfConverter", havingValue = "true")
@ConditionalOnProperty(name = "app.pdfConverter", havingValue = "xlsfo", matchIfMissing = false)
@Service
public class XlsFoPdfConverter implements PdfConverter {

    private Mapper fontMapper = null;
    
    @Override
    public byte[] convert(File inputFile, OutputStream outputStream) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputFile);

        // TODO: irgendwie parametrisierbar machen. Mit Mapping-Datei?
        // Prüfen, ob das unter Linux (im verwendeten Dockerimage) funktioniert?
        if (true) {
            if (fontMapper == null) {
                Mapper fontMapper = new BestMatchingMapper();
                wordMLPackage.setFontMapper(fontMapper);
    
                PhysicalFont font = PhysicalFonts.get("frutiger lt com roman");
                PhysicalFont fontBold = PhysicalFonts.get("frutiger lt com black");
                PhysicalFont fontItalic = PhysicalFonts.get("frutiger lt com italic");
                PhysicalFont fontBoldItalic = PhysicalFonts.get("frutiger lt com black italic");
                fontMapper.registerRegularForm("Frutiger LT Com 55 Roman", font);
                fontMapper.registerBoldForm("Frutiger LT Com 55 Roman", fontBold);
                fontMapper.registerItalicForm("Frutiger LT Com 55 Roman", fontItalic);
                fontMapper.registerBoldItalicForm("Frutiger LT Com 55 Roman", fontBoldItalic);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        Docx4J.toPDF(wordMLPackage, baos);
        baos.flush();
        baos.close();

        return baos.toByteArray();

    }

}
