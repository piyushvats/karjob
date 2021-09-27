package com.karigarjobs.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.karigarjobs.R;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.karigarjobs.provider.HomeActivity.startJopPostInform;

public class ListPostActivity extends RecyclerView.Adapter<ListPostActivity.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    public static ArrayList<JobPostDataModel> dataSet;
    private boolean isLoadingAdded = false;
    Context context;

    ListPostActivity(Context context)
    {
        this.context=context;
        this.dataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_list_card_view, viewGroup, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final JobPostDataModel data = dataSet.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                String strtitle = context.getResources().getString(R.string.title_jobname) + ":" + data.getjptitle();
                holder.title.setText(strtitle);
                ArrayList<String> dnshift = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.work_shift)));
                String strshift = dnshift.get(Integer.parseInt(data.dnshift) - 1);
                String strv = context.getResources().getString(R.string.title_jobworkshift) + ":" + strshift;
                holder.dnshift.setText(strv);
                String jpdt =  context.getResources().getString(R.string.title_jobpost_date) + ":" +  convertFormat(data.jpdate);
                holder.jpdate.setText(jpdt);
                holder.idx = String.valueOf(position);
                strv = context.getResources().getString(R.string.title_job_post_id) + data.jid;
                holder.jpid.setText(strv);

                holder.jpid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                holder.dnshift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                holder.jpid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                holder.overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(holder.overflow,holder.idx);
                    }
                });
                holder.overflow2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(holder.overflow,holder.idx);
                    }
                });
                holder.overflow3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(holder.overflow,holder.idx);
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
        public TextView title, dnshift,jpdate,jpid;
        public ImageView overflow ,overflow2,overflow3;
        public String idx;

        public ViewHolder(@NonNull View view) {
            super(view);
            cardview = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) view.findViewById(R.id.title);
            dnshift = (TextView) view.findViewById(R.id.speciality);
            jpdate = (TextView) view.findViewById(R.id.educat);
            jpid = (TextView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            overflow2 = (ImageView) view.findViewById(R.id.overflow2);
            overflow3 = (ImageView) view.findViewById(R.id.overflow3);
            idx = "";


        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataSet.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(JobPostDataModel mc) {
        dataSet.add(mc);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(ArrayList<JobPostDataModel> mcList) {
        for (JobPostDataModel mc : mcList) { add(mc); }
    }

    public void remove(JobPostDataModel city) {
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
        add(new JobPostDataModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = dataSet.size() - 1;
        JobPostDataModel item = getItem(position);
        if (item != null) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public JobPostDataModel getItem(int position) {
        return dataSet.get(position);
    }

    private void showPopupMenu(View view,String id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_item_menu_list, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(id));
        popup.show();
    }


    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private String usrid ;
        public MyMenuItemClickListener(String id) {
            this.usrid = id;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delpost:
                    //Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    ((HomeActivity)context).startdeactivatepost(dataSet.get(Integer.parseInt(this.usrid)).jid);
                    return true;
                case R.id.action_viewapp:
                    //Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    HomeActivity.parent = "VIEWUSR";
                    //((HomeActivity)context).startjobapplications(dataSet.get(Integer.parseInt(this.usrid)).jid);
                    Intent mIntent = new Intent(context, HomeActivity.class);
                    Bundle b = new Bundle();
                    b.putString("jpid", dataSet.get(Integer.parseInt(this.usrid)).jid); //Your id
                    mIntent.putExtras(b);
                    context.startActivity(mIntent);
                    ((HomeActivity)context).finish();
                    return true;
                default:
            }
            return false;
        }
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
