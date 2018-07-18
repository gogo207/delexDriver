package com.delex.country_picker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.driver.R.drawable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;


public class CountryListAdapter extends BaseAdapter {

    List<Country> countries;
    LayoutInflater inflater;
    private Context context;

    public CountryListAdapter(Context context, List<Country> countries) {
        super();
        this.context = context;
        this.countries = countries;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private int getResId(String drawableName) {

        try {
            Class<drawable> res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("CountryCodePicker", "Failure to get drawable id.", e);
        }
        return -1;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        Cell cell;
        Country country = countries.get(position);

        if (convertView == null) {
            cell = new Cell();
            cellView = inflater.inflate(R.layout.row, null);
            cell.textView = (TextView) cellView.findViewById(R.id.row_title);
            cell.imageView = (ImageView) cellView.findViewById(R.id.row_icon);
            cellView.setTag(cell);
        } else {
            cell = (Cell) cellView.getTag();
        }

        cell.textView.setText(country.getName());

        try {
            String drawableName = "flag_"
                    + country.getCode().toLowerCase(Locale.ENGLISH);
            cell.imageView.setBackgroundResource(getResId(drawableName));

        } catch (Exception e) {

        }

        return cellView;
    }

    static class Cell {
        public TextView textView;
        public ImageView imageView;
    }

}