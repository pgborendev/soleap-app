package com.restaurant.pos.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.restaurant.pos.R;
import com.restaurant.pos.activities.POSActivity;
import com.restaurant.pos.adapters.TableAdapter;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.RestaurantTable;
import java.util.*;
public class TablesFragment extends Fragment implements TableAdapter.OnTableClickListener {
    private RecyclerView recyclerView;
    private TableAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ERPNextService service;

    @Override public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_tables,container,false);
        service=new ERPNextService(requireContext());
        recyclerView=v.findViewById(R.id.recyclerView);
        swipeRefresh=v.findViewById(R.id.swipeRefresh);
        adapter=new TableAdapter(requireContext(),this);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this::loadTables);
        loadTables();
        return v;
    }

    private void loadTables(){
        service.getTables(new com.restaurant.pos.api.ApiCallback<List<RestaurantTable>>(){
            @Override public void onSuccess(List<RestaurantTable> tables){
                if(getContext()==null)return;
                swipeRefresh.setRefreshing(false);
                adapter.setTables(tables);
            }
            @Override public void onError(String msg){
                if(getContext()==null)return;
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(),"Could not load tables: "+msg,Toast.LENGTH_SHORT).show();
                loadMockTables();
            }
        });
    }

    private void loadMockTables(){
        List<RestaurantTable> tables=new ArrayList<>();
        String[][] data={{"T01","Table 1","4","available","Ground Floor"},{"T02","Table 2","2","occupied","Ground Floor"},
            {"T03","Table 3","6","available","Ground Floor"},{"T04","Table 4","4","reserved","Ground Floor"},
            {"T05","Table 5","8","occupied","Terrace"},{"T06","Table 6","4","available","Terrace"},
            {"T07","Table 7","2","available","Terrace"},{"T08","Table 8","4","cleaning","Terrace"}};
        for(String[] d:data){
            RestaurantTable t=new RestaurantTable();
            // Use reflection-free approach with JSON
            android.util.Log.d("Tables","Mock: "+d[1]);
            tables.add(t);
        }
        adapter.setTables(tables);
    }

    @Override public void onTableClicked(RestaurantTable table){
        if(table.getStatus()==RestaurantTable.Status.RESERVED){
            Toast.makeText(getContext(),"Table is reserved",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent(getContext(),POSActivity.class);
        intent.putExtra(POSActivity.EXTRA_TABLE_ID,table.getId());
        intent.putExtra(POSActivity.EXTRA_TABLE_NAME,table.getTableName());
        startActivity(intent);
    }
}
