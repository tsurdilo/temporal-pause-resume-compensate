package io.temporal.sample.config;

import io.temporal.activity.ActivityCancellationType;
import io.temporal.activity.ActivityOptions;
import io.temporal.sample.interceptors.PauseResumeWorkerInterceptor;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.spring.boot.TemporalOptionsCustomizer;
import io.temporal.spring.boot.WorkerOptionsCustomizer;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkerOptions;
import io.temporal.worker.WorkflowImplementationOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TemporalOptionsConfig {
    @Bean
    public WorkerOptionsCustomizer customWorkerOptions() {
        return new WorkerOptionsCustomizer() {
            @Nonnull
            @Override
            public WorkerOptions.Builder customize(
                    @Nonnull WorkerOptions.Builder optionsBuilder,
                    @Nonnull String workerName,
                    @Nonnull String taskQueue) {
                optionsBuilder.setStickyQueueScheduleToStartTimeout(Duration.ofSeconds(25));
                return optionsBuilder;
            }
        };
    }

    @Bean
    public TemporalOptionsCustomizer<WorkflowServiceStubsOptions.Builder>
    customServiceStubsOptions() {
        return new TemporalOptionsCustomizer<>() {
            @Nonnull
            @Override
            public WorkflowServiceStubsOptions.Builder customize(
                    @Nonnull WorkflowServiceStubsOptions.Builder optionsBuilder) {
                optionsBuilder.setRpcLongPollTimeout(Duration.ofSeconds(20));
                return optionsBuilder;
            }
        };
    }

    @Bean
    public TemporalOptionsCustomizer<WorkerFactoryOptions.Builder> customWorkerFactoryOptions() {
        return new TemporalOptionsCustomizer<WorkerFactoryOptions.Builder>() {
            @Nonnull
            @Override
            public WorkerFactoryOptions.Builder customize(
                    @Nonnull WorkerFactoryOptions.Builder optionsBuilder) {
                optionsBuilder.setWorkerInterceptors(new PauseResumeWorkerInterceptor());
                return optionsBuilder;
            }
        };
    }

    @Bean
    public TemporalOptionsCustomizer<WorkflowImplementationOptions.Builder>
    customWorkflowImplementationOptions() {
        return new TemporalOptionsCustomizer<>() {
            @Nonnull
            @Override
            public WorkflowImplementationOptions.Builder customize(
                    @Nonnull WorkflowImplementationOptions.Builder optionsBuilder) {

                Map<String, ActivityOptions> perActivityOptions = new HashMap<>();

                perActivityOptions.put("One", ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());

                perActivityOptions.put("Two", ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());

                perActivityOptions.put("Three", ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());

                perActivityOptions.put("Four", ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(8))
                        .setHeartbeatTimeout(Duration.ofSeconds(3))
                        .setCancellationType(ActivityCancellationType.TRY_CANCEL)
                        .build());

                optionsBuilder.setActivityOptions(perActivityOptions);

                return optionsBuilder;
            }
        };
    }
}
