package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private final String sha1;
    private final byte[] content;

    public Blob(String sha1, byte[] content) {
        this.sha1 = sha1;
        this.content = content;
    }

    public String getSHA1() {
        return this.sha1;
    }

    public void writeBlob(File path) {
        writeContents(path, (Object) this.content);
    }


}
