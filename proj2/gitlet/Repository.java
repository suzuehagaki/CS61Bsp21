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
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
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
        if (commit.getMap().containsKey(file.getPath())
                && commit.getMap().get(file.getPath()).equals(sha1)) {
            if (join(ADD_AREA, fileName).exists()) {
                join(ADD_AREA, fileName).delete();
            }
            return;
        }
        File removedFile = join(REMOVE_AREA, fileName);
        if (removedFile.exists()) {
            Blob temp = readObject(removedFile, Blob.class);
            if (sha1.equals(temp.getSHA1())) {
                removedFile.delete();
                return;
            }
        }
        Blob b = new Blob(file, sha1, content);
        writeObject(join(ADD_AREA,  file.getName()), b);
    }

    public static void commit(String message) {

        if (Objects.requireNonNull(ADD_AREA.listFiles()).length == 0
                && Objects.requireNonNull(REMOVE_AREA.listFiles()).length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }

        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
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
            newCommit.removeFile(blob.getFullFilePath());
        }

        cleanStagingArea();

        writeObject(HEAD, head);
        writeObject(head, newCommit);
        writeObject(join(SHA1COMMITS, newCommit.generateSHA1()), newCommit);

    }

    public static void remove(String fileName) {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        File fileInAddArea = join(ADD_AREA, fileName);
        File fileInCwd = join(CWD, fileName);
        if (!fileInAddArea.exists() && !commit.containFile(fileInCwd.getPath())) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (commit.containFile(fileInCwd.getPath())) {
            File file = join(BLOBS, commit.getSHA1(fileInCwd.getPath()));
            Blob blob = readObject(file, Blob.class);
            writeObject(join(REMOVE_AREA, fileName), blob);
            fileInCwd.delete();
        }
        fileInAddArea.delete();
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

    public static void checkout(String str, String fileName) {
        if (!str.equals("--")) {
            System.out.println("Incorrect operands.");
            return;
        }
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

    public static void checkout(String sha1Commit, String str, String fileName) {
        if (!str.equals("--")) {
            System.out.println("Incorrect operands.");
            return;
        }
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
        if (!checkoutHelper(head, branch)) {
            return;
        }
        writeObject(HEAD, branch);
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
        File file = join(SHA1COMMITS, sha1);
        if (!file.exists()) {
            System.out.println("No commit with that id exists.");
        }
        File head = readObject(HEAD, File.class);
        if (!checkoutHelper(head, file)) {
            return;
        }
        Commit commit = readObject(file, Commit.class);
        writeObject(head, commit);
        writeObject(HEAD, head);
    }

    public static void merge(String branchName) {

        File head = readObject(HEAD, File.class);
        File branch = readObject(join(BRANCHES, branchName), File.class);

        if (ADD_AREA.listFiles() != null || REMOVE_AREA.listFiles() != null) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        if (head.equals(branch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Commit headCommit = readObject(head, Commit.class);
        for (File file : Objects.requireNonNull(CWD.listFiles())) {
            if (!headCommit.containFile(file.getPath())) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }

        Commit branchCommit = readObject(branch, Commit.class);
        String sha1Ancestor = findSplitPoint(branchCommit, headCommit);
        if (sha1Ancestor.equals(headCommit.generateSHA1())) {
            checkout(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if (sha1Ancestor.equals(branchCommit.generateSHA1())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        Commit ancestorCommit = readObject(join(SHA1COMMITS, sha1Ancestor), Commit.class);

        Set<String> branchFiles = branchCommit.getFiles();
        Set<String> ancestorFiles = ancestorCommit.getFiles();
        String sha1Branch = branchCommit.generateSHA1();
        boolean conflicted = false;

        // Case 1、2、3、6、7、8: modified cases
        for (String s : ancestorFiles) {
            String sha1AncestorFile = ancestorCommit.getSHA1(s);
            String sha1HeadFile = headCommit.getSHA1(s);
            String sha1BranchFile = branchCommit.getSHA1(s);

            // Case 3
            if (sha1HeadFile == null && sha1BranchFile == null) {
                continue;
            }
            // Case 6
            if (sha1AncestorFile.equals(sha1HeadFile) && sha1BranchFile == null) {
                File file = new File(s);
                headCommit.removeFile(file);
                file.delete();
                continue;
            }
            // Case 7
            if (sha1AncestorFile.equals(sha1BranchFile) && sha1HeadFile == null) {
                continue;
            }
            // Case 1
            if (!sha1AncestorFile.equals(sha1BranchFile) && sha1AncestorFile.equals(sha1HeadFile)) {
                String fileName = new File(s).getName();
                checkout(sha1Branch, "--", fileName);
                add(fileName);
                continue;
            }
            // Case 2
            if (sha1AncestorFile.equals(sha1BranchFile) && !sha1AncestorFile.equals(sha1HeadFile)) {
                continue;
            }

            // Case 8
            String temp1 = sha1HeadFile;
            String temp2 = sha1BranchFile;
            if (sha1HeadFile == null) {
                temp1 = sha1BranchFile;
                temp2 = null;
            }
            if (!temp1.equals(temp2)) {
                String conflict = "<<<<<<< HEAD\n"
                        + "contents of file in current branch\n"
                        + "=======\n"
                        + "contents of file in given branch\n"
                        + ">>>>>>>";
                String fileName = new File(s).getName();
                writeObject(join(CWD, fileName), conflict);
                add(fileName);
                conflicted = true;
            }

        }

        // Case 5
        for (String s : branchFiles) {
            if (!headCommit.containFile(s) && !ancestorCommit.containFile(s)) {
                String fileName = new File(s).getName();
                checkout(sha1Branch, "--", fileName);
                add(fileName);
            }
        }

        commit("Merged " + head.getName() + " into " + branchName);
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
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

    private static String findSplitPoint(Commit branch1, Commit branch2) {
        int depth1 = 0, depth2 = 0;
        Commit temp = branch1;
        while (temp.getFirstParent() != null) {
            temp = readObject(join(SHA1COMMITS, temp.getFirstParent()), Commit.class);
            depth1 += 1;
        }
        temp = branch2;
        while (temp.getFirstParent() != null) {
            temp = readObject(join(SHA1COMMITS, temp.getFirstParent()), Commit.class);
            depth1 += 1;
        }
        if (depth1 > depth2) {
            temp = branch1;
            branch1 = branch2;
            branch2 = temp;
            int tempDepth = depth1;
            depth1 = depth2;
            depth2 = tempDepth;
        }
        while (depth1 < depth2) {
            branch2 = readObject(join(SHA1COMMITS, branch2.getFirstParent()), Commit.class);
            depth2 -= 1;
        }
        if (branch1.generateSHA1().equals(branch2.generateSHA1())) {
            return branch1.generateSHA1();
        }
        while (!branch1.getFirstParent().equals(branch2.getFirstParent())) {
            branch1 = readObject(join(SHA1COMMITS, branch1.getFirstParent()), Commit.class);
            branch2 = readObject(join(SHA1COMMITS, branch2.getFirstParent()), Commit.class);
        }
        return branch1.getFirstParent();
    }

    private static boolean hasUntracked(Commit commit, File prePath) {
        for (File file : Objects.requireNonNull(prePath.listFiles())) {
            if (file.getName().charAt(0) == '.' || file.getName().equals("gitlet")) {
                continue;
            }
            if (file.isDirectory()) {
                if (hasUntracked(commit, file)) {
                    return true;
                }
            } else if (!commit.containFile(file.getPath())) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkoutHelper(File head, File branch) {
        Commit headCommit = readObject(head, Commit.class);
        Commit commit = readObject(branch, Commit.class);
        if (hasUntracked(headCommit, CWD)) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return false;
        }
        headCommit.clearFiles();
        for (String s : commit.getFiles()) {
            File temp = join(BLOBS, commit.getSHA1(s));
            Blob blob = readObject(temp, Blob.class);
            blob.writeBlob();
        }
        return true;
    }

}
