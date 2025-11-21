package com.example.collab_code_editor.config.websocket;



import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import com.example.collab_code_editor.websocket.handler.EditorWsHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final EditorWsHandler editorWsHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(editorWsHandler, "/ws/editor")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*"); // allow all origins for now
    }
}