package com.crossjoin.processor.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class ResultWriter {

    @Value("${app.instance-id}")
    private int instanceId;

    @Value("${app.master-instance}")
    private int masterInstance;

    private final StringRedisTemplate redisTemplate;

    public ResultWriter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void flushIfDone() {
        if (instanceId != masterInstance) return;

        Set<String> keys = redisTemplate.keys("agg:*");
        if (keys == null || keys.isEmpty()) {
            System.out.println("No aggregation keys in Redis");
            return;
        }
        try (FileWriter writer = new FileWriter("resultado.csv")) {
            List<String> sortedKeys = new ArrayList<>(keys);
            Collections.sort(sortedKeys);

            for (String key : sortedKeys) {
                String value = redisTemplate.opsForValue().get(key);
                writer.write(key.replace("agg:", "") + "," + value + "\n");
                System.out.println("Successfully wrote to resultado.csv");
            }
        } catch (IOException e) {
            System.err.println("Error writing resultado.csv");
            e.printStackTrace();
        }
    }
}
