package com.packag.eventmonitor.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.packag.eventmonitor.Data.Chat;
import com.packag.eventmonitor.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterChat extends BaseAdapter {
    List<Chat> chat = new ArrayList<Chat>();
    Context ctx;
    public AdapterChat(Context ctx){
        this.ctx = ctx;

    }
    public void add(Chat message) {
        this.chat.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
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
        ChatViewHolder holder = new ChatViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Chat message = chat.get(position);

        if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.chat_my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.chat_their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getName());
            holder.messageBody.setText(message.getText());
//            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
//            drawable.setColor(Color.parseColor(message.getMemberData().getColor()));
        }

        return convertView;
    }
}

class ChatViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}
