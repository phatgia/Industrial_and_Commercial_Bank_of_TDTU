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
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hien thi danh sach giao dich cho ListView o HomeFragment va TransactionHistoryFragment.
 */
public class TransactionAdapter extends ArrayAdapter<TransactionItem> {

    private final NumberFormat vndFormat;

    public TransactionAdapter(@NonNull Context context, @NonNull List<TransactionItem> items) {
        super(context, 0, items);
        this.vndFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TransactionItem item = getItem(position);
        if (item != null) {
            String desc = (item.getDescription() != null && !item.getDescription().isEmpty())
                    ? item.getDescription()
                    : item.getTxType();
            holder.txtDesc.setText(desc);

            String bank = item.getToBankName() != null ? item.getToBankName() : "ICBT Bank";
            String date = item.getCreatedAt() != null ? item.getCreatedAt() : "";
            holder.txtMeta.setText(bank + (date.isEmpty() ? "" : " • " + date));

            boolean isIncoming = "DEPOSIT".equalsIgnoreCase(item.getTxType());
            String prefix = isIncoming ? "+" : "-";
            holder.txtAmount.setText(prefix + vndFormat.format(Math.round(item.getAmount())) + " đ");
            holder.txtAmount.setTextColor(Color.parseColor(isIncoming ? "#16A34A" : "#DC2626"));
        }

        return convertView;
    }

    private static class ViewHolder {
        final TextView txtDesc;
        final TextView txtMeta;
        final TextView txtAmount;

        ViewHolder(View view) {
            txtDesc = view.findViewById(R.id.txtTxDesc);
            txtMeta = view.findViewById(R.id.txtTxMeta);
            txtAmount = view.findViewById(R.id.txtTxAmount);
        }
    }
}
