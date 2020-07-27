package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

import ir.mahdi2001h.bluetoothIOT.R;


public class RecycleViewLcd extends RecyclerView.Adapter {

    private ArrayList<String> people;
    private Context context;

    public RecycleViewLcd(Context context, ArrayList<String> people) {
        this.context = context;
        this.people = people;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.row_lcd, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }


    private class ListViewHolder extends RecyclerView.ViewHolder implements OnClickListener,
            Serializable {
        TextView tx_log;


        public ListViewHolder(View itemView) {
            super(itemView);
            tx_log =itemView.findViewById(R.id.tx_log);

        }


        public void bindView(int position) {
            String p = people.get(position);
            tx_log.setText(p.toString());


        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return people.size();
    }


}
