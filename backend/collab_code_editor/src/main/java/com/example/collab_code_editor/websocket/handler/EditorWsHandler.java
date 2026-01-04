package com.example.collab_code_editor.websocket.handler;

import com.example.collab_code_editor.core.service.ProjectFileService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditorWsHandler extends TextWebSocketHandler {

    private final ProjectFileService projectFileService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Active User Sessions
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // Maps fileId -> Set of Sessions (The "Room")
    private final Map<Long, Set<WebSocketSession>> fileRooms = new ConcurrentHashMap<>();

    // Maps SessionID -> FileID (To know which room to leave)
    private final Map<String, Long> sessionToFile = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object userIdObj = session.getAttributes().get("userId");
        String email = (String) session.getAttributes().get("userEmail");

        if (userIdObj == null) {
            System.out.println("❌ [WS] Connection Rejected: No User ID");
            session.close();
            return;
        }

        Long userId = Long.valueOf(userIdObj.toString());
        sessions.put(userId, session);
        System.out.println("✅ [WS] Connected: " + email + " (ID: " + userId + ")");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            System.out.println("📩 [WS] Received Message: " + payload); // DEBUG LOG

            JsonNode json = objectMapper.readTree(payload);
            String type = json.get("type").asText();

            if ("JOIN".equals(type)) {
                handleJoin(session, json);
            } else if ("EDIT".equals(type)) {
                handleEdit(session, json);
            } else if ("CURSOR".equals(type)) {
                broadcastToRoom(session, message);
            }
        } catch (Exception e) {
            System.err.println("❌ [WS] Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleJoin(WebSocketSession session, JsonNode json) {
        Long fileId = json.get("fileId").asLong();

        fileRooms.computeIfAbsent(fileId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionToFile.put(session.getId(), fileId);

        System.out.println("📂 [WS] Session joined Room (File ID: " + fileId + ")");
        System.out.println("👥 [WS] Users in Room " + fileId + ": " + fileRooms.get(fileId).size());
    }

    private void handleEdit(WebSocketSession session, JsonNode json) {
        Long fileId = json.get("fileId").asLong();
        String content = json.get("content").asText();

        try {
            projectFileService.updateFileContent(fileId, content);
        } catch (Exception e) {
            log.error("Error saving file content to DB", e);
        }

        // Broadcast to others in the room
        broadcastToRoom(session, new TextMessage(json.toString()));
    }

    private void broadcastToRoom(WebSocketSession sender, TextMessage message) {
        Long fileId = sessionToFile.get(sender.getId());

        if (fileId == null) {
            System.err.println("⚠️ [WS] Broadcast failed: Sender is not in a room!");
            return;
        }

        Set<WebSocketSession> room = fileRooms.get(fileId);
        if (room != null) {
            for (WebSocketSession client : room) {
                // Send to everyone EXCEPT the sender
                if (client.isOpen() && !client.getId().equals(sender.getId())) {
                    try {
                        client.sendMessage(message);
                        System.out.println("📡 [WS] Sent update to client: " + client.getId());
                    } catch (Exception e) {
                        System.err.println("❌ [WS] Send failed: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) sessions.remove(userId);

        Long fileId = sessionToFile.remove(session.getId());
        if (fileId != null) {
            Set<WebSocketSession> room = fileRooms.get(fileId);
            if (room != null) {
                room.remove(session);
                if (room.isEmpty()) fileRooms.remove(fileId);
            }
        }
        System.out.println("❌ [WS] Disconnected: " + session.getId());
    }
}