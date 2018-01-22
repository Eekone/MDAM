package com.texnoprom.mdam.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.models.Register;

import java.util.List;

public class RegisterAdapter extends ArrayAdapter<Register> {

    private Resources res;
    private List<Register> Registers;

    public RegisterAdapter(List<Register> data, Context context) {
        super(context, R.layout.list_row, data);
        this.Registers = data;
        this.res = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Register Register = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row, parent, false);
            viewHolder.regTitle = (TextView) convertView.findViewById(R.id.regTitle);
            viewHolder.regValue = (TextView) convertView.findViewById(R.id.regValue);
            viewHolder.regUnit = (TextView) convertView.findViewById(R.id.regUnit);
            viewHolder.regNumber = (TextView) convertView.findViewById(R.id.regNumber);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.regNumber.setText(res.getString(R.string.register_number_ph, Register.getNumber() + 1));
        viewHolder.regTitle.setText(Register.getName());
        viewHolder.regValue.setText(Float.toString(Register.getValue()));
        // viewHolder.regUnit.setText(RegisterInfo.Unit(Register));

        return convertView;
    }

    private static class ViewHolder {
        TextView regTitle;
        TextView regValue;
        TextView regUnit;
        TextView regNumber;
    }
}

