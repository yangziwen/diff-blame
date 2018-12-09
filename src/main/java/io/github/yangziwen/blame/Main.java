package io.github.yangziwen.blame;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

public class Main {

    public static void main(String[] args) {
        System.out.println(ansi().fg(RED).a("Hello").fg(GREEN).a(" World").reset());
    }

}
