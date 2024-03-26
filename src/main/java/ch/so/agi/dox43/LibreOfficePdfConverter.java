package ch.so.agi.dox43;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sun.star.beans.PropertyValue;

@ConditionalOnProperty(name = "app.pdfConverter", havingValue = "libreoffice")
@Service
public class LibreOfficePdfConverter implements PdfConverter {

    @Value("${app.losslessCompression}")
    private boolean losslessCompression;

    private OfficeManager officeManager;
    
    private DocumentConverter documentConverter;
    
    public LibreOfficePdfConverter(OfficeManager officeManager) {
        this.officeManager = officeManager;
        
        // https://github.com/jodconverter/jodconverter/issues/160
        // https://forum.openoffice.org/en/forum/viewtopic.php?f=44&t=1804&start=0
        // https://github.com/jodconverter/jodconverter/blob/master/jodconverter-local/src/main/java/org/jodconverter/local/LocalConverter.java#L90
        // https://github.com/jodconverter/jodconverter-samples/blob/main/samples/spring-boot-rest/src/main/java/org/jodconverter/sample/rest/ConverterController.java#L142
        PropertyValue pdfFilterData[] = new PropertyValue[2];
        pdfFilterData[0] = new PropertyValue();
        pdfFilterData[0].Name = "SelectPdfVersion";
        pdfFilterData[0].Value = Integer.valueOf(1);

        if (losslessCompression) {
            pdfFilterData[1] = new PropertyValue();
            pdfFilterData[1].Name = "UseLosslessCompression";
            pdfFilterData[1].Value = Boolean.TRUE;
        } else {
            pdfFilterData[1] = new PropertyValue();
            pdfFilterData[1].Name = "Quality";
            pdfFilterData[1].Value = Integer.valueOf(95); // default 90
        }

        Map<String, Object> storeFilterDataProperties = new HashMap<>();
        storeFilterDataProperties.put("FilterData", pdfFilterData);

        documentConverter = LocalConverter.builder()
                .officeManager(officeManager)
                //.loadProperties(loadProperties)
                .storeProperties(storeFilterDataProperties).build();

    }
    
    @Override
    public byte[] convert(File inputFile, OutputStream outputStream) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension(AppConstants.PARAM_CONST_PDF);
        documentConverter.convert(new FileInputStream(inputFile)).to(baos).as(targetFormat).execute();

        return baos.toByteArray();            
    }

}
