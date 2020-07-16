package com.aylin.comet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by Aylin on 27.03.2018.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private DatabaseReference testReference;
    private List<User> mMemberList;
    private Context mContext;

    public MemberAdapter(List<User> myDataset, Context context) {
        mMemberList = myDataset;
        mContext = context;
    }

    public void add(int position, User user) {
        mMemberList.add(position, user);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mMemberList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                mContext);
        View v =
                inflater.inflate(R.layout.single_row_member_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        /*ViewHolder vh = new ViewHolder(v);
        return vh;*/
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MemberAdapter.ViewHolder holder, int position) {
        final User user = mMemberList.get(position);
        //testReference = FirebaseDatabase.getInstance().getReference().child("testReference").push();
        // testReference.setValue(user.getName());
        holder.memberNameTextView.setText(user.getName());

    }

    @Override
    public int getItemCount() {
        return mMemberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberNameTextView;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            memberNameTextView = itemView.findViewById(R.id.member_name);
        }
    }


}
