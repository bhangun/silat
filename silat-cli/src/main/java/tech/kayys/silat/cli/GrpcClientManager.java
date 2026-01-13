package tech.kayys.silat.cli;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.Closeable;
import java.io.IOException;

import tech.kayys.silat.grpc.v1.MutinyWorkflowDefinitionServiceGrpc;
import tech.kayys.silat.grpc.v1.MutinyWorkflowServiceGrpc;
import tech.kayys.silat.grpc.v1.MutinyExecutorServiceGrpc;

public class GrpcClientManager implements Closeable {

    private final ManagedChannel channel;
    private final MutinyWorkflowDefinitionServiceGrpc.MutinyWorkflowDefinitionServiceStub workflowDefinitionStub;
    private final MutinyWorkflowServiceGrpc.MutinyWorkflowServiceStub workflowStub;
    private final MutinyExecutorServiceGrpc.MutinyExecutorServiceStub executorStub;

    public GrpcClientManager(String serverAddress) {
        this.channel = ManagedChannelBuilder.forTarget(serverAddress)
                .usePlaintext()
                .build();

        this.workflowDefinitionStub = MutinyWorkflowDefinitionServiceGrpc.newMutinyStub(channel);
        this.workflowStub = MutinyWorkflowServiceGrpc.newMutinyStub(channel);
        this.executorStub = MutinyExecutorServiceGrpc.newMutinyStub(channel);
    }

    public MutinyWorkflowDefinitionServiceGrpc.MutinyWorkflowDefinitionServiceStub getWorkflowDefinitionStub() {
        return workflowDefinitionStub;
    }

    public MutinyWorkflowServiceGrpc.MutinyWorkflowServiceStub getWorkflowStub() {
        return workflowStub;
    }

    public MutinyExecutorServiceGrpc.MutinyExecutorServiceStub getExecutorStub() {
        return executorStub;
    }

    @Override
    public void close() throws IOException {
        if (channel != null && !channel.isShutdown()) {
            try {
                channel.shutdown();
                // Wait for graceful shutdown
                if (!channel.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                    if (!channel.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                        System.err.println("Channel did not terminate in time");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                channel.shutdownNow();
            }
        }
    }
}