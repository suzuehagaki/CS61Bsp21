package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private final String sha1;
    private final File fullFilePath;
    private final byte[] content;

    public Blob(File fullFilePath, String sha1, byte[] content) {
        this.fullFilePath = fullFilePath;
        this.sha1 = sha1;
        this.content = content;
    }

    public File getFullFilePath() {
        return this.fullFilePath;
    }

    public String getSHA1() {
        return this.sha1;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void writeBlob() {
        writeContents(this.fullFilePath, (Object) this.content);
    }

}
