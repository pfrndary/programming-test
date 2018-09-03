package programming.test;

import programming.test.pojo.ConsumerWithException;
import programming.test.pojo.FileInfo;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public final class InputFilesReader {

    private static final String DOCUMENT_NAME_TAG = "DOCUMENT_NAME";
    private static final String VERSION_TAG = "VERSION";
    private static final String DATA_TAG = "DATA";
    private static final String FILE_TAG = "FILE";

    private XMLInputFactory factory;

    public static void main(String... s) throws FileNotFoundException, XMLStreamException {
        InputFilesReader inputFilesReader = new InputFilesReader();
        inputFilesReader.init();
        inputFilesReader.read("C:\\var\\mnt\\fserver\\mirax\\processor\\file.xml", System.out::println);
    }

    void init() {
        factory = XMLInputFactory.newInstance();
    }

    void read(String filename, ConsumerWithException<FileInfo, XMLStreamException> consumer) throws FileNotFoundException, XMLStreamException {
        final XMLStreamReader streamReader = factory.createXMLStreamReader(filename, new FileInputStream(filename));
        while (streamReader.hasNext()) {
            System.out.println(filename);
            streamReader.next();
            if (streamReader.getEventType() == XMLStreamConstants.START_ELEMENT && FILE_TAG.equals(streamReader.getName().toString())) {
                final FileInfo fileInfo = extractFileInfo(streamReader);
                consumer.accept(fileInfo);
            }
        }
    }


    private FileInfo extractFileInfo(XMLStreamReader streamReader) throws XMLStreamException {
        String currentTag = null;
        String data = null;
        String version = null;
        String documentName = null;

        while (streamReader.getEventType() != XMLStreamConstants.END_ELEMENT || !streamReader.getName().toString().equals(FILE_TAG)) {
            switch (streamReader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    currentTag = streamReader.getName().toString();
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (DATA_TAG.equals(currentTag)) {
                        data = streamReader.getText();
                    } else if (VERSION_TAG.equals(currentTag)) {
                        version = streamReader.getText();
                    } else if (DOCUMENT_NAME_TAG.equals(currentTag)) {
                        documentName = streamReader.getText();
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    currentTag = null;
                    break;
            }
            streamReader.next();
        }
        return new FileInfo(documentName, version, data);
    }

}
