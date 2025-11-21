package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.DiscussionDto;
import com.example.collab_code_editor.core.exception.FileNotFoundException;
import com.example.collab_code_editor.core.exception.UserNotFoundException;
import com.example.collab_code_editor.core.model.Discussion;
import com.example.collab_code_editor.core.model.ProjectFile;
import com.example.collab_code_editor.core.model.User;
import com.example.collab_code_editor.core.service.DiscussionService;
import com.example.collab_code_editor.infrastructure.repository.DiscussionRepository;
import com.example.collab_code_editor.infrastructure.repository.ProjectFileRepository;
import com.example.collab_code_editor.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DiscussionServiceImpl implements DiscussionService {

    private final ProjectFileRepository fileRepository;
    private final UserRepository userRepository;
    private final DiscussionRepository discussionRepository;
    @Override
    public DiscussionDto createDiscussion(Long fileId, Long userId, String topic) {
        ProjectFile file = fileRepository.findById(fileId)
                .orElseThrow(()-> new FileNotFoundException("File not found"));
        User creator = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("user not found"));

        Discussion discussion = new Discussion();
        discussion.setFile(file);
        discussion.setCreator(creator);
        discussion.setTopic(topic);
        discussionRepository.save(discussion);

        return new DiscussionDto(discussion.getId(),topic,creator.getUsername(),discussion.getCreatedAt());
    }

    @Override
    public List<DiscussionDto> getDiscussionByFile(Long fileId) {
        return discussionRepository.findByFileId(fileId)
                .stream()
                .map(d-> new DiscussionDto(d.getId(), d.getTopic(), d.getCreator().getUsername() ,d.getCreatedAt() ))
                .toList();
    }
}
