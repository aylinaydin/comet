package com.aylin.comet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Aylin on 20.03.2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private static final int ITEM_TYPE_SENT = 0;
    private static final int ITEM_TYPE_RECEIVED = 1;
    private int receiver = 0;

    private List<UserMessage> mMessagesList;
    private Context mContext;
    private String mSenderName;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView username;
        public TextView messageTime;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            messageTextView = v.findViewById(R.id.text_message_body);
            username = v.findViewById(R.id.text_message_name);
            messageTime = v.findViewById(R.id.text_message_time);
        }
    }

    public void add(int position, UserMessage message) {
        mMessagesList.add(position, message);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mMessagesList.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessagesAdapter(List<UserMessage> myDataset, Context context) {
        mMessagesList = myDataset;
        mContext = context;

    }

    @Override
    public int getItemViewType(int position) {
        String user_id;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            user_id = null;
        }
        // if (mMessagesList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
        if (mMessagesList.get(position).getSenderId().equals(user_id)) {
            return ITEM_TYPE_SENT;
        } else {
            receiver = 1;
            return ITEM_TYPE_RECEIVED;
        }
    }

    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == ITEM_TYPE_SENT) {
            v = LayoutInflater.from(mContext).inflate(R.layout.sent_messages, null);
        } else if (viewType == ITEM_TYPE_RECEIVED) {
            v = LayoutInflater.from(mContext).inflate(R.layout.received_messages, null);
        }
        return new ViewHolder(v); // view holder for header item
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        UserMessage msg = mMessagesList.get(position);


        holder.messageTextView.setText(msg.getMessage());

        if (receiver == 1) {//if user is receiver, set username of the messages

            DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            mUsersRef.child(msg.getSenderId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User sender = dataSnapshot.getValue(User.class);

                    assert sender != null;
                    mSenderName = sender.getName();

                    try {
                        holder.username.setText(mSenderName);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        holder.messageTime.setText(msg.getMessageTime());
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}
