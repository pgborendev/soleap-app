package com.restaurant.pos.utils;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.restaurant.pos.models.ERPConfig;
public class SessionManager {
    private static final String PREF="RestaurantPOS_Session",KEY_CFG="erp_config",KEY_IN="is_logged_in",KEY_USR="username";
    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final Gson gson=new Gson();
    private SessionManager(Context ctx){prefs=ctx.getSharedPreferences(PREF,Context.MODE_PRIVATE);}
    public static synchronized SessionManager getInstance(Context ctx){if(instance==null)instance=new SessionManager(ctx.getApplicationContext());return instance;}
    public void saveERPConfig(ERPConfig c){prefs.edit().putString(KEY_CFG,gson.toJson(c)).apply();}
    public ERPConfig getERPConfig(){String j=prefs.getString(KEY_CFG,null);return j==null?null:gson.fromJson(j,ERPConfig.class);}
    public boolean hasConfig(){return getERPConfig()!=null;}
    public void setLoggedIn(boolean v){prefs.edit().putBoolean(KEY_IN,v).apply();}
    public boolean isLoggedIn(){return prefs.getBoolean(KEY_IN,false);}
    public void setUsername(String u){prefs.edit().putString(KEY_USR,u).apply();}
    public String getUsername(){return prefs.getString(KEY_USR,"Admin");}
    public void clearSession(){prefs.edit().clear().apply();}
}
