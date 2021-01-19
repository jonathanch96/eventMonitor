package com.packag.eventmonitor.Data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Chat  implements Comparable<Chat>{




    private String name; // message body
    @Exclude private boolean belongsToCurrentUser; // is this message sent by us?
    private String text; // message body
    private String userId;
    private String destUserId;
    Timestamp created_at;

    @Exclude public int getUnread_counter() {
        return unread_counter;
    }

    @Exclude public void setUnread_counter(int unread_counter) {
        this.unread_counter = unread_counter;
    }

    @Exclude  int unread_counter = 0;

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    private boolean is_read;
    private String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDestUserId() {
        return destUserId;
    }

    public void setDestUserId(String destUserId) {
        this.destUserId = destUserId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Chat(String text, String name, String userId,String destUserId) {
        this.text = text;
        this.name = name;
        this.userId = userId;
        this.destUserId = destUserId;
        this.is_read=false;
        this.created_at = new Timestamp(new Date());
    }
    public Chat(){

    }

    public String getText() {
        return text;
    }

    @Exclude public void setBelongsToCurrentUser(boolean belongsToCurrentUser) {
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    @Exclude public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Chat o) {
        //return getCreated_at().compareTo(o.getCreated_at()); //asc
        return o.getCreated_at().compareTo(getCreated_at()); //desc
    }
}
