package com.topviec.topviec_be.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ExampleEvent extends ApplicationEvent {

    private final Long entityId;
    private final String action;

    public ExampleEvent(Object source, Long entityId, String action) {
        super(source);
        this.entityId = entityId;
        this.action = action;
    }
}
