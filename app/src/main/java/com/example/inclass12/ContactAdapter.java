package com.example.inclass12;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private ArrayList<Contact> mData;
    private OnItemListener mOnItemListener;

    ContactAdapter(ArrayList<Contact> mData, OnItemListener mOnItemListener) {
        this.mData = mData;
        this.mOnItemListener = mOnItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = mData.get(position);

        holder.tv_name.setText(contact.name);
        holder.tv_email.setText(contact.email);
        holder.tv_phone.setText(contact.phone);

        Picasso.get()
                .load(!contact.img_url.equals("")? contact.img_url: "http://")
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.iv_photo);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tv_name, tv_email, tv_phone;
        ImageView iv_photo;
        Contact contact;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull final View itemView, OnItemListener onItemListener) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onItemListener.onItemLongPress(getLayoutPosition());

            return true;
        }
    }
}
