package com.hpe.caf.worker.testing;

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.codec.JsonCodec;
import com.hpe.caf.util.rabbitmq.ConsumerAckEvent;
import com.hpe.caf.util.rabbitmq.Delivery;
import com.hpe.caf.util.rabbitmq.Event;
import com.hpe.caf.util.rabbitmq.QueueConsumer;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ploch on 01/11/2015.
 */
public class SimpleQueueConsumerImpl implements QueueConsumer {

    private final BlockingQueue<Event<QueueConsumer>> eventQueue;
    private final Channel channel;
    private final ResultHandler resultHandler;
    private final Codec codec = new JsonCodec();

    public SimpleQueueConsumerImpl(final BlockingQueue<Event<QueueConsumer>> queue, Channel channel, ResultHandler resultHandler) {
        this.eventQueue = queue;
        this.channel = channel;
        this.resultHandler = resultHandler;
    }

    @Override
    public void processDelivery(Delivery delivery) {

        System.out.print("New delivery, task id: " );

        eventQueue.add(new ConsumerAckEvent(delivery.getEnvelope().getDeliveryTag()));
        try {
            TaskMessage taskMessage = codec.deserialise(delivery.getMessageData(), TaskMessage.class);
            System.out.println(taskMessage.getTaskId() + ", status: " + taskMessage.getTaskStatus());
            resultHandler.handleResult(taskMessage);
        }
        catch (CodecException | IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public void processAck(long tag) {
        try {
            channel.basicAck(tag, false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processReject(long tag) {}

    @Override
    public void processDrop(long tag) {}
}
