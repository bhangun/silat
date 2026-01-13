package tech.kayys.silat.cli;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;

class SilatCliTest {

    @Test
    void testSilatCliHelp() {
        SilatCli cli = new SilatCli();
        CommandLine cmd = new CommandLine(cli);
        
        String usage = cmd.getUsageMessage();
        assertNotNull(usage);
        assertTrue(usage.contains("silat"));
        assertTrue(usage.contains("Workflow Engine CLI"));
    }

    @Test
    void testDefaultServerAddress() {
        SilatCli cli = new SilatCli();
        CommandLine cmd = new CommandLine(cli);
        
        // Parse with no arguments to use defaults
        cmd.parseArgs();
        
        assertEquals("localhost:9090", cli.getServerAddress());
    }

    @Test
    void testCustomServerAddress() {
        SilatCli cli = new SilatCli();
        CommandLine cmd = new CommandLine(cli);
        
        cmd.parseArgs("-s", "test-server:8080");
        
        assertEquals("test-server:8080", cli.getServerAddress());
    }
}