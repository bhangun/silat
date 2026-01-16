package tech.kayys.silat.runtime.resource;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    WorkflowDefinitionResourceTest.class,
    WorkflowRunResourceTest.class,
    ExecutorRegistryResourceTest.class,
    PluginResourceTest.class,
    CallbackResourceTest.class
})
public class SilatRuntimeTestSuite {
    // Test suite for all Silat Runtime standalone API tests
}