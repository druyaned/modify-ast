package druyaned.modifyast.enexrint;

import druyaned.modifyast.enexrint.arg.ArgumentExtractor;
import druyaned.modifyast.enexrint.arg.ArgumentExtractors;

public class ParseFormat {
    
    private static final int EXTRACTOR_BUFFER_SIZE = (1 << 11);
    
    private String printFormat;
    private ArgumentExtractor[] extractors;
    
    public ArgumentExtractor[] getExtractors() {
        return extractors;
    }
    
    public String getPrintFormat() {
        return printFormat;
    }
    
    public ParseFormat run(String format) {
        char[] formatArray = format.toCharArray();
        ArgumentExtractor[] extractorBuffer = new ArgumentExtractor[EXTRACTOR_BUFFER_SIZE];
        int j = 0; // index of parents and children
        char[] printFormatArray = new char[formatArray.length];
        int k = 0; // index of printFormatArray
        if (formatArray.length > 0) {
            printFormatArray[k++] = formatArray[0];
        }
        int parentBegin = -1;
        int i = 1;
        while (i < formatArray.length) {
            if (isParentBegin(formatArray[i - 1], formatArray[i])) {
                parentBegin = i - 1;
                printFormatArray[k++] = formatArray[i++];
            } else if (isParentEnd(formatArray[i]) && parentBegin != -1) {
                printFormatArray[k++] = formatArray[i++];
                int parentEnd = i;
                String parent = format.substring(parentBegin, parentEnd);
                String child = null;
                if (i < formatArray.length && formatArray[i] == '.') {
                    printFormatArray[k++] = formatArray[i++];
                    if (
                            i < formatArray.length
                            && Character.isJavaIdentifierStart(formatArray[i])
                    ) {
                        int childBegin = i;
                        printFormatArray[k++] = formatArray[i++];
                        while (
                                i < formatArray.length
                                && Character.isJavaIdentifierPart(formatArray[i])
                        ) {
                            printFormatArray[k++] = formatArray[i++];
                        }
                        int childEnd = i;
                        child = format.substring(childBegin, childEnd);
                    }
                }
                if (ArgumentExtractors.contains(parent)) {
                    int count = i - parentBegin;
                    k -= count;
                    printFormatArray[k++] = '%';
                    printFormatArray[k++] = 's';
                    extractorBuffer[j++] = ArgumentExtractors.create(parent, child);
                }
                parentBegin = -1;
            } else {
                printFormatArray[k++] = formatArray[i++];
            }
        }
        printFormat = new String(printFormatArray, 0, k);
        extractors = new ArgumentExtractor[j];
        System.arraycopy(extractorBuffer, 0, extractors, 0, j);
        return this;
    }
    
    private boolean isParentBegin(char fst, char snd) {
        return fst == '$' && snd == '{';
    }
    
    private boolean isParentEnd(char ch) {
        return ch == '}';
    }
    
}
