package org.sda.driverpool.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
@Component
public class CurrentNodeMetaDataProviderImpl implements CurrentNodeMetaDataProvider {

    private String currentNodeId;

    @PostConstruct
    public void init() {
        String uuid = UUID.randomUUID().toString();
        currentNodeId = uuid.split("-")[0] + uuid.split("-")[1];
        log.info("The node id is {}", currentNodeId);
    }

    @Override
    public String getCurrentNodeId() {
        return currentNodeId;
    }
}