package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu /api/ekyc/upload.php.
 */
public class EkycUploadData {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("ekyc_face_path")
    private String ekycFacePath;

    @SerializedName("id_front_path")
    private String idFrontPath;

    @SerializedName("id_back_path")
    private String idBackPath;

    @SerializedName("ekyc_verified")
    private boolean ekycVerified;

    public int getUserId() { return userId; }
    public String getEkycFacePath() { return ekycFacePath; }
    public String getIdFrontPath() { return idFrontPath; }
    public String getIdBackPath() { return idBackPath; }
    public boolean isEkycVerified() { return ekycVerified; }
}
