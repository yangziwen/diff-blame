package io.github.yangziwen.blame.command;

import java.io.File;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
        separators = "=",
        commandDescription = "Show the stat info contributors")
public class StatCommand implements Command {

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
            names = {"-c", "--contributor"},
            description = "Only show the stat info of the specified contributor")
    public String contributor;


    @Override
    public void invoke(JCommander commander) {
        // TODO Auto-generated method stub
    }

}
