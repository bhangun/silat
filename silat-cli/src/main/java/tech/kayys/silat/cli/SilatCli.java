package tech.kayys.silat.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "silat",
    description = "Silat Workflow Engine CLI",
    subcommands = {
        WorkflowDefinitionCommands.class,
        WorkflowRunCommands.class,
        ExecutorCommands.class,
        ConfigCommands.class
    },
    mixinStandardHelpOptions = true,
    version = "1.0.0"
)
public class SilatCli implements Runnable {

    @Option(
        names = {"-s", "--server"},
        description = "gRPC server address (host:port)",
        defaultValue = "localhost:9090"
    )
    private String serverAddress;

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SilatCli()).execute(args);
        System.exit(exitCode);
    }
}