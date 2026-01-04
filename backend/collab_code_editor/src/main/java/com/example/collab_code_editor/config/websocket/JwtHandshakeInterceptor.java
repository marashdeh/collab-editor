package com.example.collab_code_editor.config.websocket;

import com.example.collab_code_editor.core.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenService jwtTokenService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        try {
            String token = null;

            // 1. Try getting token from Authorization Header (Bearer ...)
            List<String> authHeader = request.getHeaders().get("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                String bearer = authHeader.get(0);
                if (bearer.startsWith("Bearer ")) {
                    token = bearer.substring(7);
                }
            }

            // 2. If Header is missing, try getting token from URL Query Param (?token=...)
            // This is required for Browsers connecting to WebSocket
            if (token == null) {
                String query = request.getURI().getQuery();
                if (query != null && query.contains("token=")) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("token=")) {
                            token = param.substring(6);
                            break;
                        }
                    }
                }
            }

            // 3. Validate the Token
            if (token == null) {
                log.warn("❌ WebSocket Handshake failed: Missing token in Header and URL");
                return false;
            }

            if (!jwtTokenService.validateToken(token)) {
                log.warn("❌ WebSocket Handshake failed: Invalid JWT Token");
                return false;
            }

            // 4. Success: Extract User Info and store in Session Attributes
            String email = jwtTokenService.extractUsername(token);
            Long userId = jwtTokenService.extractUserId(token);

            if (userId == null) {
                log.error("❌ WebSocket Handshake failed: Token valid but User ID claim is null");
                return false;
            }

            // These attributes are passed to the WebSocket Session
            attributes.put("userEmail", email);
            attributes.put("userId", userId); // ✅ Critical for EditorWsHandler

            log.info("✅ WebSocket Handshake successful for User: {} (ID: {})", email, userId);
            return true;

        } catch (Exception e) {
            log.error("❌ Error inside WebSocket JWT handshake: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            org.springframework.web.socket.WebSocketHandler wsHandler,
            Exception exception) {
        // We don't need to do anything here
    }



}