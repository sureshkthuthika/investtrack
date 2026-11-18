package com.investtrack.backend.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public JwtChannelInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authorizationHeaders =
                    accessor.getNativeHeader("Authorization");

            if (authorizationHeaders == null ||
                    authorizationHeaders.isEmpty()) {
                throw new IllegalArgumentException(
                        "Missing Authorization header");
            }

            String token = authorizationHeaders.get(0);

            if (!token.startsWith("Bearer ")) {
                throw new IllegalArgumentException(
                        "Invalid Authorization header");
            }

            Jwt jwt = jwtDecoder.decode(
                    token.substring(7));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            jwt.getSubject(),
                            null,
                            List.of());

            accessor.setUser(authentication);
        }

        return message;
    }
}
