package com.restaurant.pos.ui.tables;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.restaurant.pos.adapters.TableAdapter;
import com.restaurant.pos.databinding.FragmentTablesBinding;
import com.restaurant.pos.models.ERPModels;
import com.restaurant.pos.ui.pos.POSActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TablesFragment extends Fragment {

    private FragmentTablesBinding binding;
    private TablesViewModel viewModel;
    private TableAdapter tableAdapter;
    private List<ERPModels.RestaurantTable> allTables = new ArrayList<>();
    private String selectedFloor = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTablesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TablesViewModel.class);

        tableAdapter = new TableAdapter(new ArrayList<>(), this::onTableClick);
        binding.rvTables.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvTables.setAdapter(tableAdapter);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadTables());

        // Floor chip filters
        binding.chipAll.setOnClickListener(v -> filterByFloor("All"));
        binding.chipGround.setOnClickListener(v -> filterByFloor("Ground"));
        binding.chipTerrace.setOnClickListener(v -> filterByFloor("Terrace"));
        binding.chipVIP.setOnClickListener(v -> filterByFloor("VIP"));

        viewModel.getTables().observe(getViewLifecycleOwner(), result -> {
            binding.swipeRefresh.setRefreshing(false);
            if (result.isLoading()) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
            if (result.isSuccess() && result.data != null) {
                allTables = result.data;
                filterByFloor(selectedFloor);
            }
            if (result.isError()) {
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText(result.error);
            }
        });

        viewModel.loadTables();
    }

    private void filterByFloor(String floor) {
        selectedFloor = floor;
        List<ERPModels.RestaurantTable> filtered = "All".equals(floor)
                ? allTables
                : allTables.stream()
                    .filter(t -> floor.equals(t.floor))
                    .collect(Collectors.toList());
        tableAdapter.updateData(filtered);
    }

    private void onTableClick(ERPModels.RestaurantTable table) {
        if ("Reserved".equals(table.status)) return; // Cannot open reserved tables

        Intent intent = new Intent(requireContext(), POSActivity.class);
        intent.putExtra(POSActivity.EXTRA_TABLE_NAME, table.name);
        intent.putExtra(POSActivity.EXTRA_TABLE_DISPLAY, table.tableName);
        intent.putExtra(POSActivity.EXTRA_TABLE_CAPACITY, table.capacity);
        if (table.currentOrder != null) {
            intent.putExtra(POSActivity.EXTRA_INVOICE_NAME, table.currentOrder);
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh table status when returning from POS
        viewModel.loadTables();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
