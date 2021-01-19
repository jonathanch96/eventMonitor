package com.packag.eventmonitor.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.packag.eventmonitor.Data.Chat;
import com.packag.eventmonitor.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterChatRoom extends BaseAdapter {
    List<Chat> chat = new ArrayList<Chat>();
    Context ctx;
    public AdapterChatRoom(Context ctx){
        this.ctx = ctx;

    }
    public void add(Chat message) {
        this.chat.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }
    public void updateData(ArrayList<Chat> chat) {
        this.chat.clear();
        this.chat.addAll(chat);
        notifyDataSetChanged();
    }
    public List<Chat> getChat(){
        return this.chat;
    }
    @Override
    public int getCount() {
        return chat.size();
    }

    @Override
    public Object getItem(int position) {
        return chat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatRoomViewHolder holder = new ChatRoomViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Chat message = chat.get(position);


        convertView = messageInflater.inflate(R.layout.chat_room_detail, null);
        holder.avatar = (View) convertView.findViewById(R.id.avatar_rd);
        holder.name = (TextView) convertView.findViewById(R.id.name_rd);
        holder.messageBody = (TextView) convertView.findViewById(R.id.message_body_rd);
        holder.cart_badge = (TextView) convertView.findViewById(R.id.cart_badge_rd);
        convertView.setTag(holder);

        holder.name.setText(message.getName());
        holder.messageBody.setText(message.getText());
        if(message.getUnread_counter()==0){
            holder.cart_badge.setVisibility(View.GONE);
            holder.cart_badge.setText(message.getUnread_counter()+"");
        }else{
            holder.cart_badge.setVisibility(View.VISIBLE);
            holder.cart_badge.setText(message.getUnread_counter()+"");
        }


        return convertView;
    }
}
class ChatRoomViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
    public TextView cart_badge;
}

