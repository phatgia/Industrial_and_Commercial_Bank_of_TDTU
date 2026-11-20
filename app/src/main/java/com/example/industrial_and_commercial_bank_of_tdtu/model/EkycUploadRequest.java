package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload gui ho so eKYC (khuon mat + mat truoc/sau CCCD) len /api/ekyc/upload.php.
 */
public class EkycUploadRequest {
    @SerializedName("image_base64")
    private final String imageBase64;

    @SerializedName("id_front_base64")
    private final String idFrontBase64;

    @SerializedName("id_back_base64")
    private final String idBackBase64;

    public EkycUploadRequest(String imageBase64) {
        this(imageBase64, null, null);
    }

    public EkycUploadRequest(String imageBase64, String idFrontBase64, String idBackBase64) {
        this.imageBase64 = imageBase64;
        this.idFrontBase64 = idFrontBase64;
        this.idBackBase64 = idBackBase64;
    }
}
