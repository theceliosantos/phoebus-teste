package com.phoebus.communityapi.event;

import com.phoebus.communityapi.model.CentroComunitario;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CapacidadeMaximaEvent extends ApplicationEvent {
    private final CentroComunitario centro;

    public CapacidadeMaximaEvent(Object source, CentroComunitario centro) {
        super(source);
        this.centro = centro;
    }
}