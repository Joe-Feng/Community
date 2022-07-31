package com.xcf.community;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Joe
 * @ClassName kafkaTest.java
 * @Description
 * @createTime 2022年05月23日 15:01:00
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class kafkaTest {


    @Autowired
    private KafkaProducer kafkaProducer;// 注入生产者kafkaProducer

    @Test
    public void testKafka() {
        // 生产者发送消息
        kafkaProducer.sendMessage("xcf", "Hello 你好!");
        kafkaProducer.sendMessage("xcf", "在吗？");

        // 在这里进行一下线程阻塞，模仿消费者消费消息的过程
        try {
            Thread.sleep(1000 * 3);// 10s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 生产者
 */
@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;// 注入kafkaTemplate

    /**
     * 发送消息
     *
     * @param topic   消息主题
     * @param content 消息内容
     */
    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

/**
 * 消费者
 */
@Component
@Slf4j
class KafkaConsumer {

    /**
     * 消费者订阅的主题为test
     * 就是通过 @KafkaListener(topics = {"test"}) 注解实现的
     *
     * @param record 接收的消息被封装成 ConsumerRecord 对象
     */
    @KafkaListener(topics = {"xcf"})
    public void handleMessage(ConsumerRecord record) {
        log.info(record.value().toString());
        System.out.println(record.value());
        // System.out.print(record.value());
    }
}
