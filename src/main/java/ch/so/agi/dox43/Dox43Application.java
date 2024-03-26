package ch.so.agi.dox43;

import java.util.HashMap;
import java.util.Map;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sun.star.beans.PropertyValue;

@SpringBootApplication
//@Configuration
@EnableScheduling
public class Dox43Application {
    
//    @Value("${app.losslessCompression}")
//    private boolean losslessCompression;
//
//    @Value("${app.pdfConverter}")
//    private String pdfConverter;

    @Autowired
    PdfConverter pdfConverter;

    
    public static void main(String[] args) {
        SpringApplication.run(Dox43Application.class, args);
    }
    
    
//    @ConditionalOnProperty(name = "app.pdfConverter", havingValue = "xlsfo", matchIfMissing = false)
//    @Bean
//    PdfConverter xlsFoPdfConverter() {
//        System.out.println("****1"+pdfConverter);
//        System.out.println("********************************* 11111");
//        return new XlsFoPdfConverter();
//    } 
//
//    @ConditionalOnProperty(name = "app.pdfConverter", havingValue = "libreoffice", matchIfMissing = false)
//    @Bean
//    PdfConverter libreOfficePdfConverter(OfficeManager officeManager) {
//        return new LibreOfficePdfConverter(documentConverter(officeManager));
//    }
//
//    @ConditionalOnProperty(name = "app.pdfConverter", havingValue = "libreoffice", matchIfMissing = false)
//    @Bean(name = "libreOfficePdfConverter")
//    DocumentConverter documentConverter(OfficeManager officeManager) {
//        
//        System.out.println("********************************* 22222");
//        
//        // https://github.com/jodconverter/jodconverter/issues/160
//        // https://forum.openoffice.org/en/forum/viewtopic.php?f=44&t=1804&start=0
//        // https://github.com/jodconverter/jodconverter/blob/master/jodconverter-local/src/main/java/org/jodconverter/local/LocalConverter.java#L90
//        // https://github.com/jodconverter/jodconverter-samples/blob/main/samples/spring-boot-rest/src/main/java/org/jodconverter/sample/rest/ConverterController.java#L142
//        PropertyValue pdfFilterData[] = new PropertyValue[2];
//        pdfFilterData[0] = new PropertyValue();
//        pdfFilterData[0].Name = "SelectPdfVersion";
//        pdfFilterData[0].Value = Integer.valueOf(1);
//
//        if (losslessCompression) {
//            pdfFilterData[1] = new PropertyValue();
//            pdfFilterData[1].Name = "UseLosslessCompression";
//            pdfFilterData[1].Value = Boolean.TRUE;                
//        } else {
//            pdfFilterData[1] = new PropertyValue();
//            pdfFilterData[1].Name = "Quality";
//            pdfFilterData[1].Value = Integer.valueOf(95); // default 90
//        }
//        
//        Map<String, Object> storeFilterDataProperties = new HashMap<>();
//        storeFilterDataProperties.put("FilterData", pdfFilterData);
//
//        DocumentConverter converter =
//                LocalConverter.builder()
//                        .officeManager(officeManager)
////                        .loadProperties(loadProperties)
//                        .storeProperties(storeFilterDataProperties)
//                        .build();
//        
//        return converter;
//    }
}
