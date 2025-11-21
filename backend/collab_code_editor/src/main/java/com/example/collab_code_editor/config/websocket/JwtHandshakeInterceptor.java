package com.example.collab_code_editor.config.websocket;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import com.example.collab_code_editor.core.security.JwtTokenService;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenService jwtTokenService;

    @Override
    public boolean beforeHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        try {
            // Extract Authorization header
            HttpHeaders headers = request.getHeaders();
            String authHeader = headers.getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("❌ Missing or invalid Authorization header");
                return false;
            }

            String token = authHeader.substring(7);

            // Validate the token
            if (!jwtTokenService.validateToken(token)) {
                log.warn("❌ Invalid JWT Token");
                return false;
            }

            // Extract user email (or ID) from token
            String email = jwtTokenService.extractUsername(token);

            // Store the user in WebSocket session attributes
            attributes.put("userEmail", email);

            log.info("✅ WebSocket handshake authenticated for: {}", email);
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
            WebSocketHandler wsHandler,
            Exception exception) {
    }
}