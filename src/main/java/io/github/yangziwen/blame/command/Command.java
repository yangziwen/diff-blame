package io.github.yangziwen.blame.command;

import com.beust.jcommander.JCommander;

public interface Command {

    void invoke(JCommander commander);

    default String name() {
        return this.getClass()
                .getSimpleName()
                .replaceAll("(?<!^)([A-Z])", "-$1")
                .toLowerCase()
                .replaceAll("-command$", "");
    }

}
