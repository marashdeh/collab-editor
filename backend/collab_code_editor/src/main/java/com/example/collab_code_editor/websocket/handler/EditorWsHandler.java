package com.example.collab_code_editor.websocket.handler;

import com.example.collab_code_editor.core.dto.CollaboratorDto;
import com.example.collab_code_editor.core.dto.CollaboratorUpdateMessage;
import com.example.collab_code_editor.core.dto.CursorMessage;
import com.example.collab_code_editor.core.dto.TypingMessage;
import com.example.collab_code_editor.core.dto.ws.CursorUpdateMessage;
import com.example.collab_code_editor.core.dto.ws.DocumentJoinMessage;
import com.example.collab_code_editor.core.dto.ws.EditorChangeMessage;
import com.example.collab_code_editor.core.model.CollaboratorRole;
import com.example.collab_code_editor.core.model.ProjectFile;
import com.example.collab_code_editor.core.service.FileVersionService;
import com.example.collab_code_editor.core.service.ProjectFileService;
import com.example.collab_code_editor.core.util.ColorUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditorWsHandler implements WebSocketHandler {
    private final ProjectFileService projectFileService;
    private final FileVersionService fileVersionService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    // Maps fileId : all sessions editing that file
    private final Map<Long, Set<WebSocketSession>> fileRooms = new ConcurrentHashMap<>();

    // Maps sessionId : fileId the user joined
    private final Map<String, Long> sessionToFile = new ConcurrentHashMap<>();
    // sessionId : collaborator info
    private final Map<String, CollaboratorDto> sessionToCollaborator = new ConcurrentHashMap<>();

    // fileId : set of collaborators currently in that file
    private final Map<Long, Set<CollaboratorDto>> fileCollaborators = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String userIdStr = (String) session.getAttributes().get("userEmail");
        Long userId = Long.valueOf(userIdStr);

        String color = ColorUtil.generateColor(userId);  // 🎨 assign stable color

        CollaboratorDto collaborator = CollaboratorDto.builder()
                .id(null)
                .userId(userId)
                .username("User-" + userId)
                .role(CollaboratorRole.MEMBER)
                .color(color)                     // 🎨 ADD THIS
                .build();

        sessionToCollaborator.put(session.getId(), collaborator);
        log.info(" Connected: {} with color {}", collaborator.getUsername(), collaborator.getColor());

    }



    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();

        JsonNode json = objectMapper.readTree(payload);
        String type = json.get("type").asText();

        switch (type) {
            case "JOIN" -> handleJoin(session, json);
            case "EDIT" -> handleEdit(session, json);
            case "CURSOR" -> handleCursor(session, json);
            case "TYPING" -> handleTyping(session, json);
            default -> log.warn("⚠ Unknown message type: {}", type);
        }
    }

    private void handleJoin(WebSocketSession session, JsonNode json) throws Exception {
        DocumentJoinMessage join = objectMapper.treeToValue(json, DocumentJoinMessage.class);
        Long fileId = join.getFileId();

        // 1) add session to WebSocket room
        fileRooms.putIfAbsent(fileId, ConcurrentHashMap.newKeySet());
        fileRooms.get(fileId).add(session);

        sessionToFile.put(session.getId(), fileId);

        // 2) add collaborator to file's active list
        CollaboratorDto collaborator = sessionToCollaborator.get(session.getId());
        if (collaborator != null) {
            fileCollaborators.putIfAbsent(fileId, ConcurrentHashMap.newKeySet());
            fileCollaborators.get(fileId).add(collaborator);
        }

        log.info("📄 {} joined file {} (session {})",
                collaborator != null ? collaborator.getUsername() : "Unknown",
                fileId,
                session.getId());

        // 3) send JOINED confirmation to this client
        session.sendMessage(new TextMessage("{\"type\":\"JOINED\",\"fileId\":" + fileId + "}"));

        // 4) send updated collaborator list to everyone in that file
        broadcastCollaborators(fileId);
    }


    private void handleEdit(WebSocketSession session, JsonNode json) throws Exception {

        EditorChangeMessage edit = objectMapper.treeToValue(json, EditorChangeMessage.class);
        Long fileId = edit.getFileId();

        // Get file
        ProjectFile file = projectFileService.getFile(fileId);

        //  Persist content to ProjectFile
        projectFileService.updateFileContent(fileId, edit.getContent());

        //  Create a version entry
        fileVersionService.saveNewVersion(file, edit.getContent());

        // Broadcast to the room
        broadcastToFile(fileId, json.toString(), session);

        log.info(" Saved version for file {} (session {})", fileId, session.getId());
    }


    private void handleCursor(WebSocketSession session, JsonNode json) throws Exception {
        CursorMessage msg = objectMapper.treeToValue(json, CursorMessage.class);
        Long fileId = msg.getFileId();

        // attach correct userId
        CollaboratorDto col = sessionToCollaborator.get(session.getId());
        if (col != null) {
            msg.setUserId(col.getUserId());
            msg.setColor(col.getColor());
        }

        String out = objectMapper.writeValueAsString(msg);

        // broadcast to all except sender
        broadcastToFile(fileId, out, session);

        log.info("🖱 Cursor move from user {} -> line {}, col {}",
                msg.getUserId(), msg.getLine(), msg.getColumn());
    }



    // Broadcast to everyone editing the same file (except sender)
    private void broadcastToFile(Long fileId, String message, WebSocketSession sender) throws Exception {
        Set<WebSocketSession> room = fileRooms.get(fileId);

        if (room == null) return;

        for (WebSocketSession s : room) {
            if (!s.getId().equals(sender.getId()) && s.isOpen()) {
                s.sendMessage(new TextMessage(message));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error(" WebSocket error: {}", exception.getMessage());
        cleanupSession(session);
    }

    private void handleTyping(WebSocketSession session, JsonNode json) throws Exception {
        TypingMessage msg = objectMapper.treeToValue(json, TypingMessage.class);
        Long fileId = msg.getFileId();

        // attach the correct userId based on session
        CollaboratorDto col = sessionToCollaborator.get(session.getId());
        if (col != null) {
            msg.setUserId(col.getUserId());
        }

        String out = objectMapper.writeValueAsString(msg);

        // send to others only
        broadcastToFile(fileId, out, session);

        log.info("⌨️ TYPING from user {} in file {}", msg.getUserId(), fileId);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("🔥 afterConnectionClosed CALLED for {}", session.getId());
        log.info("🔥 Close status: {}", status);
        cleanupSession(session);
    }



    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void cleanupSession(WebSocketSession session) {
        String sessionId = session.getId();
        Long fileId = sessionToFile.remove(sessionId);
        CollaboratorDto collaborator = sessionToCollaborator.remove(sessionId);

        if (fileId != null) {
            Set<WebSocketSession> room = fileRooms.get(fileId);
            if (room != null) {
                room.remove(session);
            }

            Set<CollaboratorDto> collabs = fileCollaborators.get(fileId);
            if (collabs != null && collaborator != null) {
                collabs.remove(collaborator);
                if (collabs.isEmpty()) {
                    fileCollaborators.remove(fileId);
                }
            }

            log.info(" {} left file {}",
                    collaborator != null ? collaborator.getUsername() : "Unknown",
                    fileId);

            // notify remaining users about updated list
            broadcastCollaborators(fileId);
        }
    }



    private void broadcastCollaborators(Long fileId) {
        Set<WebSocketSession> room = fileRooms.get(fileId);
        Set<CollaboratorDto> collabs = fileCollaborators.get(fileId);

        if (room == null || collabs == null) {
            return;
        }

        CollaboratorUpdateMessage msg = new CollaboratorUpdateMessage();
        msg.setFileId(fileId);
        msg.setCollaborators(new ArrayList<>(collabs));

        try {
            String json = objectMapper.writeValueAsString(msg);

            for (WebSocketSession s : room) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(json));
                }
            }

            log.info(" Sent COLLAB_UPDATE for file {} to {} sessions", fileId, room.size());
        } catch (Exception e) {
            log.error("Error broadcasting collaborators for file {}: {}", fileId, e.getMessage());
        }
    }
    private String generateColor(Long userId) {
        String[] colors = {
                "#FF4C4C", "#4C8CFF", "#4CFF72", "#FFB84C", "#B84CFF",
                "#FF4CE0", "#4CFFD9", "#FFD24C", "#FF6F91", "#6F4CFF"
        };

        // stable color based on userId
        int index = (int) (userId % colors.length);
        return colors[index];
    }


}

