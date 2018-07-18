package com.delex.vehiclelist;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.delex.driver.R;
import com.delex.pojo.SigninDriverVehicle;
import com.delex.utility.SessionManager;
import com.delex.utility.VariableConstant;

import java.util.ArrayList;

/**************************************************************************************************/

/**
 * Created by vibin baby on 10/4/17.
 */

public class VechicleListRVA extends RecyclerView.Adapter {


    ArrayList<View> views;
    ArrayList<RadioButton> buttons;
    ArrayList<SigninDriverVehicle> vDataList;
    private Context context;
    private View view;

    /**********************************************************************************************/
    public VechicleListRVA(Context context) {
        this.context = context;
        vDataList = ((VehicleList) context).vDataList;

    }

    /**********************************************************************************************/
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_select_vechicle, parent, false);
        return new VehicleViewHolder(view);
    }

    /**********************************************************************************************/
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VehicleViewHolder) {

            VehicleViewHolder vehicle = (VehicleViewHolder) holder;
            vehicle.tv_plateno_value.setText(vDataList.get(position).getPlatNo());
            vehicle.tv_type_value.setText(vDataList.get(position).getVehicleType());
            vehicle.tv_model_value.setText(vDataList.get(position).getVehicleModel());
            /*buttons.add(holder.rb_selectveh);
            views.add(holder.view_green);*/
            final int index = holder.getAdapterPosition();

            if (vDataList.get(position).isSelected()) {
                vehicle.rb_selectveh.setChecked(true);
                vehicle.view_green.setBackgroundResource(R.drawable.single_sel_veh_leftgreen);

            } else {
                vehicle.rb_selectveh.setChecked(false);
                vehicle.view_green.setBackgroundResource(R.drawable.single_sel_veh_leftgray);
            }
            /*holder.rb_selectveh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        resetAll();
                        holder.rb_selectveh.setChecked(true);
                        holder.view_green.setBackgroundResource(R.drawable.single_sel_veh_leftgreen);
                        VariableConstant.VECHICLESELECTED=true;
                        VariableConstant.VECHICLEID=index;
                        Toast.makeText(context, ""+index, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        holder.rb_selectveh.setChecked(false);
                        holder.view_green.setBackgroundResource(R.drawable.single_sel_veh_leftgray);

                    }
                }
            });*/

          /*  vehicle.rb_selectveh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onVehicleSelect(position);
                }
            });*/

            vehicle.ll_vechicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onVehicleSelect(position);
                }
            });
            vehicle.rb_selectveh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVehicleSelect(position);
                }
            });
        }

    }
    public void onVehicleSelect(int index){
        resetAll();
        vDataList.get(index).setSelected(true);
        SessionManager.getSessionManager(context).setTypeId(vDataList.get(index).getTypeId());
        VechicleListRVA.this.notifyDataSetChanged();
        VariableConstant.VECHICLESELECTED = true;
        VariableConstant.VECHICLEID = vDataList.get(index).getId();
        VariableConstant.VECHICLE_TYPE_ID = vDataList.get(index).getTypeId();
    }
    /**
     * reset the radio button color and the left view color,
     * if the vechicle id is not selected then it will be gray color
     */
    private void resetAll() {
        for (int i = 0; i < vDataList.size(); i++) {
            vDataList.get(i).setSelected(false);

        }
    }

    /* *********************************************************************************************/

    /**********************************************************************************************/
    @Override
    public int getItemCount() {
        return vDataList.size();
    }

    /**********************************************************************************************/
    public class VehicleViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_vechicle;
        private TextView tv_plateno_vl, tv_type_vl, tv_model_vl;
        private TextView tv_plateno_value, tv_type_value, tv_model_value;
        private RadioButton rb_selectveh;
        private View view_green;

        public VehicleViewHolder(View itemView) {
            super(itemView);

            Typeface ClanaproNarrMedium = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrMedium.otf");
            Typeface ClanaproNarrNews = Typeface.createFromAsset(context.getAssets(), "fonts/ClanPro-NarrNews.otf");

            ll_vechicle = (LinearLayout) itemView.findViewById(R.id.ll_vechicle);

            tv_plateno_vl = (TextView) itemView.findViewById(R.id.tv_plateno_vl);
            tv_plateno_vl.setTypeface(ClanaproNarrNews);

            tv_type_vl = (TextView) itemView.findViewById(R.id.tv_type_vl);
            tv_type_vl.setTypeface(ClanaproNarrNews);

            tv_model_vl = (TextView) itemView.findViewById(R.id.tv_model_vl);
            tv_model_vl.setTypeface(ClanaproNarrNews);

            tv_plateno_value = (TextView) itemView.findViewById(R.id.tv_plateno_value);
            tv_plateno_value.setTypeface(ClanaproNarrMedium);

            tv_type_value = (TextView) itemView.findViewById(R.id.tv_type_value);
            tv_type_value.setTypeface(ClanaproNarrMedium);

            tv_model_value = (TextView) itemView.findViewById(R.id.tv_model_value);
            tv_model_value.setTypeface(ClanaproNarrMedium);

            view_green = itemView.findViewById(R.id.view_green);

            rb_selectveh = (RadioButton) itemView.findViewById(R.id.rb_selectveh);

        }

    }
}
