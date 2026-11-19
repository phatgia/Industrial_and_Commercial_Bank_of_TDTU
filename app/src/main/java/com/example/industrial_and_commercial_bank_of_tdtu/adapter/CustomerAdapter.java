package com.example.industrial_and_commercial_bank_of_tdtu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.industrial_and_commercial_bank_of_tdtu.R;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CustomerSummary;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hien thi danh sach khach hang trong CustomerManager (Bank Officer).
 */
public class CustomerAdapter extends ArrayAdapter<CustomerSummary> {

    private final NumberFormat vndFormat;

    public CustomerAdapter(@NonNull Context context, @NonNull List<CustomerSummary> items) {
        super(context, 0, items);
        this.vndFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_customer, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CustomerSummary item = getItem(position);
        if (item != null) {
            String name = item.getFullName() != null ? item.getFullName() : "Khách hàng";
            holder.txtInitials.setText(extractInitials(name));
            holder.txtName.setText(name);

            String meta = item.getPhone() + " • Số dư: " + vndFormat.format(Math.round(item.getCheckingBalance())) + " đ";
            holder.txtMeta.setText(meta);

            holder.txtEkyc.setText(item.isEkycVerified() ? "Đã eKYC" : "Chưa eKYC");
            holder.txtEkyc.setTextColor(Color.parseColor(item.isEkycVerified() ? "#059669" : "#D97706"));
        }

        return convertView;
    }

    private String extractInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) return "KH";
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        char first = parts[parts.length - 2].charAt(0);
        char last = parts[parts.length - 1].charAt(0);
        return ("" + first + last).toUpperCase(Locale.ROOT);
    }

    private static class ViewHolder {
        final TextView txtInitials;
        final TextView txtName;
        final TextView txtMeta;
        final TextView txtEkyc;

        ViewHolder(View view) {
            txtInitials = view.findViewById(R.id.txtCustomerInitials);
            txtName = view.findViewById(R.id.txtCustomerName);
            txtMeta = view.findViewById(R.id.txtCustomerMeta);
            txtEkyc = view.findViewById(R.id.txtCustomerEkyc);
        }
    }
}
