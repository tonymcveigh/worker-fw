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

package com.hpe.caf.api.worker;


import java.util.Objects;


/**
 * Object to represent a response from a Worker, to be interpreted by the core worker framework.
 * @since 6.0
 */
public class WorkerResponse
{
    private final String queueReference;
    private final TaskStatus taskStatus;
    private final byte[] data;
    private final String messageType;
    private final int apiVersion;
    private final byte[] context;


    /**
     * Create a new WorkerResponse.
     * 
     * @param queue the reference to the queue that the response data should be put upon. This can
     *        be null if no queue is provided
     * @param status the status of the message the Worker is returning
     * @param data the serialised task-specific data returned from the Worker internals
     * @param msgType the task-specific message classifier
     * @param version the task-specific message API version
     * @param context the new context to add to the task message, can be null
     */
    public WorkerResponse(final String queue, final TaskStatus status, final byte[] data, final String msgType, final int version, final byte[] context)
    {
        this.queueReference = queue;  // queueReference can be null for a dead end worker
        this.taskStatus = Objects.requireNonNull(status);
        this.data = data;
        this.messageType = Objects.requireNonNull(msgType);
        this.apiVersion = version;
        this.context = context;
    }


    public TaskStatus getTaskStatus()
    {
        return taskStatus;
    }


    public String getQueueReference()
    {
        return queueReference;
    }


    public byte[] getData()
    {
        return data;
    }


    public String getMessageType()
    {
        return messageType;
    }


    public int getApiVersion()
    {
        return apiVersion;
    }


    public byte[] getContext()
    {
        return context;
    }
}
