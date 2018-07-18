package com.delex.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delex.adapter.CurrentUpcomingJobRVA;
import com.delex.driver.R;
import com.delex.pojo.AssignedAppointments;
import com.delex.pojo.AssignedTripData;
import com.delex.utility.Utility;

import java.util.ArrayList;


public class HomeCurrentJobFrag extends Fragment {

    private static final String TAG = "HomeCurrentJobFrag";
    public ArrayList<AssignedAppointments> assignedAppointmentses;
    public CurrentUpcomingJobRVA currentJobRVA;
    RecyclerView recyclerView;
    private View rootView;

    public static HomeCurrentJobFrag getHomeCurrentJobFrag(AssignedTripData assignedTripData) {

        HomeCurrentJobFrag frag = new HomeCurrentJobFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", assignedTripData);
        frag.setArguments(bundle);
       /* HashMap hashMap=new HashMap();

        for(int i=0;i<assignedTripData.getAppointments().size();i++)
        {
            PubNubStrData data=new PubNubStrData();
            data.setBid(assignedTripData.getAppointments().get(i).getBid());
            data.setStatus(Integer.parseInt(assignedTripData.getAppointments().get(i).getStatusCode()));
            data.setCustomerChannel("");
            data.setDistance(0.0);
            data.setRoot("");

            hashMap.put(assignedTripData.getAppointments().get(i).getBid(),data);
            Utility.printLog("printing : bid "+assignedTripData.getAppointments().get(i).getBid()+" status"+assignedTripData.getAppointments().get(i).getStatusCode());
        }*/

        return frag;
    }

    /**********************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recyclerview, container, false);
        Utility.printLog(TAG + " on createview called");
        initializeViews();
        return rootView;
    }

    /**********************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            AssignedTripData assignedTripData = (AssignedTripData) bundle.getSerializable("data");
            assignedAppointmentses = new ArrayList<>();
            assignedAppointmentses.clear();
            assignedAppointmentses.addAll(assignedTripData.getAppointments());

            Utility.printLog("assignedAppointmentses " + assignedAppointmentses);
        }
    }

    /**********************************************************************************************/
                                    /*initializeViews*/
    /**********************************************************************************************/
    /**
     * <h1>initializeViews</h1>
     * <p>this is the method, for initialize the views</p>
     */
    private void initializeViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_job_home);
        currentJobRVA = new CurrentUpcomingJobRVA(getActivity(), "CURRENT", assignedAppointmentses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(currentJobRVA);
        currentJobRVA.notifyDataSetChanged();
    }


}
