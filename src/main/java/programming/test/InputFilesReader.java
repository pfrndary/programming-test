package programming.test;

import org.apache.log4j.Logger;
import programming.test.pojo.ConsumerWithException;
import programming.test.pojo.FileInfo;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

final class InputFilesReader {

    private final static Logger logger = Logger.getLogger(InputFilesReader.class);

    private static final String DOCUMENT_NAME_TAG = "DOCUMENT_NAME";
    private static final String VERSION_TAG = "VERSION";
    private static final String DATA_TAG = "DATA";
    private static final String FILE_TAG = "FILE";

    private XMLInputFactory factory;

    void init() {
        factory = XMLInputFactory.newInstance();
    }

    /**
     * Read and consume the data read.
     *
     * @param absolutePath Absolute path to the xml file.
     * @param consumer     A lambda expression you can use to specify what you want to do with the data.
     * @throws FileNotFoundException The file does not exists.
     * @throws XMLStreamException    XML error.
     */
    void read(String absolutePath, ConsumerWithException<FileInfo, XMLStreamException> consumer) throws FileNotFoundException, XMLStreamException {
        final XMLStreamReader streamReader = factory.createXMLStreamReader(absolutePath, new FileInputStream(absolutePath));
        while (streamReader.hasNext()) {
            logger.info("Reading " + absolutePath);
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

        // Parse until we find the closed FILE_TAG
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
