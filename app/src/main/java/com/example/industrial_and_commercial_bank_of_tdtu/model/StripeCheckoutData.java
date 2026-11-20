package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu /api/transactions/stripe_checkout.php.
 */
public class StripeCheckoutData {
    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("checkout_url")
    private String checkoutUrl;

    @SerializedName("success_url_prefix")
    private String successUrlPrefix;

    @SerializedName("cancel_url_prefix")
    private String cancelUrlPrefix;

    public String getSessionId() { return sessionId; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getSuccessUrlPrefix() { return successUrlPrefix; }
    public String getCancelUrlPrefix() { return cancelUrlPrefix; }
}
