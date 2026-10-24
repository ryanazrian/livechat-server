package com.example.ryanazrian.livechat.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findByRoomKeyOrderByCreatedAtAsc(String roomKey);

    List<ChatMessage> findTop50ByRoomKeyOrderByCreatedAtDesc(String roomKey);

    @Query("select m from ChatMessage m where m.roomKey = :roomKey "
            + "and (:after is null or m.createdAt > :after) order by m.createdAt asc")
    List<ChatMessage> findSince(@Param("roomKey") String roomKey, @Param("after") Instant after);

    /** Distinct DM room keys (presence-bearing), filtered to the caller in the service. */
    @Query("select distinct m.roomKey from ChatMessage m where m.roomKey like 'dm:%'")
    List<String> findDmRoomKeys();
}
