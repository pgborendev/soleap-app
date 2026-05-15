package com.restaurant.pos.api;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.restaurant.pos.models.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import okhttp3.*;
public class ERPNextService {
    private static final MediaType JSON=MediaType.get("application/json; charset=utf-8");
    private final ERPNextClient client;
    private final Gson gson=new Gson();
    private final Handler mainHandler=new Handler(Looper.getMainLooper());
    private final ExecutorService exec=Executors.newCachedThreadPool();

    public ERPNextService(Context ctx){client=ERPNextClient.getInstance(ctx);}

    public void testConnection(String url,String key,String secret,ApiCallback<Boolean> cb){
        exec.execute(()->{
            try{
                Request req=new Request.Builder().url(url+"/api/method/frappe.auth.get_logged_user")
                    .header("Authorization","token "+key+":"+secret).get().build();
                try(Response r=client.getHttpClient().newCall(req).execute()){
                    boolean ok=r.isSuccessful();
                    mainHandler.post(()->{ if(ok)cb.onSuccess(true); else cb.onError("HTTP "+r.code()); });
                }
            }catch(Exception e){mainHandler.post(()->cb.onError("Connection failed: "+e.getMessage()));}
        });
    }

    public void getMenuItems(ApiCallback<List<MenuItem>> cb){
        String url=client.getBaseUrl()+"/api/resource/Item?filters=[[\"disabled\",\"=\",0]]&fields=[\"name\",\"item_name\",\"item_code\",\"standard_rate\",\"item_group\",\"image\",\"description\"]&limit_page_length=200";
        getList(url,MenuItem.class,cb);
    }

    public void getTables(ApiCallback<List<RestaurantTable>> cb){
        String url=client.getBaseUrl()+"/api/resource/Restaurant Table?fields=[\"name\",\"table_name\",\"seating_capacity\",\"status\",\"floor\",\"current_order\"]&limit_page_length=100";
        getList(url,RestaurantTable.class,cb);
    }

    public void updateTableStatus(String tableId,String status,ApiCallback<Boolean> cb){
        exec.execute(()->{
            try{
                JsonObject body=new JsonObject();body.addProperty("status",status);
                String url=client.getBaseUrl()+"/api/resource/Restaurant Table/"+tableId;
                Request req=new Request.Builder().url(url).put(RequestBody.create(gson.toJson(body),JSON)).build();
                try(Response r=client.getHttpClient().newCall(req).execute()){
                    boolean ok=r.isSuccessful();
                    mainHandler.post(()->{if(ok)cb.onSuccess(true);else cb.onError("Failed: "+r.code());});
                }
            }catch(Exception e){mainHandler.post(()->cb.onError(e.getMessage()));}
        });
    }

    public void getActiveOrders(ApiCallback<List<Order>> cb){
        String url=client.getBaseUrl()+"/api/resource/POS Invoice?filters=[[\"status\",\"in\",[\"Draft\",\"Submitted\"]]]&fields=[\"name\",\"customer\",\"grand_total\",\"net_total\",\"total_taxes_and_charges\",\"status\",\"posting_date\",\"posting_time\",\"items\",\"table\",\"waiter\"]&limit_page_length=50&order_by=creation desc";
        getList(url,Order.class,cb);
    }

    public void createPOSInvoice(Order order,ApiCallback<Order> cb){
        exec.execute(()->{
            try{
                ERPConfig cfg=client.getConfig();
                JsonObject body=new JsonObject();
                body.addProperty("doctype","POS Invoice");
                body.addProperty("pos_profile",cfg.getPosProfile());
                body.addProperty("company",cfg.getCompany());
                body.addProperty("customer",order.getCustomer()!=null?order.getCustomer():"Walk-in Customer");
                if(order.getTableId()!=null)body.addProperty("table",order.getTableId());
                if(order.getWaiter()!=null)body.addProperty("waiter",order.getWaiter());
                JsonArray items=new JsonArray();
                for(Order.OrderItem item:order.getItems()){
                    JsonObject i=new JsonObject();
                    i.addProperty("item_code",item.getItemCode());
                    i.addProperty("item_name",item.getItemName());
                    i.addProperty("qty",item.getQty());
                    i.addProperty("rate",item.getRate());
                    i.addProperty("uom","Nos");
                    items.add(i);
                }
                body.add("items",items);
                String url=client.getBaseUrl()+"/api/resource/POS Invoice";
                Request req=new Request.Builder().url(url).post(RequestBody.create(gson.toJson(body),JSON)).build();
                try(Response r=client.getHttpClient().newCall(req).execute()){
                    String rb=r.body()!=null?r.body().string():"";
                    if(r.isSuccessful()){
                        JsonObject json=JsonParser.parseString(rb).getAsJsonObject();
                        Order created=gson.fromJson(json.get("data"),Order.class);
                        mainHandler.post(()->cb.onSuccess(created));
                    }else{mainHandler.post(()->cb.onError("Failed: "+r.code()));}
                }
            }catch(Exception e){mainHandler.post(()->cb.onError(e.getMessage()));}
        });
    }

    public void processPayment(PaymentEntry payment,ApiCallback<Boolean> cb){
        exec.execute(()->{
            try{
                String url=client.getBaseUrl()+"/api/method/erpnext.accounts.doctype.pos_invoice.pos_invoice.make_posa_payment_entry";
                JsonObject body=new JsonObject();
                body.addProperty("invoice",payment.getInvoiceName());
                body.addProperty("payment_method",payment.getMethodString());
                body.addProperty("amount",payment.getAmount());
                if(payment.getReferenceNo()!=null)body.addProperty("reference_no",payment.getReferenceNo());
                Request req=new Request.Builder().url(url).post(RequestBody.create(gson.toJson(body),JSON)).build();
                try(Response r=client.getHttpClient().newCall(req).execute()){
                    boolean ok=r.isSuccessful();
                    mainHandler.post(()->{if(ok)cb.onSuccess(true);else cb.onError("Payment failed: "+r.code());});
                }
            }catch(Exception e){mainHandler.post(()->cb.onError(e.getMessage()));}
        });
    }

    private <T> void getList(String url,Class<T> clazz,ApiCallback<List<T>> cb){
        exec.execute(()->{
            try{
                Request req=new Request.Builder().url(url).get().build();
                try(Response r=client.getHttpClient().newCall(req).execute()){
                    String body=r.body()!=null?r.body().string():"";
                    if(r.isSuccessful()){
                        JsonObject json=JsonParser.parseString(body).getAsJsonObject();
                        JsonArray data=json.getAsJsonArray("data");
                        List<T> list=new ArrayList<>();
                        for(int i=0;i<data.size();i++)list.add(gson.fromJson(data.get(i),clazz));
                        mainHandler.post(()->cb.onSuccess(list));
                    }else{mainHandler.post(()->cb.onError("HTTP "+r.code()));}
                }
            }catch(Exception e){mainHandler.post(()->cb.onError(e.getMessage()));}
        });
    }
}
