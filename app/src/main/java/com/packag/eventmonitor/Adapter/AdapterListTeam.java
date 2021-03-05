package com.packag.eventmonitor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.R;
import com.packag.eventmonitor.ShowingScoreActivity;

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
        TextView btn_ass_Show = v.findViewById(R.id.btn_ass_Show);

        final int position = i;
        tv_amtl_no_urut.setText(Integer.toString(dataTeam.get(i).getNo_urut()));
        tv_amtl_team_name.setText(dataTeam.get(i).getTeam_name());
        if(dataTeam.get(i).getPenilaian()!=null) {
            btn_ass_Show.setVisibility(View.VISIBLE);
            tv_amtl_nk.setText(ctx.getString(R.string.total_penilaian)+" : "+ String.format("%.2f",dataTeam.get(i).getPenilaian().getTotal_nilai()));
            tv_amtl_p.setText(ctx.getString(R.string.total_pengurangan)+" : "+ String.format("%.2f",dataTeam.get(i).getPenilaian().getTotal_potongan()));
            tv_amtl_nb.setText(ctx.getString(R.string.grand_total)+" : "+String.format("%.2f",dataTeam.get(i).getPenilaian().getGrand_total()));
        }else{
            btn_ass_Show.setVisibility(View.GONE);

            tv_amtl_nk.setText(ctx.getString(R.string.total_penilaian)+" : Belum di isi");
            tv_amtl_p.setText(ctx.getString(R.string.total_pengurangan)+" : Belum di isi");
            tv_amtl_nb.setText(ctx.getString(R.string.grand_total)+" : Belum di isi");
        }
        btn_ass_Show. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ShowingScoreActivity.class);
                intent.putExtra("score",String.format("%.2f",dataTeam.get(position).getPenilaian().getGrand_total()));

                ctx.startActivity(intent);

            }
        });
        return v;
    }
}
