package io.github.yangziwen.blame.command;

import java.io.File;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class ShowCommand implements Command {

    @Parameter(
            names = {"-h", "--help"},
            description = "Print this message",
            help = true)
    public boolean help;

    @Parameter(
            names = {"-r", "--repo"},
            description = "Specify the git repository file path",
            required = true)
    public File repo;

    @Parameter(
            names = {"-a", "--author"},
            description = "Only show modifications by the specified author")
    public String author;

    @Parameter(
            names = {"-c", "--committer"},
            description = "Only show modifications by the specified committer")
    public String committer;

    @Override
    public void invoke(JCommander commander) {
        // TODO Auto-generated method stub
    }

}
