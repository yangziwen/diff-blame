package io.github.yangziwen.blame.diff;

import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.util.RawCharUtil;

public class EnhancedRawText extends RawText {

    public EnhancedRawText(byte[] input) {
        super(input);
    }

    /**
     * Check whether line i is a blank line
     * @param i     line number(0-based)
     * @return
     */
    public boolean isBlankLine(int i) {
        int start = getStart(i);
        int end = getEnd(i);
        for (int ptr = start; ptr < end; ptr++) {
            if (!RawCharUtil.isWhitespace(content[ptr])) {
                return false;
            }
        }
        return true;
    }

    private int getStart(final int i) {
        return lines.get(i + 1);
    }

    private int getEnd(final int i) {
        return lines.get(i + 2);
    }

}
