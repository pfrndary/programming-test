package programming.test.pojo;

public final class FileInfo {

    private final String documentName;
    private final String version;
    private final String data;

    public FileInfo(String documentName, String version, String data) {
        this.documentName = documentName;
        this.version = version;
        this.data = data;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getVersion() {
        return version;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "documentName='" + documentName + '\'' +
                ", version='" + version + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
