package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** timestamp of this Commit. */
    private String timestamp;

    /** The message of this Commit. */
    private String message;

    private Map<String, String> map;

    /** parents of this Commit. */
    private String firstParent;
    private String secondParent;


    /* TODO: fill in the rest of this class. */
    public Commit(Date date, String message, String parent) {
        this.timestamp = dateToTimeStamp(date);
        this.message = message;
        this.firstParent = parent;
        this.map = new TreeMap<>();
    }

    public Commit(Commit parent, String message) {
        this.timestamp = dateToTimeStamp(new Date());
        this.message = message;
        this.firstParent = parent.generateSHA1();
        this.map = parent.getMap();
    }

    public void addFile(File file, Blob blob) {
        this.map.put(file.getPath(), blob.getSHA1());
    }

    public String generateSHA1() {
        if (this.secondParent != null && this.firstParent != null) {
            return sha1(this.timestamp, this.message, this.map.toString(), this.firstParent, this.secondParent);
        } else if (this.firstParent != null) {
            return sha1(this.timestamp, this.message, this.map.toString(), this.firstParent);
        }
        return sha1(this.timestamp, this.message, this.map.toString());
    }

    public Map<String, String> getMap() {
        return this.map;
    }

    public String getFirstParent() {
        return this.firstParent;
    }

    public String getSecondParent() {
        return this.secondParent;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean containFile(String filePath) {
        return this.map.containsKey(filePath);
    }

    public String getSHA1(String filePath) {
        return this.map.get(filePath);
    }

    public Set<String> getFiles() {
        return this.map.keySet();
    }

    public void dumpCommit() {
        System.out.println("===");
        System.out.println("commit " + this.generateSHA1());
        System.out.println("Date: " + this.getTimestamp());
        System.out.println(this.getMessage());
        if (this.getSecondParent() != null) {
            System.out.println("Merged development into master.");
        }
        System.out.println();
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }



}
