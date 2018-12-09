package io.github.yangziwen.blame.diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import lombok.Builder;
import lombok.Getter;

/**
 * The diff calculator
 * calculate the diff entries and the corresponding edits between two revisions
 *
 * @author yangziwen
 */
@Getter
@Builder
public class DiffCalculator {

    private DiffAlgorithm diffAlgorithm;

    @Builder.Default
    private RawTextComparator comparator = RawTextComparator.DEFAULT;

    @Builder.Default
    private int bigFileThreshold = DiffHelper.DEFAULT_BIG_FILE_THRESHOLD;

    public List<DiffEntryWrapper> calculate(
            File repoDir,
            String oldRev,
            String newRev) throws Exception {
        try (Git git = Git.open(repoDir);
                ObjectReader reader = git.getRepository().newObjectReader();
                RevWalk rw = new RevWalk(git.getRepository())) {
            RevCommit oldCommit = rw.parseCommit(git.getRepository().resolve(oldRev));
            RevCommit newCommit = rw.parseCommit(git.getRepository().resolve(newRev));
            return calculateDiff(oldCommit, newCommit, reader, git, repoDir);
        }
    }

    private List<DiffEntryWrapper> calculateDiff(
            RevCommit oldCommit,
            RevCommit newCommit,
            ObjectReader reader,
            Git git,
            File repoDir) throws Exception {

        RenameDetector detector = new RenameDetector(git.getRepository());
        AbstractTreeIterator oldTree = new CanonicalTreeParser(null, reader, oldCommit.getTree());
        AbstractTreeIterator newTree = new CanonicalTreeParser(null, reader, newCommit.getTree());

        List<DiffEntry> entries = git.diff()
                .setOldTree(oldTree)
                .setNewTree(newTree)
                .call();

        detector.reset();
        detector.addAll(entries);
        entries = detector.compute();

        return entries.stream()
                .map(entry -> {
                    EnhancedRawText oldText = newRawText(entry, DiffEntry.Side.OLD, reader);
                    EnhancedRawText newText = newRawText(entry, DiffEntry.Side.NEW, reader);
                    BlameResult blameResult = calculateBlameResult(entry.getNewPath(), newCommit, git);
                    BlameResult reversedBlameResult = calculateReversedBlameResult(
                            entry.getOldPath(), oldCommit, newCommit, git);
                    return DiffEntryWrapper.builder()
                            .gitDir(repoDir)
                            .diffEntry(entry)
                            .editList(calculateEditList(oldText, newText))
                            .oldRawText(oldText)
                            .newRawText(newText)
                            .blameResult(blameResult)
                            .reversedBlameResult(reversedBlameResult)
                            .build();
                }).collect(Collectors.toList());
    }

    private List<Edit> calculateEditList(RawText oldText, RawText newText) {
        EditList edits = diffAlgorithm.diff(comparator, oldText, newText);
        List<Edit> editList = new ArrayList<Edit>();
        for (Edit edit : edits) {
            editList.add(edit);
        }
        return editList;
    }

    private BlameResult calculateBlameResult(String filePath, RevCommit startCommit, Git git) {
        try {
            return git.blame().setFilePath(filePath)
                    .setStartCommit(startCommit)
                    .setTextComparator(comparator)
                    .setFollowFileRenames(true)
                    .call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private BlameResult calculateReversedBlameResult(
            String filePath,
            RevCommit startCommit,
            RevCommit endCommit,
            Git git) {
        try {
            return git.blame().setFilePath(filePath)
                    .reverse(startCommit, endCommit)
                    .setTextComparator(comparator)
                    .setFollowFileRenames(true)
                    .call();
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EnhancedRawText newRawText(DiffEntry entry, DiffEntry.Side side, ObjectReader reader) {
        try {
            return new EnhancedRawText(DiffHelper.open(entry, side, reader, bigFileThreshold));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
