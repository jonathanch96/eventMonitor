package com.packag.eventmonitor.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.R;

import java.util.Vector;

public class AdapterListTeam extends BaseAdapter {
    private Context ctx;
    Vector<Team> dataTeam;

    public AdapterListTeam(Context ctx, Vector<Team> dataTeam) {
        this.ctx = ctx;
        this.dataTeam = dataTeam;
    }

    @Override
    public int getCount() {
        return dataTeam.size();
    }

    @Override
    public Object getItem(int i) {
        return dataTeam.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(ctx, R.layout.adapter_model_team_list,null);
        TextView tv_amtl_no_urut = v.findViewById(R.id.tv_amtl_no_urut);
        TextView tv_amtl_team_name = v.findViewById(R.id.tv_amtl_team_name);
        TextView tv_amtl_nk = v.findViewById(R.id.tv_amtl_nk);
        TextView tv_amtl_p = v.findViewById(R.id.tv_amtl_p);
        TextView tv_amtl_nb = v.findViewById(R.id.tv_amtl_nb);


        tv_amtl_no_urut.setText(Integer.toString(dataTeam.get(i).getNo_urut()));
        tv_amtl_team_name.setText(dataTeam.get(i).getTeam_name());
        if(dataTeam.get(i).getPenilaian()!=null) {
            tv_amtl_nb.setText("Nilai Bersih : "+Double.toString(dataTeam.get(i).getPenilaian().getTb()));
            tv_amtl_nk.setText("Nilai Kotor : "+Double.toString(dataTeam.get(i).getPenilaian().getTk()));
            tv_amtl_p.setText("Pengurangan : "+Double.toString(dataTeam.get(i).getPenilaian().getP()));
        }else{
            tv_amtl_nb.setText("Nilai Bersih : Belum di isi");
            tv_amtl_nk.setText("Nilai Kotor : Belum di isi");
            tv_amtl_p.setText("Pengurangan : Belum di isi");
        }
        return v;
    }
}
