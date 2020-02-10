package com.example.inclass09;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    ArrayList<InboxBody> mData;

    public InboxAdapter(ArrayList<InboxBody> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InboxBody ib = mData.get(position);
        holder.tv_subject.setText(ib.subject);
        holder.tv_date.setText(ib.date);

        holder.ib = ib;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_subject, tv_date;
        InboxBody ib;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            final Context context = itemView.getContext();
            tv_subject = itemView.findViewById(R.id.tv_subject);
            tv_date = itemView.findViewById(R.id.tv_date);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("Demo", "clicked");

                    Intent i = new Intent(context, DisplayMail.class);
                    i.putExtra("subject", tv_subject.getText().toString());
                    i.putExtra("date", tv_date.getText().toString());

                    context.startActivity(i);
                }
            });
        }
    }
}
