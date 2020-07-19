package com.aylin.comet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by Aylin on 27.03.2018.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List<Group> mGroupsList;
    private Context mContext;
    private DatabaseReference denemeGroupId;
    private String input;

    public GroupAdapter(List<Group> myDataset, Context context) {
        mGroupsList = myDataset;
        mContext = context;

    }

    public void add(int position, Group group) {
        mGroupsList.add(position, group);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mGroupsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.group_single_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        /*ViewHolder vh = new ViewHolder(v);
        return vh;*/
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int position) {
        final Group group = mGroupsList.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        assert email != null;
        if (email.equals("aylin.aydin1@std.yeditepe.edu.tr")) {
            input = group.getGroupName() + "\n Key:" + group.getGroupKey();
        } else {
            input = group.getGroupName();
        }
        holder.groupNameTxtV.setText(input);
        // holder.groupNameTxtV.setText(group.getGroupName());
        //listen to single view layout click
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send this user id to chat messages activity
                goToUpdateActivity(group.getGroupId(), group.getGroupName());
            }
        });
    }

    private void goToUpdateActivity(String groupId, String groupName) {
        Intent goToUpdate = new Intent(mContext, ChatActivity.class);
        goToUpdate.putExtra("GROUP_ID", groupId);
        goToUpdate.putExtra("GROUP_NAME", groupName);
        mContext.startActivity(goToUpdate);
    }

    @Override
    public int getItemCount() {
        return mGroupsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupNameTxtV;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            groupNameTxtV = itemView.findViewById(R.id.group_name);
        }
    }
}
