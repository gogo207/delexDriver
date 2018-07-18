package com.delex.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delex.driver.R;
import com.delex.pojo.SignupZonedata;
import com.delex.pojo.VehMakeData;
import com.delex.pojo.VehTypeData;
import com.delex.pojo.VehTypeSepecialities;
import com.delex.pojo.VehicleMakeModel;
import com.delex.signup.GenericListActivity;

import java.util.ArrayList;

public class GenericListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private final Typeface ClanaproNarrMedium,ClanaproNarrNews;
    private ArrayList data;
    private Activity context;
    private String type;

    private ArrayList<SignupZonedata> signupZonedata;
    private ArrayList<VehTypeData> vehTypeDatas;
    private ArrayList<VehTypeSepecialities> vehTypeSepecialities;
    private ArrayList<VehMakeData> vehMakeDatas;
    private ArrayList<VehicleMakeModel> vehicleMakeModels;


    public GenericListAdapter(Activity context, ArrayList data, String type) {
        this.context = context;
        this.data = data;
        this.type = type;

        ClanaproNarrMedium = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrMedium.otf");
        ClanaproNarrNews = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrNews.otf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewsd = LayoutInflater.from(context).inflate(R.layout.single_select_operator, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(viewsd);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            final MyViewHolder viewHolder = (MyViewHolder) holder;

            switch (type) {
                case "ZONE":
                    signupZonedata = data;
                    viewHolder.operator.setText(signupZonedata.get(position).getName());
                    if (signupZonedata.get(position).isSelected()) {
                        viewHolder.operator.setChecked(true);
                    } else {
                        viewHolder.operator.setChecked(false);
                    }
                    break;

                case "VEHICLE_TYPE":

                    vehTypeDatas = data;
                    viewHolder.operator.setText(vehTypeDatas.get(position).getName());
                    if (vehTypeDatas.get(position).isSelected()) {
                        viewHolder.operator.setChecked(true);
                    } else {
                        viewHolder.operator.setChecked(false);
                    }
                    break;

                case "VEHICLE_SPECIALITIES":

                    vehTypeSepecialities = data;
                    viewHolder.operator.setText(vehTypeSepecialities.get(position).getName());
                    if (vehTypeSepecialities.get(position).isSelected()) {
                        viewHolder.operator.setChecked(true);
                    } else {
                        viewHolder.operator.setChecked(false);
                    }
                    break;

                case "VEHICLE_MAKE":

                    vehMakeDatas = data;
                    viewHolder.operator.setText(vehMakeDatas.get(position).getName());
                    if (vehMakeDatas.get(position).isSelected()) {
                        viewHolder.operator.setChecked(true);
                    } else {
                        viewHolder.operator.setChecked(false);
                    }
                    break;

                case "VEHICLE_MODEL":

                    vehicleMakeModels = data;
                    viewHolder.operator.setText(vehicleMakeModels.get(position).getName());
                    if (vehicleMakeModels.get(position).isSelected()) {
                        viewHolder.operator.setChecked(true);
                    } else {
                        viewHolder.operator.setChecked(false);
                    }
                    break;
            }

            viewHolder.operator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setView(viewHolder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {
        switch (type) {
            case "ZONE":
                ((GenericListActivity) context).sendResult(signupZonedata, type);

                break;

            case "VEHICLE_TYPE":
                ((GenericListActivity) context).sendResult(vehTypeDatas, type);

                break;

            case "VEHICLE_SPECIALITIES":
                ((GenericListActivity) context).sendResult(vehTypeSepecialities, type);

                break;

            case "VEHICLE_MAKE":
                ((GenericListActivity) context).sendResult(vehMakeDatas, type);

                break;

            case "VEHICLE_MODEL":
                ((GenericListActivity) context).sendResult(vehicleMakeModels, type);
                break;
        }

    }

    public void setView(int position) {
        switch (type) {
            case "ZONE":
                if (signupZonedata.get(position).isSelected()) {
                    signupZonedata.get(position).setSelected(false);

                } else {
                    signupZonedata.get(position).setSelected(true);
                }
                break;

            case "VEHICLE_TYPE":
                Log.d("mura", "setView: " + vehTypeDatas.get(position).isSelected());
                if (vehTypeDatas.get(position).isSelected()) {
                    vehTypeDatas.get(position).setSelected(false);
                } else {

                    vehTypeDatas.get(position).setSelected(true);
                    removeOtherSelectedViews(vehTypeDatas, position);
                }

                break;

            case "VEHICLE_SPECIALITIES":
                if (vehTypeSepecialities.get(position).isSelected()) {
                    vehTypeSepecialities.get(position).setSelected(false);
                } else {
                    vehTypeSepecialities.get(position).setSelected(true);
                }
                break;

            case "VEHICLE_MAKE":
                if (vehMakeDatas.get(position).isSelected()) {
                    vehMakeDatas.get(position).setSelected(false);
                } else {
                    vehMakeDatas.get(position).setSelected(true);
                    removeOtherSelectedViews(vehMakeDatas, position);
                }

                break;

            case "VEHICLE_MODEL":
                if (vehicleMakeModels.get(position).isSelected()) {
                    vehicleMakeModels.get(position).setSelected(false);
                } else {
                    vehicleMakeModels.get(position).setSelected(true);
                    removeOtherSelectedViews(vehicleMakeModels, position);
                }
                break;
        }
    }

    public void removeOtherSelectedViews(ArrayList list, int selected) {
        for (int i = 0; i < list.size(); i++) {
            if (i != selected) {
                Object o = list.get(i);

                if (o instanceof VehTypeData) {
                    ((VehTypeData) o).setSelected(false);
                }
                if (o instanceof VehMakeData) {
                    ((VehMakeData) o).setSelected(false);
                }
                if (o instanceof VehicleMakeModel) {
                    ((VehicleMakeModel) o).setSelected(false);
                }
            }
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatCheckBox operator;

        public MyViewHolder(View itemView) {
            super(itemView);
            operator = (AppCompatCheckBox) itemView.findViewById(R.id.rb_single_operator);
            operator.setTypeface(ClanaproNarrNews);
        }
    }
}
