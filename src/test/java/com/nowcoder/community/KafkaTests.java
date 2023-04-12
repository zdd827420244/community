package com.nowcoder.community;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() { //生产者发数据，使我们主动去调去发的，我们希望什么时候发，就什么时候调用这个函数  而消费者，处理这个消息是被动地，一旦队列里有消息，就会去处理
        kafkaProducer.sendMessage("test", "你好");
        kafkaProducer.sendMessage("test", "在吗");

        try {
            Thread.sleep(1000 * 10);  //等待一会消费者消费
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate; //KafkaTemplate被Spring整合到容器里了，直接注入就行

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }

}

@Component
class KafkaConsumer {  //被动的自动去处理数据，不需要我们去调用，不需要用到KafkaTemplate这个工具

    @KafkaListener(topics = {"test"}) //监听器，监听主题 里面可以一个或多个主题 一直在监听这个主题，没有消息就阻塞着，一直试图读取  一旦读到就会交给下面这个方法，会把消息封装成ConsumerRecord
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }


}