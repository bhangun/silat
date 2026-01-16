package tech.kayys.silat.cli;

import picocli.AutoComplete;

/**
 * Generates shell completion script for the Silat CLI
 */
public class GenerateCompletion {
    public static void main(String[] args) {
        String[] cmdArgs = new String[]{
            "silat",
            "--completion-script-bash",
            "target/silat_completion.sh"
        };
        AutoComplete.main(cmdArgs);
    }
}