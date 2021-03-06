package com.demo.allframework.rabbitmq.springmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring 整合 RabbitMQ 生产者
 * @author YUDI
 * @date 2020/12/28 22:59
 */
@RestController
@RequestMapping("/")
public class Producer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * Hello World
     */
    @GetMapping("send")
    public void sending(){
        // 最基本的 hello word 模型
        // 参数一：指定 routingKey，当交换机为默认时，会自动查询与 routingKey 相同名称的队列(没有则创建)并发送消息
        // 参数二：消息内容，Object 类型
        rabbitTemplate.convertAndSend("hello","Hello World!");
    }

    /**
     * workQueues：工作队列
     */
    @GetMapping("workQueues")
    public void workQueues(){
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("work","Hello work queues!");
        }
    }

    /**
     * fanout：广播模式
     */
    @GetMapping("fanout")
    public void fanout(){
        // 指定交换机名称，路由键，消息
        // 广播模式下路由键无意义，为 "" 即可
        rabbitTemplate.convertAndSend("notice","","Hello fanout!");
    }

    @GetMapping("configFanout")
    public void configFanout(){
        // 发送消息到 RabbitMQConfig 配置的交换机
        rabbitTemplate.convertAndSend("boot_fanout_exchange","","config fanout!");
    }

    /**
     * direct：直连模式
     */
    @GetMapping("direct/{routingKey}")
    public void direct(@PathVariable String routingKey){
        // 指定交换机名称，路由键，消息
        rabbitTemplate.convertAndSend("logs",routingKey,"Hello direct! RoutingKey is " + routingKey);
    }

    @GetMapping("configDirect/{routingKey}")
    public void configDirect(@PathVariable String routingKey){
        // 发送消息到 RabbitMQConfig 配置的交换机
        rabbitTemplate.convertAndSend("boot_direct_exchange",routingKey,"config direct! RoutingKey is " + routingKey);
    }

    /**
     * topics：主题模式
     */
    @GetMapping("topic/{routingKey}")
    public void topic(@PathVariable String routingKey){
        // 指定交换机名称，路由键，消息
        rabbitTemplate.convertAndSend("topics",routingKey,"Hello topic! RoutingKey is " + routingKey);
    }

    @GetMapping("configTopic/{routingKey}")
    public void configTopic(@PathVariable String routingKey){
        // 方式一：通过消息后处理对象设置消息的相关参数
        // MessagePostProcessor messagePostProcessor = (message -> {
        //     message.getMessageProperties().setExpiration("5000");
        //     return message;
        // });

        // 方式二：通过消息参数设置消息的过期时间
        MessageProperties messageProperties = new MessageProperties();
        // messageProperties.setExpiration("5000");
        // 构建消息对象
        Message message = new Message(("config topic! RoutingKey is " + routingKey).getBytes(), messageProperties);
        // 发送消息到 RabbitMQConfig 配置的交换机
        for (int i = 0; i < 10; i++) {
            // 基于方式一的消息发送
            // rabbitTemplate.convertAndSend("boot_topic_exchange", routingKey, "config topic! RoutingKey is " + routingKey, messagePostProcessor);
            // 方式二
            rabbitTemplate.convertAndSend("boot_topic_exchange", routingKey,  message);
        }
    }

    /**
     * 死信队列测试
     * @param routingKey
     */
    @GetMapping("deadTopic/{routingKey}")
    public void deadTopic(@PathVariable String routingKey){
        // 测试情况一：消息过期
        // rabbitTemplate.convertAndSend("test_topic_exchange",routingKey,"test ttl dead message...");
        // 测试情况二：消息队列长度超出限制
        for (int i = 0; i < 20; i++) {
            rabbitTemplate.convertAndSend("test_topic_exchange",routingKey,"test ttl dead message...");
        }
    }
}
