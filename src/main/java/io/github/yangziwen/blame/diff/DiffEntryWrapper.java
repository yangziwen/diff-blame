package io.github.yangziwen.blame.diff;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;

import lombok.Builder;
import lombok.Data;

/**
 * The diff entry wrapper
 *
 * @author yangziwen
 */
@Data
@Builder
public class DiffEntryWrapper {

    private File gitDir;

    private DiffEntry diffEntry;

    private List<Edit> editList;

    private EnhancedRawText oldRawText;

    private EnhancedRawText newRawText;

    private BlameResult blameResult;

    private BlameResult reversedBlameResult;

    public String getOldPath() {
        return diffEntry.getOldPath();
    }

    public String getNewPath() {
        return diffEntry.getNewPath();
    }

    public String getAbsoluteOldPath() {
        return getOldFile().getAbsolutePath();
    }

    public String getAbsoluteNewPath() {
        return getNewFile().getAbsolutePath();
    }

    public File getOldFile() {
        return new File(gitDir, diffEntry.getOldPath());
    }

    public File getNewFile() {
        return new File(gitDir, diffEntry.getNewPath());
    }

}
