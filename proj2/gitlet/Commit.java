package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author suzue
 */
public class Commit implements Serializable {
    /*
      List all instance variables of the Commit class here with a useful
      comment above them describing what that variable represents and how that
      variable is used. We've provided one example for `message`.
     */

    /** timestamp of this Commit. */
    private final String timestamp;

    /** The message of this Commit. */
    private final String message;

    private final Map<String, String> map;

    /** parents' sha1ID of this Commit. */
    private final String firstParent;
    private String secondParent;

    public Commit(Date date, String message, String parent) {
        this.timestamp = dateToTimeStamp(date);
        this.message = message;
        this.firstParent = parent;
        this.map = new TreeMap<>();
    }

    public Commit(Commit firstParent, String message) {
        this.timestamp = dateToTimeStamp(new Date());
        this.message = message;
        this.firstParent = firstParent.generateSHA1();
        this.map = firstParent.getMap();
    }

    public Commit(Commit firstParent, String secondParentID, String message) {
        this.timestamp = dateToTimeStamp(new Date());
        this.message = message;
        this.firstParent = firstParent.generateSHA1();
        this.map = firstParent.getMap();
        this.secondParent = secondParentID;
    }

    public void addFile(String fileName, String sha1) {
        this.map.put(fileName, sha1);
    }

    public void removeFile(String fileName) {
        this.map.remove(fileName);
    }

    public String generateSHA1() {
        if (this.secondParent != null && this.firstParent != null) {
            return sha1(this.timestamp, this.message,
                    this.map.toString(), this.firstParent, this.secondParent);
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

    public boolean containFile(String fileName) {
        return this.map.containsKey(fileName);
    }

    public String getSHA1(String fileName) {
        return this.map.get(fileName);
    }

    public Set<String> getFiles() {
        return this.map.keySet();
    }

    public void dumpCommit() {
        System.out.println("===");
        System.out.println("commit " + this.generateSHA1());
        if (this.secondParent != null) {
            System.out.println("Merge: " + firstParent.substring(0, 7) + " "
                    + secondParent.substring(0, 7));
        }
        System.out.println("Date: " + this.getTimestamp());
        System.out.println(this.message);
        System.out.println();
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }



}
