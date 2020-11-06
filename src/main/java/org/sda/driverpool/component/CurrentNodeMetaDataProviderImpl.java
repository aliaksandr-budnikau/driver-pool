package org.sda.driverpool.component;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Log4j2
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