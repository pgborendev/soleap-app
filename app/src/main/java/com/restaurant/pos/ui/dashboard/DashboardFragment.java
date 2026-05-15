package com.restaurant.pos.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.restaurant.pos.R;
import com.restaurant.pos.adapters.OrderSummaryAdapter;
import com.restaurant.pos.api.ERPNextClient;
import com.restaurant.pos.databinding.FragmentDashboardBinding;
import com.restaurant.pos.models.ERPModels;
import com.restaurant.pos.ui.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private OrderSummaryAdapter ordersAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Setup RecyclerView for recent orders
        ordersAdapter = new OrderSummaryAdapter(new ArrayList<>(), invoice ->
                navigateToOrders());
        binding.rvRecentOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentOrders.setAdapter(ordersAdapter);

        // ERPNext info
        String baseUrl = ERPNextClient.getPrefs().getString(ERPNextClient.KEY_BASE_URL, "Not set");
        binding.tvErpUrl.setText(baseUrl);

        // Quick action buttons
        binding.btnNewOrder.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_tables));
        binding.btnKitchen.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_kitchen));
        binding.btnOrders.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_orders));
        binding.btnSettings.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        // Swipe to refresh
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());

        // Observe data
        viewModel.getInvoices().observe(getViewLifecycleOwner(), result -> {
            binding.swipeRefresh.setRefreshing(false);
            if (result.isSuccess() && result.data != null) {
                updateStats(result.data);
                ordersAdapter.updateData(result.data.subList(0, Math.min(5, result.data.size())));
            }
        });

        viewModel.getTables().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess() && result.data != null) {
                long occupied = result.data.stream()
                        .filter(t -> "Occupied".equals(t.status)).count();
                binding.tvTablesOccupied.setText(occupied + "/" + result.data.size());
            }
        });

        viewModel.refresh();
    }

    private void updateStats(List<ERPModels.POSInvoice> invoices) {
        double revenue = 0;
        int active = 0;
        for (ERPModels.POSInvoice inv : invoices) {
            revenue += inv.grandTotal;
            if ("Draft".equals(inv.status) || "Submitted".equals(inv.status)) active++;
        }
        binding.tvRevenue.setText(String.format(Locale.getDefault(), "$%.2f", revenue));
        binding.tvActiveOrders.setText(String.valueOf(active));
        double avg = invoices.isEmpty() ? 0 : revenue / invoices.size();
        binding.tvAvgOrder.setText(String.format(Locale.getDefault(), "$%.1f", avg));
    }

    private void navigateToOrders() {
        Navigation.findNavController(requireView()).navigate(R.id.nav_orders);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
