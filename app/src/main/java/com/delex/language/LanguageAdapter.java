package com.delex.language;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.delex.app.SplashScreen;
import com.delex.driver.R;
import com.delex.pojo.Languages;
import com.delex.utility.SessionManager;

import java.util.ArrayList;

/**
 * Created by DELL on 22-12-2017.
 */

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> implements View.OnClickListener {

    private Activity context;
    private ArrayList<Languages> languages;
    private ArrayList<RadioButton> radioButtons=new ArrayList<>();
    private SessionManager sessionManager;

    public LanguageAdapter(Activity context, ArrayList<Languages> languages) {
        this.context = context;
        this.languages = languages;
        radioButtons.clear();
        sessionManager=SessionManager.getSessionManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_language,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.language.setText(languages.get(position).getName());
        if(languages.get(position).isSelected())
            holder.radioButton.setChecked(true);

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    clearAll();
                    holder.radioButton.setChecked(true);
                }
            }
        });
        radioButtons.add(holder.radioButton);
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tvDone){
            for(int i=0;i<radioButtons.size();i++){
                if(radioButtons.get(i).isChecked()){
                    sessionManager.setLang(languages.get(i).getCode());
                    sessionManager.setLangName(languages.get(i).getName());
                    //VariableConstant.LANGUAGE=languages.get(i).getCode();
                    //VariableConstant.langg=languages.get(i).getName();
                }
            }
            Intent intent=new Intent(context, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private RadioButton radioButton;
        private TextView language;

        public MyViewHolder(View itemView) {
            super(itemView);

            radioButton= (RadioButton) itemView.findViewById(R.id.rbSelLang);
            language= (TextView) itemView.findViewById(R.id.tvLanguage);
        }

    }

    public void clearAll(){

        for(int i=0;i<radioButtons.size();i++)
            radioButtons.get(i).setChecked(false);
    }
}
