package com.texnoprom.mdam.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.models.ExpandingRow;

import java.util.ArrayList;


public class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.ExpandingViewHolder> {
    private ArrayList<ExpandingRow> expandingRows;
    private Context mContext;

    public RecyclerDataAdapter(ArrayList<ExpandingRow> expandingRows, Context mContext) {
        this.expandingRows = expandingRows;
        this.mContext = mContext;
    }

    @Override
    public ExpandingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_row, parent, false);
        return new ExpandingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpandingViewHolder holder, int position) {
        ExpandingRow expandingRow = expandingRows.get(position);
        holder.textView_parentName.setText(expandingRow.getParentName());
        //
        int noOfChildTextViews = holder.linearLayout_childItems.getChildCount();
        int noOfChild = expandingRow.getChildDataItems().size();
        if (noOfChild < noOfChildTextViews) {
            for (int index = noOfChild; index < noOfChildTextViews; index++) {
                TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(index);
                currentTextView.setVisibility(View.GONE);
            }
        }
        for (int textViewIndex = 0; textViewIndex < noOfChild; textViewIndex++) {
            TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(textViewIndex);
            currentTextView.setText(expandingRow.getChildDataItems().get(textViewIndex));
            currentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "" + ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return expandingRows.size();
    }

    class ExpandingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private TextView textView_parentName;
        private LinearLayout linearLayout_childItems;

        ExpandingViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            textView_parentName = (TextView) itemView.findViewById(R.id.tv_parentName);
            linearLayout_childItems = (LinearLayout) itemView.findViewById(R.id.ll_child_items);
            linearLayout_childItems.setVisibility(View.GONE);
            int intMaxNoOfChild = 0;
            for (int index = 0; index < expandingRows.size(); index++) {
                int intMaxSizeTemp = expandingRows.get(index).getChildDataItems().size();
                if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp;
            }
            for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {
                TextView textView = new TextView(context);
                textView.setId(indexView);
                textView.setPadding(45, 20, 45, 20);
                // textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                textView.setGravity(Gravity.START);
                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_sub_module_text));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setOnClickListener(this);
                linearLayout_childItems.addView(textView, layoutParams);
            }
            textView_parentName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tv_parentName) {
                if (linearLayout_childItems.getVisibility() == View.VISIBLE) {
                    linearLayout_childItems.setVisibility(View.GONE);
                } else {
                    linearLayout_childItems.setVisibility(View.VISIBLE);
                }
            } else {
                TextView textViewClicked = (TextView) view;
                Toast.makeText(context, "" + textViewClicked.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
