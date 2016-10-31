package com.hpe.caf.worker.datastore.s3;

import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.api.ConfigurationSource;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.DataStoreProvider;
import com.hpe.caf.api.worker.ManagedDataStore;

public class S3DataStoreProvider implements DataStoreProvider
{
    @Override
    public final ManagedDataStore getDataStore(final ConfigurationSource configurationSource)
            throws DataStoreException
    {
        try {
            S3DataStoreConfiguration storageServiceDataStoreConfiguration = configurationSource.getConfiguration(S3DataStoreConfiguration.class);
            return new S3DataStore(storageServiceDataStoreConfiguration);
        } catch (ConfigurationException e) {
            throw new DataStoreException("Cannot create object data store", e);
        }
    }
}