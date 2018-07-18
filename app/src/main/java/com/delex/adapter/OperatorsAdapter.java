package com.delex.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.delex.driver.R;
import com.delex.pojo.OperatorsData;
import com.delex.pojo.SignupZonedata;
import com.delex.pojo.VehMakeData;
import com.delex.pojo.VehTypeData;
import com.delex.pojo.VehTypeSepecialities;
import com.delex.pojo.VehicleMakeModel;
import com.delex.signup.SelectAnOperator;
import com.delex.utility.Utility;

import java.util.ArrayList;

/**
 * Created by ads on 10/05/17.
 */

public class OperatorsAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<OperatorsData> mDataList;
    private ArrayList<SignupZonedata> signupZonedata;
    private ArrayList<VehTypeData> vehTypeDatas;
    private ArrayList<VehTypeSepecialities> vehTypeSepecialities;
    private ArrayList<VehMakeData> vehMakeDatas;
    private ArrayList<VehicleMakeModel> vehicleMakeModels;
    private int opertaion;
    private String TAG = OperatorsAdapter.class.getSimpleName();

    /**********************************************************************************************/
    public OperatorsAdapter(Context context, int opertaion) {
        this.context = context;
        this.opertaion = opertaion;

        if (opertaion == 1) {
            mDataList = ((SelectAnOperator) context).operatorData;
        } else if (opertaion == 2) {
            vehTypeDatas = ((SelectAnOperator) context).vehTypeData;
        } else if (opertaion == 3) {
            vehTypeSepecialities = ((SelectAnOperator) context).vehTypeSepecialities;
        } else if (opertaion == 4) {
            vehMakeDatas = ((SelectAnOperator) context).vehMakeDatas;
        } else if (opertaion == 5) {
            vehicleMakeModels = ((SelectAnOperator) context).vehicleMakeModels;
        } else if (opertaion == 6) {
            signupZonedata = ((SelectAnOperator) context).signupZonedata;
        }
    }

    /**********************************************************************************************/
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new SingleOperator(LayoutInflater.from(context).inflate(R.layout.single_select_operator, parent, false));
    }

    /**********************************************************************************************/
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SingleOperator) {
            SingleOperator anOperator = (SingleOperator) holder;
            /**
             * operation 1 : have to show the Operator list
             * operation 2 : have to show the Vehicle Type list
             */
            if (opertaion == 1) {
                anOperator.operator.setText(mDataList.get(position).getCompany());
                if (mDataList.get(position).isSelected()) {
                    anOperator.operator.setChecked(true);
                } else {
                    anOperator.operator.setChecked(false);
                }
            } else if (opertaion == 2) {
                anOperator.operator.setText(vehTypeDatas.get(position).getName());
                if (vehTypeDatas.get(position).isSelected()) {
                    anOperator.operator.setChecked(true);
                } else {
                    anOperator.operator.setChecked(false);
                }
            } else if (opertaion == 3) {
                anOperator.operator.setText(vehTypeSepecialities.get(position).getName());
                Utility.printLog("specialities " + vehTypeSepecialities.get(position).getName());
                if (vehTypeSepecialities.get(position).isSelected()) {
                    anOperator.operator.setChecked(true);
                } else {
                    anOperator.operator.setChecked(false);
                }
            } else if (opertaion == 4) {
                anOperator.operator.setText(vehMakeDatas.get(position).getName());
                if (vehMakeDatas.get(position).isSelected()) {
                    anOperator.operator.setChecked(true);
                } else {
                    anOperator.operator.setChecked(false);
                }
            } else if (opertaion == 5) {
                anOperator.operator.setText(vehicleMakeModels.get(position).getName());
                Utility.printLog("makemodels " + vehicleMakeModels.get(position).getName());
                if (vehicleMakeModels.get(position).isSelected()) {
                    anOperator.operator.setChecked(true);
                } else {
                    anOperator.operator.setChecked(false);
                }
            }
            if (opertaion == 6) {
                anOperator.operator.setText(signupZonedata.get(position).getName());
                if (signupZonedata.get(position).isSelected()) {
                    anOperator.operator.setChecked(true);
                } else {
                    anOperator.operator.setChecked(false);
                }
            }

            anOperator.operator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeUiOnClick(holder.getAdapterPosition());
                }
            });

        }
    }

    /**********************************************************************************************/
    private void changeUiOnClick(int position) {
        if (opertaion == 1) {
            for (int index = 0; index < mDataList.size(); index++) {
                if (index == position) {
                    mDataList.get(index).setSelected(true);
                    SelectAnOperator anOperator = (SelectAnOperator) context;
                    anOperator.selectedOperatorName = mDataList.get(index).getCompany();
                    anOperator.selectedOperatorId = mDataList.get(index).getId();
                    Log.d("TAG", "operator " + anOperator.selectedOperatorName + " " + anOperator.selectedOperatorId);
                } else {
                    mDataList.get(index).setSelected(false);
                }
            }
        } else if (opertaion == 2) {
            for (int index = 0; index < vehTypeDatas.size(); index++) {
                if (index == position) {
                    vehTypeDatas.get(index).setSelected(true);
                    SelectAnOperator anOperator = (SelectAnOperator) context;
                    anOperator.selectedOperatorName = vehTypeDatas.get(index).getName();
                    anOperator.selectedOperatorId = vehTypeDatas.get(index).getId();
                    anOperator.vehTypeData.get(position).setSelected(true);
                    anOperator.vehTypeSepecialities.addAll(vehTypeDatas.get(index).getSepecialities());

                    Log.d("TAG", "operator " + anOperator.selectedOperatorName + " " + anOperator.selectedOperatorId +
                            " " + anOperator.vehTypeSepecialities.toString());
                } else {
                    vehTypeDatas.get(index).setSelected(false);
                }
            }
        } else if (opertaion == 3) {
            //for (int index = 0; index < vehTypeSepecialities.size(); index++) {
                /*if (index == position) {
                    vehTypeSepecialities.get(index).setSelected(true);
                    SelectAnOperator anOperator = (SelectAnOperator) context;
                    anOperator.selectedOperatorName = vehTypeSepecialities.get(index).getName();
                    anOperator.selectedOperatorId = vehTypeSepecialities.get(index).getId();

                    Log.d("TAG", "operator " + anOperator.selectedOperatorName + " " + anOperator.selectedOperatorId);
                } else {
                    vehTypeSepecialities.get(index).setSelected(false);
                }*/
            if (vehTypeSepecialities.get(position).isSelected()) {
                vehTypeSepecialities.get(position).setSelected(false);
                SelectAnOperator anOperator = (SelectAnOperator) context;
                anOperator.vehTypeSepecialities.get(position).setSelected(false);
            } else {
                vehTypeSepecialities.get(position).setSelected(true);
                SelectAnOperator anOperator = (SelectAnOperator) context;
                anOperator.selectedOperatorName = vehTypeSepecialities.get(position).getName();
                anOperator.selectedOperatorId = vehTypeSepecialities.get(position).getId();
                anOperator.vehTypeSepecialities.get(position).setSelected(true);
            }
            //}
        } else if (opertaion == 4) {
            for (int index = 0; index < vehMakeDatas.size(); index++) {
                if (index == position) {
                    vehMakeDatas.get(index).setSelected(true);
                    SelectAnOperator anOperator = (SelectAnOperator) context;
                    anOperator.selectedOperatorName = vehMakeDatas.get(index).getName();
                    anOperator.selectedOperatorId = vehMakeDatas.get(index).getId();
                    anOperator.vehMakeDatas.get(position).setSelected(true);
                    anOperator.vehicleMakeModels.addAll(vehMakeDatas.get(index).getModels());

                    Log.d("TAG", "operator " + anOperator.selectedOperatorName + " " + anOperator.selectedOperatorId +
                            " " + anOperator.vehicleMakeModels.toString());
                } else {
                    vehMakeDatas.get(index).setSelected(false);
                }
            }
        } else if (opertaion == 5) {
            for (int index = 0; index < vehicleMakeModels.size(); index++) {
                if (index == position) {
                    vehicleMakeModels.get(index).setSelected(true);
                    SelectAnOperator anOperator = (SelectAnOperator) context;
                    anOperator.selectedOperatorName = vehicleMakeModels.get(index).getName();
                    anOperator.selectedOperatorId = vehicleMakeModels.get(index).getId();

                    Log.d("TAG", "operator " + anOperator.selectedOperatorName + " " + anOperator.selectedOperatorId);
                } else {
                    vehicleMakeModels.get(index).setSelected(false);
                }
            }
        } else if (opertaion == 6) {
//            for (int index = 0; index < signupZonedata.size(); index++) {
                /*if (index == position) {
                    signupZonedata.get(index).setSelected(true);
                    SelectAnOperator anOperator = (SelectAnOperator) context;
                    anOperator.selectedOperatorName = signupZonedata.get(index).getName();
                    anOperator.selectedOperatorId = signupZonedata.get(index).getId();
                    Log.d("TAG", "operator " + anOperator.selectedOperatorName + " " + anOperator.selectedOperatorId);
                } else {
                    signupZonedata.get(index).setSelected(false);
                }*/
            if (signupZonedata.get(position).isSelected()) {
                signupZonedata.get(position).setSelected(false);
            } else {
                signupZonedata.get(position).setSelected(true);
                SelectAnOperator anOperator = (SelectAnOperator) context;
                anOperator.selectedOperatorName = signupZonedata.get(position).getName();
                anOperator.selectedOperatorId = signupZonedata.get(position).getId();
            }
//            }
        }

        OperatorsAdapter.this.notifyDataSetChanged();
    }

    /**********************************************************************************************/
    @Override
    public int getItemCount() {
        int size = 0;

        if (opertaion == 1) {
            size = mDataList.size();
        } else if (opertaion == 2) {
            size = vehTypeDatas.size();
        } else if (opertaion == 3) {
            size = vehTypeSepecialities.size();
        } else if (opertaion == 4) {
            size = vehMakeDatas.size();
        } else if (opertaion == 5) {
            size = vehicleMakeModels.size();
        } else if (opertaion == 6) {
            size = signupZonedata.size();
        }
        return size;

    }

    /**********************************************************************************************/
    private class SingleOperator extends RecyclerView.ViewHolder {
        RadioButton operator;

        public SingleOperator(View itemView) {
            super(itemView);
            operator = (RadioButton) itemView.findViewById(R.id.rb_single_operator);
        }
    }
}
