package ch.so.agi.dox43;

import java.io.File;
import java.io.OutputStream;

public interface PdfConverter {
    public byte[] convert(File inputFile, OutputStream outputStream) throws Exception;
}
