package ch.so.agi.dox43;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.springframework.stereotype.Service;

public class LibreOfficePdfConverter implements PdfConverter {

    private DocumentConverter documentConverter;
    
    public LibreOfficePdfConverter(DocumentConverter documentConverter) {
        this.documentConverter = documentConverter;
    }
    
    @Override
    public byte[] convert(File inputFile, OutputStream outputStream) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension(AppConstants.PARAM_CONST_PDF);
        documentConverter.convert(new FileInputStream(inputFile)).to(baos).as(targetFormat).execute();

        return baos.toByteArray();            
    }

}
