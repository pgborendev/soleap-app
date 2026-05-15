package com.restaurant.pos.models;
public class ERPConfig {
    private String baseUrl, apiKey, apiSecret, posProfile, company, warehouse;
    public ERPConfig() {}
    public ERPConfig(String baseUrl, String apiKey, String apiSecret, String posProfile) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0,baseUrl.length()-1) : baseUrl;
        this.apiKey=apiKey; this.apiSecret=apiSecret; this.posProfile=posProfile;
        this.company="Restaurant Co."; this.warehouse="Stores - RC";
    }
    public String getBaseUrl(){return baseUrl;} public String getApiKey(){return apiKey;}
    public String getApiSecret(){return apiSecret;} public String getPosProfile(){return posProfile;}
    public String getCompany(){return company;} public String getWarehouse(){return warehouse;}
    public String getAuthHeader(){return "token "+apiKey+":"+apiSecret;}
    public void setBaseUrl(String v){baseUrl=v;} public void setApiKey(String v){apiKey=v;}
    public void setApiSecret(String v){apiSecret=v;} public void setPosProfile(String v){posProfile=v;}
    public void setCompany(String v){company=v;} public void setWarehouse(String v){warehouse=v;}
}
