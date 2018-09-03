package programming.test;

public final class Configuration {

    private final String inputDirectory;
    private final String outputDirectoryClient;
    private final String outputDirectoryServer;

    public Configuration(String inputDirectory, String outputDirectoryClient, String outputDirectoryServer) {
        this.inputDirectory = inputDirectory;
        this.outputDirectoryClient = outputDirectoryClient;
        this.outputDirectoryServer = outputDirectoryServer;
    }

    public String getInputDirectory() {
        return inputDirectory;
    }

    public String getOutputDirectoryClient() {
        return outputDirectoryClient;
    }

    public String getOutputDirectoryServer() {
        return outputDirectoryServer;
    }
}
