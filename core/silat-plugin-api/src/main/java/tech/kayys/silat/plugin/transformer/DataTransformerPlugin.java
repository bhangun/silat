package tech.kayys.silat.plugin.transformer;

import tech.kayys.silat.plugin.Plugin;
import java.util.Map;

/**
 * Plugin interface for data transformers
 * 
 * Transformer plugins can transform input/output data for workflow nodes.
 */
public interface DataTransformerPlugin extends Plugin {
    
    /**
     * Check if this transformer supports the given node type
     * 
     * @param nodeType the node type
     * @return true if this transformer can handle the node type
     */
    boolean supports(String nodeType);
    
    /**
     * Transform input data before task execution
     * 
     * @param input the input data
     * @param node the node definition
     * @return the transformed input data
     */
    Map<String, Object> transformInput(Map<String, Object> input, NodeContext node);
    
    /**
     * Transform output data after task execution
     * 
     * @param output the output data
     * @param node the node definition
     * @return the transformed output data
     */
    Map<String, Object> transformOutput(Map<String, Object> output, NodeContext node);
    
    /**
     * Node context information
     */
    interface NodeContext {
        String nodeId();
        String nodeType();
        Map<String, Object> configuration();
    }
}
