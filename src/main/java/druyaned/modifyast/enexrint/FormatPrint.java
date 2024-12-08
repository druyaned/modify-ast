package druyaned.modifyast.enexrint;

import druyaned.modifyast.enexrint.arg.ArgumentExtractor;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.APPEND;

public class FormatPrint {
    
    private final String printFormat;
    private final ArgumentExtractor[] extractors;
    private final Object[] args;
    private final Path outputPath;
    
    public FormatPrint(String format, String outputFile) {
        ParseFormat parseFormat = new ParseFormat().run(format);
        printFormat = parseFormat.getPrintFormat();
        extractors = parseFormat.getExtractors();
        args = new Object[extractors.length];
        outputPath = outputFile.isEmpty() ? null : Path.of(outputFile);
    }
    
    public void run(Object target, String methodName) {
        updateArgs(target, methodName);
        String string = String.format(printFormat + "\n", args);
        if (outputPath == null) {
            System.out.print(string);
            return;
        }
        try {
            Files.writeString(outputPath, string, APPEND);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    private void updateArgs(Object target, String methodName) {
        for (int i = 0; i < extractors.length; i++) {
            args[i] = extractors[i].getArg(target, methodName);
        }
    }
    
}
