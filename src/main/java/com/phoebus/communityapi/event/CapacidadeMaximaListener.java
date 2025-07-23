package com.phoebus.communityapi.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CapacidadeMaximaListener {
    private static final Logger logger = LoggerFactory.getLogger(CapacidadeMaximaListener.class);

    @EventListener
    public void handleCapacidadeMaximaEvent(CapacidadeMaximaEvent event) {
        logger.info("Notificação: Centro {} atingiu sua capacidade máxima de {}.",
                event.getCentro().getNome(), event.getCentro().getCapacidadeMaxima());
    }
}