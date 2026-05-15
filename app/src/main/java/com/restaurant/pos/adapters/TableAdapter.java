package com.restaurant.pos.adapters;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.pos.R;
import com.restaurant.pos.models.RestaurantTable;
import java.util.*;
public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    public interface OnTableClickListener { void onTableClicked(RestaurantTable table); }
    private List<RestaurantTable> tables=new ArrayList<>();
    private final Context ctx;
    private final OnTableClickListener listener;
    public TableAdapter(Context ctx,OnTableClickListener l){this.ctx=ctx;this.listener=l;}
    public void setTables(List<RestaurantTable> t){tables=t;notifyDataSetChanged();}
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View v=LayoutInflater.from(ctx).inflate(R.layout.item_table,parent,false);
        return new ViewHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder h,int pos){
        RestaurantTable t=tables.get(pos);
        h.tvName.setText(t.getTableName());
        h.tvCapacity.setText(t.getCapacity()+" seats");
        h.tvFloor.setText(t.getFloor());
        int colorRes,statusText;
        switch(t.getStatus()){
            case OCCUPIED: colorRes=R.color.accent;statusText=R.string.status_occupied;break;
            case RESERVED: colorRes=R.color.amber;statusText=R.string.status_reserved;break;
            case CLEANING: colorRes=R.color.blue;statusText=R.string.status_cleaning;break;
            default: colorRes=R.color.green;statusText=R.string.status_available;
        }
        h.tvStatus.setText(statusText);
        h.tvStatus.setTextColor(ContextCompat.getColor(ctx,colorRes));
        h.card.setStrokeColor(ContextCompat.getColor(ctx,colorRes));
        h.card.setOnClickListener(v->listener.onTableClicked(t));
    }
    @Override public int getItemCount(){return tables.size();}
    static class ViewHolder extends RecyclerView.ViewHolder{
        com.google.android.material.card.MaterialCardView card;
        TextView tvName,tvCapacity,tvFloor,tvStatus;
        ViewHolder(View v){
            super(v);card=v.findViewById(R.id.card);tvName=v.findViewById(R.id.tvName);
            tvCapacity=v.findViewById(R.id.tvCapacity);tvFloor=v.findViewById(R.id.tvFloor);
            tvStatus=v.findViewById(R.id.tvStatus);
        }
    }
}
