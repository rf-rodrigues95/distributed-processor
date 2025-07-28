package com.crossjoin.processor.service;

import com.crossjoin.processor.util.ExpressionEvaluator;
import org.apache.pulsar.client.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.data.redis.core.ValueOperations;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.StandardCharsets;


@Service
public class ProducerConsumerService {

    private PulsarClient client;
    private Producer<byte[]> producer;
    private Consumer<byte[]> consumer;

    private final StringRedisTemplate redisTemplate;

    public ProducerConsumerService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() throws PulsarClientException {
        client = PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .build();
        producer = client.newProducer()
                .topic("persistent://public/default/processor")
                .create();
    }

    public void produceFile(File file) {
        //implement logger
        System.out.println("Processing file: " + file.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines().forEach(line -> {
                System.out.println("Sending " + line + "to Pulsar");

                try {
                    producer.send(line.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    System.err.println("Error reading file " + file.getName());
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName());
            e.printStackTrace();
        }
    }

    public void consumeFile() throws PulsarClientException {
        consumer = client.newConsumer()
                    .topic("persistent://public/default/processor")
                    .subscriptionName("acc-processor-sub")
                    .subscriptionType(SubscriptionType.Shared)
                    .subscribe();

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        System.out.println("Consumer started, waiting for messages...");
        
        while (true) {
            try {
                Message<byte[]> msg = consumer.receive();
                String payload = new String(msg.getData(), StandardCharsets.UTF_8);

                System.out.println("Received message: " + payload);

                String[] parts = payload.split(",");
                String key = parts[0];
                String expr = parts[1];
                int result = ExpressionEvaluator.evaluate(expr);
                ops.increment("agg:" + key, result);
                consumer.acknowledge(msg);

                System.out.println("Acknowledged message with id: " + msg.getMessageId());
            } catch (Exception e) {
                System.err.println("Error consuming file");
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void cleanup() throws PulsarClientException {
        if (producer != null) {
            producer.close();
        }
        if (client != null) {
            client.close();
        }
    }
}