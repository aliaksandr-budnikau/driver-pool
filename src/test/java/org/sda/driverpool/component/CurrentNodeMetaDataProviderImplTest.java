package org.sda.driverpool.component;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CurrentNodeMetaDataProviderImplTest {

    @Test
    public void getCurrentNodeId() {
        CurrentNodeMetaDataProviderImpl provider = new CurrentNodeMetaDataProviderImpl();
        provider.init();
        assertNotNull(provider.getCurrentNodeId());
        assertEquals(12, provider.getCurrentNodeId().length());
    }
}