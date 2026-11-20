package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload gui cau hoi cho tro ly AI (/api/chat/ask.php).
 */
public class ChatRequest {
    @SerializedName("message")
    private final String message;

    public ChatRequest(String message) {
        this.message = message;
    }
}
