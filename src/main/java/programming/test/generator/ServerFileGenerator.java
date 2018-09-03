package programming.test.generator;

import programming.test.pojo.FileInfo;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ServerFileGenerator {
    public static final String SERVER_FILENAME = "server.xml";
    private static final String FILES_TAG = "FILES";
    private static final String FILE_TAG = "FILE";
    private static final String DOCUMENT_NAME_TAG = "DOCUMENT_NAME";
    private static final String VERSION_TAG = "VERSION";
    private static final String LENGTH_TAG = "LENGTH";
    private static final String PATH_TO_FILE_TAG = "PATH_TO_FILE";

    private XMLOutputFactory xof = XMLOutputFactory.newInstance();
    private XMLStreamWriter xtw = null;

    /**
     * Initialize the file generator.
     *
     * @param directory Path where the file will be generated. For the file name see : {@link ServerFileGenerator#SERVER_FILENAME}
     * @throws IOException        File error.
     * @throws XMLStreamException XML stream error.
     */
    public void initFile(String directory) throws IOException, XMLStreamException {
        final Path serverFilePath = Paths.get(directory, SERVER_FILENAME);
        xtw = xof.createXMLStreamWriter(new FileOutputStream(serverFilePath.toString()), "UTF-8");
        xtw.writeStartDocument("utf-8", "1.0");
        xtw.writeStartElement(FILES_TAG);
    }

    /**
     * Write the file information in the correct format for the server file.
     *
     * @param pathToFile Directory where file was read.
     * @param fileInfo        File information you want to write.
     * @throws XMLStreamException XML stream error.
     */
    public void writeAFileInfoInServerFile(String pathToFile, FileInfo fileInfo) throws XMLStreamException {
        writeAFileInfoInServerFile(pathToFile, fileInfo.getDocumentName(), fileInfo.getVersion(), fileInfo.getData().length());
    }

    private void writeAFileInfoInServerFile(String pathToFile, String documentName, String version, long length) throws XMLStreamException {
        xtw.writeStartElement(FILE_TAG);
        xtw.writeStartElement(DOCUMENT_NAME_TAG);
        xtw.writeCharacters(documentName);
        xtw.writeEndElement();

        xtw.writeStartElement(VERSION_TAG);
        xtw.writeCharacters(version);
        xtw.writeEndElement();

        xtw.writeStartElement(LENGTH_TAG);
        xtw.writeCharacters(Long.toString(length));
        xtw.writeEndElement();

        xtw.writeStartElement(PATH_TO_FILE_TAG);
        xtw.writeCharacters(pathToFile);
        xtw.writeEndElement();

        xtw.writeEndElement(); // FILE_TAG
    }


    public void close() throws XMLStreamException {
        xtw.writeEndElement();
        xtw.writeEndDocument();
        xtw.flush();
        xtw.close();
    }

}
