package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The blobs which contain files' content. */
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    /** The commits which maintain versions. */
    public static final File COMMITS = join(GITLET_DIR, "commits");
    /** Area to hold staged files. */
    public static final File STAGE_AREA = join(GITLET_DIR, "StagingArea");
    public static final File ADD_AREA = join(STAGE_AREA, "AddingArea");
    public static final File REMOVE_AREA = join(STAGE_AREA, "RemovingArea");
    public static final File BRANCHES = join(COMMITS, "branches");
    public static final File HEAD = join(COMMITS, "head");
    public static final File MASTER = join(BRANCHES, "master");
    public static final File SHA1COMMITS = join(COMMITS, "sha1commits");

    /* TODO: fill in the rest of this class. */

    /** init .gitlet directory if it does not exist. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        try {
            BLOBS.mkdir();
            COMMITS.mkdir();
            SHA1COMMITS.mkdir();
            STAGE_AREA.mkdir();
            ADD_AREA.mkdir();
            REMOVE_AREA.mkdir();
            BRANCHES.mkdir();
            HEAD.createNewFile();
            MASTER.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Commit commit = new Commit(new Date(0), "initial commit", null);
        writeObject(HEAD, MASTER);
        writeObject(MASTER, commit);
        writeObject(join(SHA1COMMITS, commit.generateSHA1()), commit);
    }

    public static void add(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        byte[] content = readContents(file);
        String sha1 = sha1((Object) content);
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        if (commit.getMap().containsKey(file.getPath()) && commit.getMap().get(file.getPath()).equals(sha1)) {
            if (join(ADD_AREA, fileName).exists()) {
                join(ADD_AREA, fileName).delete();
            }
            return;
        }
        Blob b = new Blob(file, sha1, content);
        writeObject(join(ADD_AREA,  fileName), b);
    }

    public static void commit(String message) {

        if (Objects.requireNonNull(ADD_AREA.listFiles()).length == 0
                && Objects.requireNonNull(REMOVE_AREA.listFiles()).length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }

        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        Commit newCommit = new Commit(commit, message);

        for (File file : Objects.requireNonNull(ADD_AREA.listFiles())) {
            Blob blob = readObject(file, Blob.class);
            String formerBlob =  newCommit.getMap().get(blob.getFullFilePath().getPath());
            if (formerBlob == null) {
                newCommit.addFile(blob.getFullFilePath(), blob);
            } else if (!formerBlob.equals(blob.getSHA1())) {
                newCommit.removeFile(blob.getFullFilePath());
                newCommit.addFile(blob.getFullFilePath(), blob);
            }
            writeObject(join(BLOBS, blob.getSHA1()), blob);
        }
        for (File file : Objects.requireNonNull(REMOVE_AREA.listFiles())) {
            Blob blob = readObject(file, Blob.class);
            newCommit.addFile(blob.getFullFilePath(), blob);
        }

        cleanStagingArea();

        writeObject(HEAD, head);
        writeObject(head, newCommit);
        writeObject(join(SHA1COMMITS, newCommit.generateSHA1()), newCommit);

    }

    public static void remove(String fileName) {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        File file = join(ADD_AREA, fileName);
        if (!file.exists()) {
            System.out.println("No reason to remove the file.");
            return;
        }
        Blob blob = readObject(file, Blob.class);
        if (!commit.getMap().containsKey(blob.getFullFilePath().getPath())) {
            System.out.println("No reason to remove the file.");
            return;
        }

        file.delete();
        if (join(CWD, fileName).exists()) {
            join(CWD, fileName).delete();
        }
        writeObject(join(REMOVE_AREA, fileName), blob);
    }

    public static void log() {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        while (true) {
            commit.dumpCommit();
            if (commit.getFirstParent() == null) {
                break;
            }
            commit = readObject(join(SHA1COMMITS, commit.getFirstParent()), Commit.class);
        }
    }

    public static void globalLog() {
        for (File file : Objects.requireNonNull(SHA1COMMITS.listFiles())) {
            Commit commit = readObject(file, Commit.class);
            commit.dumpCommit();
        }
    }

    public static void find(String message) {
        int count = 0;
        for (File file : Objects.requireNonNull(SHA1COMMITS.listFiles())) {
            Commit commit = readObject(file, Commit.class);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.generateSHA1());
                count += 1;
            }
        }
        if (count == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);

        System.out.println("=== Branches ===");
        for (File file : Objects.requireNonNull(BRANCHES.listFiles())) {
            if (head.equals(file)) {
                System.out.println("*" + file.getName());
            } else {
                System.out.println(file.getName());
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (File file : Objects.requireNonNull(ADD_AREA.listFiles())) {
            System.out.println(file.getName());
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (File file : Objects.requireNonNull(REMOVE_AREA.listFiles())) {
            System.out.println(file.getName());
        }
        System.out.println();

        // TODO:
        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println();

        // TODO:
        System.out.println("=== Untracked Files ===");

        System.out.println();
    }

    public static void checkoutFile(String fileName) {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        if (!commit.containFile(join(CWD, fileName).getPath())) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String sha1 = commit.getSHA1(join(CWD, fileName).getPath());
        Blob blob = readObject(join(BLOBS, sha1), Blob.class);
        blob.writeBlob();
    }

    public static void checkoutFile(String sha1Commit, String fileName) {
        if (!join(SHA1COMMITS, sha1Commit).exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = readObject(join(SHA1COMMITS, sha1Commit), Commit.class);
        if (!commit.containFile(join(CWD, fileName).getPath())) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String sha1 = commit.getSHA1(join(CWD, fileName).getPath());
        Blob blob = readObject(join(BLOBS, sha1), Blob.class);
        blob.writeBlob();
    }

    public static void checkout(String branchName) {
        File branch = join(BRANCHES, branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        File head = readObject(HEAD, File.class);
        if (branch.equals(head)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit headCommit = readObject(head, Commit.class);
        Commit commit = readObject(branch, Commit.class);
        for (File file : Objects.requireNonNull(CWD.listFiles())) {
            if (!headCommit.containFile(file.getPath())) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        headCommit.clearFiles();
        for (String s : commit.getFiles()) {
            File temp = join(BLOBS, commit.getSHA1(s));
            Blob blob = readObject(temp, Blob.class);
            blob.writeBlob();
        }
        cleanStagingArea();

    }

    public static void branch(String branchName) {
        File branch = join(BRANCHES, branchName);
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        writeObject(branch, commit);
    }

    public static void removeBranch(String branchName) {
        File branch = join(BRANCHES, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        File head = readObject(HEAD, File.class);
        if (head.equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branch.delete();
    }

    public static void reset(String sha1) {
        File file = readObject(join(SHA1COMMITS, sha1), File.class);
        if (!file.exists()) {
            System.out.println("No commit with that id exists.");
        }
        File head = readObject(HEAD, File.class);
        writeObject(head, file);
        writeObject(HEAD, file);
        checkout(head.getName());
    }

    public static void merge(String branchName) {
        File head = readObject(HEAD, File.class);
        File branch = readObject(join(BRANCHES, branchName), File.class);
        if (head.equals(branch)) {
            checkout(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

    }

    private static void cleanStagingArea() {
        for (File file : Objects.requireNonNull(ADD_AREA.listFiles())) {
            file.delete();
        }
        for (File file : Objects.requireNonNull(REMOVE_AREA.listFiles())) {
            file.delete();
        }
    }

}
