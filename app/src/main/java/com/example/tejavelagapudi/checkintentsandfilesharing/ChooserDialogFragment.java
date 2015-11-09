package com.example.tejavelagapudi.checkintentsandfilesharing;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.databinding.layouts.DataBindingInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.tejavelagapudi.checkintentsandfilesharing.databinding.FragmentDialogChooserBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejavelagapudi on 11/9/15.
 */
public class ChooserDialogFragment extends DialogFragment {


    ArrayList<String> mActivities = new ArrayList<>();
    ArrayList<String> mChoosenActivities = new ArrayList<>();
    ActivitiesSelectedListener mActivitiesSelectedListener;

    interface ActivitiesSelectedListener{
        void onActivitiesSelected(ArrayList<String> selectedActivities);
    }

    public static ChooserDialogFragment newInstance(ArrayList<String> activities) {
        ChooserDialogFragment chooserDialogFragment = new ChooserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", activities);
        chooserDialogFragment.setArguments(bundle);
        return chooserDialogFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivities = getArguments().getStringArrayList("list");
        mActivitiesSelectedListener=(ActivitiesSelectedListener)getActivity();
        final FragmentDialogChooserBinding fragmentDialogChooserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_chooser, container, false);
        RecyclerView recyclerView = fragmentDialogChooserBinding.recyclerView;
        ChooserRecyclerViewAdapter chooserRecyclerViewAdapter = new ChooserRecyclerViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chooserRecyclerViewAdapter);
        fragmentDialogChooserBinding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChoosenActivities.size()==0){
                    mActivitiesSelectedListener.onActivitiesSelected(mActivities);
                }else{
                    mActivitiesSelectedListener.onActivitiesSelected(mChoosenActivities);
                }
                getDialog().dismiss();

            }
        });
        return fragmentDialogChooserBinding.getRoot();
    }

    public class ChooserRecyclerViewAdapter extends RecyclerView.Adapter<ChooserRecyclerViewAdapter.ViewHolder> {

        private  final String TAG = ChooserRecyclerViewAdapter.class.getSimpleName();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recylcer_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final int pos = position;
            holder.textView.setText(mActivities.get(position));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        mChoosenActivities.add(mActivities.get(pos));
                    }else{
                        mChoosenActivities.remove(mActivities.get(pos));
                    }
                }
            });
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    } else {
                        holder.checkBox.setChecked(true);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            CheckBox checkBox;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text_view);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            }
        }
        /*private boolean isChecked(CheckBox checkBox){
            return checkBox.isChecked();
        }*/

    }

}
