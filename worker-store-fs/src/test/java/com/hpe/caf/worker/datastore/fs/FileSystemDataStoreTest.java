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

package com.hpe.caf.worker.datastore.fs;


import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.api.worker.DataStore;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.ReferenceNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


public class FileSystemDataStoreTest
{
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();
    private File temp;
    private final String testData = "test123";


    @Before
    public void setUp()
        throws IOException
    {
        temp = tempDir.newFolder();
    }


    @Test
    public void testDataStoreStream()
        throws ConfigurationException, DataStoreException, IOException
    {
        FileSystemDataStoreConfiguration conf = new FileSystemDataStoreConfiguration();
        conf.setDataDir(temp.getAbsolutePath());
        DataStore store = new FileSystemDataStore(conf);
        final byte[] data = testData.getBytes(StandardCharsets.UTF_8);
        String storeRef = store.store(new ByteArrayInputStream(data), "test");
        try (InputStream inStr = store.retrieve(storeRef)) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int nRead;
                while ( (nRead = inStr.read(data, 0, data.length)) != -1 ) {
                    bos.write(data, 0, nRead);
                }
                bos.flush();
                Assert.assertArrayEquals(data, bos.toByteArray());
            }
        }
        Assert.assertEquals(testData.length(), store.size(storeRef));
    }


    @Test
    public void testDataStoreBytes()
        throws ConfigurationException, DataStoreException, IOException
    {
        FileSystemDataStoreConfiguration conf = new FileSystemDataStoreConfiguration();
        conf.setDataDir(temp.getAbsolutePath());
        DataStore store = new FileSystemDataStore(conf);
        final byte[] data = testData.getBytes(StandardCharsets.UTF_8);
        String storeRef = store.store(data, "test");
        try (InputStream inStr = store.retrieve(storeRef)) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int nRead;
                while ( (nRead = inStr.read(data, 0, data.length)) != -1 ) {
                    bos.write(data, 0, nRead);
                }
                bos.flush();
                Assert.assertArrayEquals(data, bos.toByteArray());
            }
        }
        Assert.assertEquals(testData.length(), store.size(storeRef));
    }


    @Test
    public void testDataStorePath()
        throws ConfigurationException, DataStoreException, IOException
    {
        FileSystemDataStoreConfiguration conf = new FileSystemDataStoreConfiguration();
        conf.setDataDir(temp.getAbsolutePath());
        DataStore store = new FileSystemDataStore(conf);
        final byte[] data = testData.getBytes(StandardCharsets.UTF_8);
        Path p = Paths.get(temp.getAbsolutePath()).resolve(UUID.randomUUID().toString());
        Files.write(p, data);
        String storeRef = store.store(p, "test");
        try (InputStream inStr = store.retrieve(storeRef)) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                int nRead;
                while ( (nRead = inStr.read(data, 0, data.length)) != -1 ) {
                    bos.write(data, 0, nRead);
                }
                bos.flush();
                Assert.assertArrayEquals(data, bos.toByteArray());
            }
        }
        Assert.assertEquals(testData.length(), store.size(storeRef));
    }


    @Test(expected = DataStoreException.class)
    public void testInvalidReference()
        throws DataStoreException, IOException
    {
        FileSystemDataStoreConfiguration conf = new FileSystemDataStoreConfiguration();
        conf.setDataDir(temp.getAbsolutePath());
        DataStore store = new FileSystemDataStore(conf);
        Path p = Paths.get(temp.getAbsolutePath());
        for ( int i = 0; i < 5; i++ ) {
            p = p.resolve("..");
        }
        store.retrieve(p.toString());
    }


    @Test(expected = ReferenceNotFoundException.class)
    public void testMissingRef()
        throws DataStoreException
    {
        FileSystemDataStoreConfiguration conf = new FileSystemDataStoreConfiguration();
        conf.setDataDir(temp.getAbsolutePath());
        DataStore store = new FileSystemDataStore(conf);
        store.retrieve(UUID.randomUUID().toString());
    }
}
