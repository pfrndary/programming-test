package programming.test;

import org.apache.log4j.Logger;
import programming.test.generator.ClientFileGenerator;
import programming.test.generator.ServerFileGenerator;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DictionaryGenerator {

    private final static Logger logger = Logger.getLogger(DictionaryGenerator.class);

    private static final String INPUT_FILES_FLAG = "-i";
    private static final String DESTINATION_CLIENT_DIRECTORY_FLAG = "-c";
    private static final String DESTINATION_SERVER_DIRECTORY_FLAG = "-s";
    private static final int NBR_PARAMETERS = 6;

    private String inputDirectoryFiles;
    private String outputDirectoryForServerFile;
    private String outputDirectoryForClientFile;

    public static void main(String... parameters) {
        final DictionaryGenerator dictionaryGenerator = new DictionaryGenerator();
        dictionaryGenerator.validateAndInit(parameters);
        dictionaryGenerator.buildClientAndServerDictionary();
    }

    private void validateAndInit(String[] parameters) {
        // Greater or equal if you want to add extra parameter for the JVM at runtime
        if (parameters.length >= NBR_PARAMETERS) {
            for (int i = 0; i < NBR_PARAMETERS; i++) {
                final String parameter = parameters[i++];
                switch (parameter) {
                    case INPUT_FILES_FLAG:
                        inputDirectoryFiles = parameters[i];
                        break;
                    case DESTINATION_CLIENT_DIRECTORY_FLAG:
                        outputDirectoryForClientFile = parameters[i];
                        break;
                    case DESTINATION_SERVER_DIRECTORY_FLAG:
                        outputDirectoryForServerFile = parameters[i];
                        break;
                    default:
                        stopWithErrorMessageAndUsage("The following flag is not valid " + parameter);
                }
            }
        } else {
            printUsage();
            System.exit(-1);
        }
        validatePathExists();
    }

    private void validatePathExists() {
        final Path pathInputDirectoryFiles = Paths.get(inputDirectoryFiles);
        final Path pathOutputDirectoryForClientFile = Paths.get(outputDirectoryForClientFile);
        final Path pathOutputDirectoryForServerFile = Paths.get(outputDirectoryForServerFile);
        if (!Files.isDirectory(pathInputDirectoryFiles)) {
            stopWithErrorMessageAndUsage("The provided input directory does not exist : " + pathInputDirectoryFiles);
        }
        if (!Files.isDirectory(pathOutputDirectoryForClientFile)) {
            stopWithErrorMessageAndUsage("The provided output directory for the client file does not exist : " + pathOutputDirectoryForClientFile);
        }
        if (!Files.isDirectory(pathOutputDirectoryForServerFile)) {
            stopWithErrorMessageAndUsage("The provided output directory for the server file does not exist : " + pathOutputDirectoryForServerFile);
        }
    }

    private void buildClientAndServerDictionary() {
        final ServerFileGenerator serverFileGenerator = new ServerFileGenerator();
        final ClientFileGenerator clientFileGenerator = new ClientFileGenerator();
        try {
            serverFileGenerator.initFile(outputDirectoryForServerFile);
            clientFileGenerator.initFile(outputDirectoryForClientFile);
        } catch (IOException | XMLStreamException e) {
            logger.error("failed to initialize the output files.", e);
        }

        parseAllFiles(inputDirectoryFiles, serverFileGenerator, clientFileGenerator);

        try {
            serverFileGenerator.close();
            clientFileGenerator.close();
        } catch (XMLStreamException e) {
            logger.error("failed to close the output files.", e);
        }
    }

    private void parseAllFiles(String inputDirectoryFiles, ServerFileGenerator serverFileGenerator, ClientFileGenerator clientFileGenerator) {
        // DirectoryStream is softer for the JVM, we don't load all the files in memory
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(inputDirectoryFiles))) {
            final InputFilesReader inputFilesReader = new InputFilesReader();
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    parseAllFiles(path.toAbsolutePath().toString(), serverFileGenerator, clientFileGenerator);
                    continue;
                }
                inputFilesReader.init();
                final String pathToFile = path.toAbsolutePath().toString();
                try {
                    inputFilesReader.read(pathToFile, f -> {
                        clientFileGenerator.writeAFileInfoInServerFile(f);
                        serverFileGenerator.writeAFileInfoInServerFile(pathToFile, f);
                    });
                } catch (XMLStreamException | FileNotFoundException e) {
                    logger.warn("Filed to parse a file. Skipping : " + pathToFile, e);
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to list a directory. Skipping : " + inputDirectoryFiles, e);
        }
    }

    private void stopWithErrorMessageAndUsage(String s) {
        logger.error(s);
        printUsage();
        System.exit(-1);
    }

    private void printUsage() {
        System.out.println(getClass().getSimpleName() + " " + INPUT_FILES_FLAG + " <inputDirectoryFile> " +
                DESTINATION_CLIENT_DIRECTORY_FLAG + " <clientOutputDirectoryFile> "
                + DESTINATION_SERVER_DIRECTORY_FLAG + " <serverOutputDirectoryFile>");
        System.out.println("\t" + INPUT_FILES_FLAG + "\tpath to the directory where the files are stored");
        System.out.println("\t" + DESTINATION_CLIENT_DIRECTORY_FLAG + "\tpath to the directory where the file client.xml will be saved");
        System.out.println("\t" + DESTINATION_SERVER_DIRECTORY_FLAG + "\tpath to the directory where the file server.xml will be saved");
    }

}
