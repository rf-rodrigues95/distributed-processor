package com.crossjoin.processor.config;

import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import com.crossjoin.processor.service.ProducerConsumerService;

import jakarta.annotation.PostConstruct;

import org.springframework.integration.file.filters.FileSystemPersistentAcceptOnceFileListFilter;
import java.io.File;

@Configuration
@EnableIntegration
public class IntegrationConfig {

    @Value("${app.input-directory}")
    private String inputDirectory;

    @Autowired
    private ConcurrentMetadataStore metadataStore;

    @Autowired
    private ProducerConsumerService producerConsumerService;

    @PostConstruct
    public void start() {
        startConsumerLoop();
    }

    @Bean
    public FileReadingMessageSource fileReadingMessageSource() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(inputDirectory));
        source.setFilter(new FileSystemPersistentAcceptOnceFileListFilter(metadataStore, "fileLock"));
        return source;
    }

    @Bean
    public IntegrationFlow fileReadingFlow() {
        return IntegrationFlow
            .from(fileReadingMessageSource(),
                  c -> c.poller(Pollers.fixedDelay(1000)
                                    .maxMessagesPerPoll(1)))
            .handle((file, headers) -> {
                producerConsumerService.produceFile((File) file);
                return null;
            })
            .get();
    }

    public void startConsumerLoop() {
        new Thread(() -> {
            try {
                producerConsumerService.consumeFile();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }, "PulsarConsumerThread").start();
    }
}
