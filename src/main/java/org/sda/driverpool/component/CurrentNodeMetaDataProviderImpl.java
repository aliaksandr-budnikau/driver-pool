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
        this.currentNodeId = generateId();
        log.info("The node id is {}", this.currentNodeId);
    }

    @Override
    public String generateId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.split("-")[0] + uuid.split("-")[1];
    }

    @Override
    public String getCurrentNodeId() {
        return currentNodeId;
    }
}