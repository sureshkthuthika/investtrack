package com.investtrack.backend.config;

import com.investtrack.backend.repository.PortfolioRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class PortfolioSubscriptionInterceptor
        implements ChannelInterceptor {

    private static final String DESTINATION_PREFIX =
            "/topic/portfolios/";

    private final PortfolioRepository portfolioRepository;

    public PortfolioSubscriptionInterceptor(
            PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message, StompHeaderAccessor.class);

        if (accessor == null ||
                !StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return message;
        }

        Principal user = accessor.getUser();

        if (user == null) {
            throw new AccessDeniedException(
                    "WebSocket authentication required");
        }

        String destination = accessor.getDestination();

        /*if ("/user/queue/portfolio-updates".equals(destination)) {
            return message;
        }*/

        if ("/user/queue/portfolio-updates".equals(destination)) {
            System.out.println(
                    "USER QUEUE SUBSCRIBED: user="
                            + user.getName()
                            + ", destination="
                            + destination
            );
            return message;
        }

        if (destination == null ||
                !destination.startsWith(DESTINATION_PREFIX)) {
            throw new AccessDeniedException(
                    "Subscription destination not allowed");
        }

        String portfolioId =
                destination.substring(DESTINATION_PREFIX.length());

        if (portfolioId.isBlank() || portfolioId.contains("/")) {
            throw new AccessDeniedException(
                    "Invalid portfolio subscription");
        }

        boolean ownsPortfolio =
                portfolioRepository.findByIdAndOwner(
                        portfolioId, user.getName()).isPresent();

        if (!ownsPortfolio) {
            throw new AccessDeniedException(
                    "You do not have access to this portfolio");
        }

        return message;
    }
}
