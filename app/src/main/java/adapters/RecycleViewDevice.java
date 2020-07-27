package adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import ir.mahdi2001h.bluetoothIOT.R;


public class RecycleViewDevice extends RecyclerView.Adapter {

    private List<BluetoothDevice> people;
    private Context context;
    private PersonListClickListener listener;

    public RecycleViewDevice(Context context, List<BluetoothDevice> people, PersonListClickListener listener) {
        this.context = context;
        this.people = people;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.row_device, parent, false);
        return new ListViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements OnClickListener, Serializable {
        TextView tx_device_name, tx_device_address;
        private PersonListClickListener listener;

        public ListViewHolder(View itemView, PersonListClickListener listener) {
            super(itemView);
            tx_device_name = itemView.findViewById(R.id.tx_1);
            tx_device_address = itemView.findViewById(R.id.tx_device_address);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }


        public void bindView(int position) {
            BluetoothDevice p = people.get(position);
            tx_device_name.setText(p.getName());
            tx_device_address.setText(p.getAddress());


        }

        @Override
        public void onClick(View v) {
            BluetoothDevice p = people.get(getAdapterPosition());
            this.listener.onClick(v, p);
        }

    }

    @Override
    public int getItemCount() {
        return people.size();
    }


}
