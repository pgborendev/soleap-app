package com.restaurant.pos.api;
import android.content.Context;
import android.util.Log;
import com.restaurant.pos.models.ERPConfig;
import com.restaurant.pos.utils.SessionManager;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
public class ERPNextClient {
    private static final String TAG="ERPNextClient";
    private static ERPNextClient instance;
    private OkHttpClient httpClient;
    private ERPConfig config;
    private ERPNextClient(Context ctx){
        config=SessionManager.getInstance(ctx).getERPConfig();
        buildClient();
    }
    public static synchronized ERPNextClient getInstance(Context ctx){
        if(instance==null)instance=new ERPNextClient(ctx.getApplicationContext());
        return instance;
    }
    public static void resetInstance(){instance=null;}
    private void buildClient(){
        HttpLoggingInterceptor log=new HttpLoggingInterceptor(m->Log.d(TAG,m));
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor auth=chain->{
            Request.Builder b=chain.request().newBuilder()
                .header("Authorization",config!=null?config.getAuthHeader():"")
                .header("Content-Type","application/json")
                .header("Accept","application/json");
            return chain.proceed(b.build());
        };
        httpClient=new OkHttpClient.Builder()
            .addInterceptor(auth).addInterceptor(log)
            .connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .build();
    }
    public OkHttpClient getHttpClient(){return httpClient;}
    public ERPConfig getConfig(){return config;}
    public void updateConfig(ERPConfig c){config=c;buildClient();}
    public String getBaseUrl(){return config!=null?config.getBaseUrl():"";}
}
