/*
 * (c) Copyright 2015-2016 Hewlett Packard Enterprise Development LP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hpe.caf.worker.testing;

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.worker.InvalidTaskException;
import com.hpe.caf.api.worker.TaskCallback;
import com.hpe.caf.api.worker.TaskRejectedException;
import com.hpe.caf.configs.RabbitConfiguration;
import com.hpe.caf.util.rabbitmq.RabbitUtil;
import com.hpe.caf.worker.queue.rabbit.RabbitWorkerQueueConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.config.Config;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by ploch on 08/11/2015.
 */
public class QueueServicesFactory {

    private static class NoOpCallback implements TaskCallback {

        @Override
        public void registerNewTask(String s, byte[] bytes, Map<String, Object> headers) throws TaskRejectedException, InvalidTaskException {
        }

        @Override
        public void abortTasks() {
        }
    }

    public static QueueServices create(final RabbitWorkerQueueConfiguration configuration, final String resultsQueueName, final Codec codec) throws IOException, TimeoutException {
        Connection connection = createConnection(configuration, new NoOpCallback());
        Channel pubChan = connection.createChannel();
        Channel conChan = connection.createChannel();

        RabbitUtil.declareWorkerQueue(pubChan, configuration.getInputQueue());
        RabbitUtil.declareWorkerQueue(conChan, resultsQueueName);

        return new QueueServices(connection, pubChan, configuration.getInputQueue(), conChan, resultsQueueName, codec);
    }

    private static Connection createConnection(RabbitWorkerQueueConfiguration configuration, final TaskCallback callback)
            throws IOException, TimeoutException
    {
        RabbitConfiguration rc = configuration.getRabbitConfiguration();
        ConnectionOptions lyraOpts = RabbitUtil.createLyraConnectionOptions(rc.getRabbitHost(), rc.getRabbitPort(), rc.getRabbitUser(), rc.getRabbitPassword());
        Config lyraConfig = RabbitUtil.createLyraConfig(rc.getBackoffInterval(), rc.getMaxBackoffInterval(), rc.getMaxAttempts());
        Connection connection = RabbitUtil.createRabbitConnection(lyraOpts, lyraConfig);
        return connection;
    }
}
