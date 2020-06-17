package com.hospital.repository;

import com.hospital.entities.Conversation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation,Long> {

    Conversation findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Conversation> findAllBySenderIdOrReceiverId(Long senderId,Long receiverId,  Sort sort);
}
