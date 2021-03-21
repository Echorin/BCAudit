package com.oschain.fastchaindb.common.rokectmq;

public class RckProducer {
//    public static void main(String[] args) throws InterruptedException {
//        ApplicationContext context = new ClassPathXmlApplicationContext("/spring/spring-rokectmq.xml");
//        DefaultMQProducer producer = (DefaultMQProducer) context.getBean("rocketmqProduct");
//        for (int i = 0; i < 10; i++) {
//            try {
//
//                /*
//                 * Create a message instance, specifying topic, tag and message body.
//                 */
//                Message msg = new Message("TopicTest" /* Topic */,
//                        "TagA" /* Tag */,
//                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//                );
//
//                /*
//                 * Call send message to deliver message to one of brokers.
//                 */
//                SendResult sendResult = producer.send(msg);
//
//                System.out.printf("%s%n", sendResult);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Thread.sleep(1000);
//            }
//        }
//    }
}
