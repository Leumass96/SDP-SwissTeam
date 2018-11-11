package ch.epfl.swissteam.services;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An adapter for capabilities
 */
public class CapabilitiesAdapter extends RecyclerView.Adapter<CapabilitiesAdapter.CapabilitiesViewHolder> {

    private List<Categories> capabilities_;
    private Map<String, List<String>> keyWords_;

    /**
     * Create an adapter for capabilities
     *
     * @param capabilities a list of capabilities
     */
    public CapabilitiesAdapter(List<Categories> capabilities, Map<String, List<String>> keyWords) {
        if(keyWords==null){
            keyWords_ = new HashMap<>();
        }else{
            keyWords_ = keyWords;
        }
        if(capabilities == null) {
            capabilities_ = new ArrayList<>();
        }else{
            capabilities_ = capabilities;
        }

    }

    @Override
    public CapabilitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.capabilities_display_adapter, parent, false);

        CapabilitiesViewHolder vh = new CapabilitiesViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CapabilitiesViewHolder mViewHolder, int position) {
        mViewHolder.mCapabilityName_.setText(capabilities_.get(position).toString());
        StringBuilder builder = new StringBuilder();
        for (String kw : keyWords_.get(capabilities_.get(position).toString())){
            builder.append(kw).append("; ");
        }
        //builder.delete(builder.length() - 2, builder.length());
        mViewHolder.mKeywordsEditText_.setText(builder.toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return capabilities_ == null ? 0 : capabilities_.size();
    }

    /**
     * ViewHolder for Capabilities
     */
    public static class CapabilitiesViewHolder extends RecyclerView.ViewHolder {

        public TextView mCapabilityName_;
        public TextView mKeywordsEditText_;

        /**
         * Create a CapabilitiesViewHolder
         *
         * @param v the current View
         */
        public CapabilitiesViewHolder(View v) {
            super(v);
            mCapabilityName_ = (TextView) v.findViewById(R.id.textview_capabilitiesadapter_capabilityname);
            mKeywordsEditText_ = (TextView) v.findViewById(R.id.textview_capabilitiesadapter_keywords);

        }
    }

}
