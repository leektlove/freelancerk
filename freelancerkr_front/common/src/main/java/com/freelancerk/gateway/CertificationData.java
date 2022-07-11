package com.freelancerk.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CertificationData {
    @JsonProperty("imp_uid")
    private String impUid;
    @JsonProperty("merchant_uid")
    private String merchantUid;
    @JsonProperty("pg_tid")
    private String pgTid;
    @JsonProperty("pg_provider")
    private String pgProvider;
    @JsonProperty("name")
    private String name;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("birth")
    private int birth;
    @JsonProperty("foreigner")
    private boolean foreigner;
    @JsonProperty("certified")
    private boolean certified;
    @JsonProperty("certified_at")
    private long certifiedAt;
    @JsonProperty("unique_key")
    private String uniqueKey;
    @JsonProperty("unique_in_site")
    private String uniqueInSite;
    @JsonProperty("origin")
    private String origin;
    private String phone;
    private String carrier;
}
