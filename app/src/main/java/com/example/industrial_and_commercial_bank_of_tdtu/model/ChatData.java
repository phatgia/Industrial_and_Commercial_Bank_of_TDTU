package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu /api/chat/ask.php.
 */
public class ChatData {
    @SerializedName("reply")
    private String reply;

    @SerializedName("action")
    private String action;

    public String getReply() { return reply; }
    public String getAction() { return action; }
}
