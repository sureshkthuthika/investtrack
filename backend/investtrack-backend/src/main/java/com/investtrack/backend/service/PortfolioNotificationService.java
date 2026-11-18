package com.investtrack.backend.service;

import com.investtrack.backend.event.PortfolioUpdatedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class PortfolioNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public PortfolioNotificationService(
            SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )

    public void handlePortfolioUpdated(PortfolioUpdatedEvent event) {

        System.out.println("NOTIFICATION LISTENER INVOKED: " + event);
        System.out.println("NOTIFICATION OWNER: " + event.owner());

        // Notify users viewing this specific portfolio
        messagingTemplate.convertAndSend(
                "/topic/portfolios/" + event.portfolioId(),
                event
        );

        // Notify all connected sessions belonging to the portfolio owner
        messagingTemplate.convertAndSendToUser(
                event.owner(),
                "/queue/portfolio-updates",
                event
        );

        System.out.println("NOTIFICATIONS SENT");
    }
}
