package com.karigarjobs.provider;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.karigarjobs.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.karigarjobs.provider.HomeActivity.objSing;

public class ListUserReqActivity extends RecyclerView.Adapter<ListUserReqActivity.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    public static ArrayList<UserJobReqDataModel> dataSet;
    private boolean isLoadingAdded = false;
    Context context;

    ListUserReqActivity(Context context)
    {
        this.context=context;
        this.dataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Toolbar toolbar = (Toolbar) (((Activity)context).findViewById(R.id.toolbar));
        toolbar.setTitle(context.getResources().getString(R.string.title_jobappl_usrlist));

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_req_jobpost_view, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final UserJobReqDataModel data = dataSet.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                //String tm = data.getjobapldate();
                //String dt = tm.substring(0, 19).replace('T', ' ');
                String mainval = "<b>"+data.getUname()+"</b><br>"+context.getResources().getString(R.string.title_jobcategory)+":"+data.getJobcat()+"<br>"+context.getResources().getString(R.string.title_your_age)+":"+data.getAge()+"<br>"+context.getResources().getString(R.string.title_jpapplied_dt)+";"+convertFormat(data.getjobapldate());
                holder.title.setText(Html.fromHtml(mainval));
                holder.idx = String.valueOf(position);
                holder.uid = data.getUid();

                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //objSing.startUserIntro(data);
                    }
                });


                holder.imageCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        objSing.startUserGsmCall(data);
                    }
                });


                break;
            case LOADING:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return dataSet==null ? 0: dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardview;
        public TextView title;
        public ImageButton imageCell;
        public ImageView overflow ,overflow2,overflow3;
        public String idx,uid;

        public ViewHolder(@NonNull View view) {
            super(view);
            cardview = (CardView) itemView.findViewById(R.id.card_view_usr);
            title = (TextView) view.findViewById(R.id.title);
            imageCell =(ImageButton) view.findViewById(R.id.imageButton2);
            idx = "";
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataSet.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(UserJobReqDataModel mc) {
        dataSet.add(mc);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(ArrayList<UserJobReqDataModel> mcList) {
        for (UserJobReqDataModel mc : mcList) { add(mc); }
    }

    public void remove(UserJobReqDataModel city) {
        int position = dataSet.indexOf(city);
        if (position > -1) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) { remove(getItem(0)); }
    }

    public boolean isEmpty() { return getItemCount() == 0; }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new UserJobReqDataModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = dataSet.size() - 1;
        UserJobReqDataModel item = getItem(position);
        if (item != null) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public UserJobReqDataModel getItem(int position) {
        return dataSet.get(position);
    }

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat convertDateFormat = new SimpleDateFormat("dd MMM yy");
        SimpleDateFormat convettimeFormat = new SimpleDateFormat("hh:mm a");
        String condtft = convertDateFormat.format(date) +"  "+ convettimeFormat.format(date);
        return condtft;
    }

}
