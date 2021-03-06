package com.hospital.repository;

import com.hospital.entities.Conversation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation,Long> {

    List<Conversation> findAllByComptes_Id(Long id, Sort sort);
    Conversation findBySenderAndReceiver(String sender, String receiver);
}
