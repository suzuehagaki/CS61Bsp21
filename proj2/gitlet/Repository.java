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
    /** The blobs which contain files' and commits' content. */
    public static final File CONTENTS = join(GITLET_DIR, "contents");
    public static final File FILES_CONTENTS = join(CONTENTS, "files");
    public static final File COMMITS_CONTENTS = join(CONTENTS, "commits");
    /** The commits which maintain versions. */
    public static final File COMMITS = join(GITLET_DIR, "commits");
    /** Area to hold staged files. */
    public static final File STAGE_AREA = join(GITLET_DIR, "StagingArea");
    public static final File ADD_AREA = join(STAGE_AREA, "AddingArea");
    public static final File REMOVE_AREA = join(STAGE_AREA, "RemovingArea");
    public static final File BRANCHES = join(COMMITS, "branches");
    public static final File HEAD = join(COMMITS, "head");
    public static final File MASTER = join(BRANCHES, "master");

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
            CONTENTS.mkdir();
            FILES_CONTENTS.mkdir();
            COMMITS_CONTENTS.mkdir();
            COMMITS.mkdir();
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
        writeObject(join(COMMITS_CONTENTS, commit.generateSHA1()), commit);
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
        if (commit.containFile(fileName)
                && commit.getSHA1(fileName).equals(sha1)) {
            join(ADD_AREA, fileName).delete();
            join(REMOVE_AREA, fileName).delete();
            return;
        }
        writeContents(join(ADD_AREA,  fileName), (Object) content);
    }

    public static void commit(String message) {

        commitHelper(message, null);

    }

    public static void remove(String fileName) {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        File fileInAddArea = join(ADD_AREA, fileName);
        File fileInCwd = join(CWD, fileName);
        if (!fileInAddArea.exists() && !commit.containFile(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (commit.containFile(fileInCwd.getName())) {
            File file = join(FILES_CONTENTS, commit.getSHA1(fileName));
            byte[] content = readContents(file);
            writeContents(join(REMOVE_AREA, fileName), (Object) content);
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
            commit = readObject(join(COMMITS_CONTENTS, commit.getFirstParent()), Commit.class);
        }
    }

    public static void globalLog() {
        for (File file : Objects.requireNonNull(COMMITS_CONTENTS.listFiles())) {
            Commit commit = readObject(file, Commit.class);
            commit.dumpCommit();
        }
    }

    public static void find(String message) {
        int count = 0;
        for (File file : Objects.requireNonNull(COMMITS_CONTENTS.listFiles())) {
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
        if (!commit.containFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String sha1 = commit.getSHA1(fileName);
        byte[] content = readContents(join(FILES_CONTENTS, sha1));
        writeContents(join(CWD, fileName), (Object) content);
    }

    public static void checkout(String sha1, String str, String fileName) {
        if (!str.equals("--")) {
            System.out.println("Incorrect operands.");
            return;
        }
        String fullSha1 = "";
        for (File file : Objects.requireNonNull(COMMITS_CONTENTS.listFiles())) {
            if (file.getName().startsWith(sha1)) {
                fullSha1 = file.getName();
            }
        }
        if (fullSha1.isEmpty()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = readObject(join(COMMITS_CONTENTS, fullSha1), Commit.class);
        if (!commit.containFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String fileSha1 = commit.getSHA1(fileName);
        byte[] content = readContents(join(FILES_CONTENTS, fileSha1));
        writeContents(join(CWD, fileName), (Object) content);
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
        if (checkoutHelper(head, branch)) {
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
        File file = join(COMMITS_CONTENTS, sha1);
        if (!file.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File head = readObject(HEAD, File.class);
        if (checkoutHelper(head, file)) {
            return;
        }
        Commit commit = readObject(file, Commit.class);
        writeObject(head, commit);
        writeObject(HEAD, head);
        cleanStagingArea();
    }

    public static void merge(String branchName) {

        File head = readObject(HEAD, File.class);
        File branch = join(BRANCHES, branchName);

        if (Objects.requireNonNull(ADD_AREA.listFiles()).length != 0
                || Objects.requireNonNull(REMOVE_AREA.listFiles()).length != 0) {
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
        Commit branchCommit = readObject(branch, Commit.class);
        String sha1Ancestor = findSplitPoint(branchCommit, headCommit);
        File ancestor = join(COMMITS_CONTENTS, sha1Ancestor);
        if (hasUntracked(ancestor, branch)) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return;
        }
        if (sha1Ancestor.equals(branchCommit.generateSHA1())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (sha1Ancestor.equals(headCommit.generateSHA1())) {
            checkout(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        Commit ancestorCommit = readObject(join(COMMITS_CONTENTS, sha1Ancestor), Commit.class);

        Set<String> branchFiles = branchCommit.getFiles();
        Set<String> ancestorFiles = ancestorCommit.getFiles();
        String sha1Branch = branchCommit.generateSHA1();
        boolean conflicted = false;

        // Case 1、2、3、6、7、8: modified cases
        for (String fileName : ancestorFiles) {
            String sha1AncestorFile = ancestorCommit.getSHA1(fileName);
            String sha1HeadFile = headCommit.getSHA1(fileName);
            String sha1BranchFile = branchCommit.getSHA1(fileName);

            // Case 3
            if (sha1HeadFile == null && sha1BranchFile == null) {
                continue;
            }
            // Case 6
            if (sha1AncestorFile.equals(sha1HeadFile) && sha1BranchFile == null) {
                headCommit.removeFile(fileName);
                join(CWD, fileName).delete();
                continue;
            }
            // Case 7
            if (sha1AncestorFile.equals(sha1BranchFile) && sha1HeadFile == null) {
                continue;
            }
            // Case 1
            if (!sha1AncestorFile.equals(sha1BranchFile) && sha1AncestorFile.equals(sha1HeadFile)) {
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
                        + ">>>>>>>\n";
                writeContents(join(CWD, fileName), conflict);
                add(fileName);
                conflicted = true;
            }
        }

        for (String fileName : branchFiles) {
            // Case 5
            if (!headCommit.containFile(fileName) && !ancestorCommit.containFile(fileName)) {
                checkout(sha1Branch, "--", fileName);
                add(fileName);
            } else if (!ancestorCommit.containFile(fileName)
                    && !branchCommit.getSHA1(fileName).equals(headCommit.getSHA1(fileName))) {
                String conflict = "<<<<<<< HEAD\n"
                        + Arrays.toString(readContents(join(FILES_CONTENTS, headCommit.getSHA1(fileName))))
                        + "\n=======\n"
                        + Arrays.toString(readContents(join(FILES_CONTENTS, branchCommit.getSHA1(fileName))))
                        + "\n>>>>>>>\n";
                writeContents(join(CWD, fileName), conflict);
                add(fileName);
                conflicted = true;
            }
        }

        commitHelper("Merged " + branchName + " into " + head.getName(), sha1Branch);
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

    /** Get the largest precursor of both branch1 and branch2 in a hasse diagram.
      * Is it a hasse diagram? */
    private static String findSplitPoint(Commit branch1, Commit branch2) {

        Set<String> precursorsOfBranch1 = findPrecursors(branch1);
        Set<String> commonPrecursors = new TreeSet<>();
        List<Commit> commits = new LinkedList<>();
        commits.add(branch2);
        if (precursorsOfBranch1.contains(branch2.generateSHA1())) {
            return branch2.generateSHA1();
        }
        while (!commits.isEmpty()) {
            List<Commit> temp = new LinkedList<>();
            for (Commit commit : commits) {
                String firstParent = commit.getFirstParent();
                String secondParent = commit.getSecondParent();
                if (firstParent != null) {
                    temp.add(readObject(join(COMMITS_CONTENTS, firstParent), Commit.class));
                    if (precursorsOfBranch1.contains(firstParent)) {
                        commonPrecursors.add(firstParent);
                    }
                }
                if (secondParent != null) {
                    temp.add(readObject(join(COMMITS_CONTENTS, secondParent), Commit.class));
                    if (precursorsOfBranch1.contains(secondParent)) {
                        commonPrecursors.add(secondParent);
                    }
                }
            }
            commits = temp;
        }

        String splitPoint = "";
        int depth = -1;
        for (String commonPrecursor : commonPrecursors) {
            Commit commit = readObject(join(COMMITS_CONTENTS, commonPrecursor), Commit.class);
            int tempDepth = getDepth(commit);
            if (depth < tempDepth) {
                splitPoint = commonPrecursor;
                depth = tempDepth;
            }
        }
        return splitPoint;
    }

    private static Set<String> findPrecursors(Commit commit) {
        Set<String> precursors = new TreeSet<>();
        List<Commit> commits = new LinkedList<>();
        commits.add(commit);
        precursors.add(commit.generateSHA1());
        while (!commits.isEmpty()) {
            List<Commit> temp = new LinkedList<>();
            for (Commit com : commits) {
                if (com.getFirstParent() != null) {
                    temp.add(readObject(join(COMMITS_CONTENTS, com.getFirstParent()), Commit.class));
                    precursors.add(com.getFirstParent());
                }
                if (com.getSecondParent() != null) {
                    temp.add(readObject(join(COMMITS_CONTENTS, com.getSecondParent()), Commit.class));
                    precursors.add(com.getSecondParent());
                }
            }
            commits = temp;
        }
        return precursors;
    }

    private static int getDepth(Commit commit) {
        if (commit.getFirstParent() == null) {
            return 0;
        }
        int depth1 = getDepth(readObject(join(COMMITS_CONTENTS, commit.getFirstParent()), Commit.class));
        int depth2 = 0;
        if (commit.getSecondParent() != null) {
            depth2 = getDepth(readObject(join(COMMITS_CONTENTS, commit.getSecondParent()), Commit.class));
        }
        if (depth1 > depth2) {
            return 1 + depth1;
        }
        return 1 + depth2;
    }

    private static boolean hasUntracked(File branch) {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        Commit branchCommit = readObject(branch, Commit.class);
        for (File file : Objects.requireNonNull(CWD.listFiles())) {
            if (file.isDirectory()) {
                continue;
            }
            if (!commit.containFile(file.getName()) && branchCommit.containFile(file.getName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasUntracked(File splitPoint, File branch) {
        File head = readObject(HEAD, File.class);
        Commit commit = readObject(head, Commit.class);
        Commit branchCommit = readObject(branch, Commit.class);
        Commit splitCommit = readObject(splitPoint, Commit.class);
        for (File file : Objects.requireNonNull(CWD.listFiles())) {
            if (file.isDirectory()) {
                continue;
            }
            if (!commit.containFile(file.getName()) && branchCommit.containFile(file.getName())) {
                return true;
            }
            if (!commit.containFile(file.getName()) && splitCommit.containFile(file.getName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkoutHelper(File head, File branch) {
        Commit headCommit = readObject(head, Commit.class);
        Commit commit = readObject(branch, Commit.class);
        if (hasUntracked(branch)) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return true;
        }
        for (String fileName : headCommit.getFiles()) {
            join(CWD, fileName).delete();
        }
        for (Map.Entry<String, String> entry : commit.getMap().entrySet()) {
            byte[] content = readContents(join(FILES_CONTENTS, entry.getValue()));
            writeContents(join(CWD, entry.getKey()), (Object) content);
        }
        return false;
    }

    private static void commitHelper(String message, String secondParentID) {
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
        Commit newCommit = new Commit(commit, secondParentID, message);

        for (File file : Objects.requireNonNull(ADD_AREA.listFiles())) {
            byte[] content = readContents(file);
            String sha1 = sha1((Object) content);
            String formerContentSha1 = newCommit.getSHA1(file.getName());
            if (formerContentSha1 == null) {
                newCommit.addFile(file.getName(), sha1);
            } else if (!sha1.equals(formerContentSha1)) {
                newCommit.removeFile(file.getName());
                newCommit.addFile(file.getName(), sha1);
            }
            writeContents(join(FILES_CONTENTS, sha1), (Object) content);
        }
        for (File file : Objects.requireNonNull(REMOVE_AREA.listFiles())) {
            newCommit.removeFile(file.getName());
        }

        cleanStagingArea();

        writeObject(HEAD, head);
        writeObject(head, newCommit);
        writeObject(join(COMMITS_CONTENTS, newCommit.generateSHA1()), newCommit);
    }

}
