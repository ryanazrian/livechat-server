package com.example.ryanazrian.livechat.chat;

/**
 * Room-key conventions for the chat feature.
 *   global      -> "global"                                   (any authenticated user)
 *   DM          -> "dm:<smallerUserId>:<largerUserId>"        (two participants only)
 * DM keys are built symmetrically so both peers derive the same key regardless of who initiates.
 */
public final class ChatRooms {

    public static final String GLOBAL_KEY = "global";
    public static final String GLOBAL_SUBSCRIBE_PREFIX = "/topic/chat/global";
    public static final String DM_SUBSCRIBE_PREFIX = "/topic/chat/dm/";

    private ChatRooms() {
    }

    public static String dmKey(String a, String b) {
        String first = a.compareTo(b) <= 0 ? a : b;
        String second = a.compareTo(b) <= 0 ? b : a;
        return "dm:" + first + ":" + second;
    }

    public static boolean isGlobal(String roomKey) {
        return GLOBAL_KEY.equals(roomKey);
    }

    public static boolean isDm(String roomKey) {
        return roomKey != null && roomKey.startsWith("dm:");
    }

    /** @return true if {@code sub} is one of the two participants encoded in a dm roomKey. */
    public static boolean isParticipant(String roomKey, String sub) {
        if (!isDm(roomKey) || sub == null) {
            return false;
        }
        String[] parts = roomKey.split(":");
        return parts.length == 3 && (sub.equals(parts[1]) || sub.equals(parts[2]));
    }

    /** @return the other participant in a dm roomKey, or null if not a participant. */
    public static String otherParticipant(String roomKey, String sub) {
        if (!isParticipant(roomKey, sub)) {
            return null;
        }
        String[] parts = roomKey.split(":");
        return sub.equals(parts[1]) ? parts[2] : parts[1];
    }

    /** STOMP destination for a room key. */
    public static String destination(String roomKey) {
        return isGlobal(roomKey) ? GLOBAL_SUBSCRIBE_PREFIX : DM_SUBSCRIBE_PREFIX + roomKey;
    }
}
