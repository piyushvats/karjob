package com.karigarjobs.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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

import static com.karigarjobs.user.HomeActivityU.jcfilterval;
import static com.karigarjobs.user.HomeActivityU.startJopPostInform;

public class ListPostActivityUser extends RecyclerView.Adapter<ListPostActivityUser.ViewHolder>
        implements Filterable {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    public static ArrayList<JobPostDataModelUser> dataSet;
    public static ArrayList<JobPostDataModelUser> dataSetAll;
    private boolean isLoadingAdded = false;
    Context context;

    ListPostActivityUser(Context context)
    {
        this.context=context;
        this.dataSet = new ArrayList<>();
        this.dataSetAll = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_list_card_view, viewGroup, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final JobPostDataModelUser data = dataSet.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                holder.jpid.setText(data.jid +": " +data.getjptitle());
                String pay =  context.getResources().getString(R.string.title_salaryrange) + ": " + data.salmin + " - " + data.salmax + " "+context.getResources().getString(R.string.title_rupee_sym);
                holder.title.setText(pay);
                holder.dnshift.setText(data.location);
                String jpdt =  context.getResources().getString(R.string.title_jobpost_date) + ": " +  convertFormat(data.jpdate);
                holder.jpdate.setText(jpdt);


                holder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

/*
                holder.overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //showPopupMenu(holder.overflow,holder.idx);
                    }
                });
                holder.overflow2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //showPopupMenu(holder.overflow,holder.idx);
                    }
                });
                holder.overflow3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //showPopupMenu(holder.overflow,holder.idx);
                    }
                });*/
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public Filter getFilter() {
        Filter obj = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    dataSet = dataSetAll;
                } else {
                    ArrayList<JobPostDataModelUser> filteredList = new ArrayList<>();
                    for (JobPostDataModelUser row : dataSetAll) {
                        if (jcfilterval.contains(row.catnum)) {
                            filteredList.add(row);
                        }
                    }
                    dataSet = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataSet;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataSet = (ArrayList<JobPostDataModelUser>) filterResults.values;
                notifyDataSetChanged();
            }

        };
        return obj;
    }

    @Override
    public int getItemCount() {
        return dataSet==null ? 0: dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardview;
        public TextView title, dnshift,jpdate,jpid;
        //public ImageView overflow ,overflow2,overflow3;
        public String idx;

        public ViewHolder(@NonNull View view) {
            super(view);
            cardview = (CardView) view.findViewById(R.id.card_view);
            title = (TextView) view.findViewById(R.id.title);
            dnshift = (TextView) view.findViewById(R.id.speciality);
            jpdate = (TextView) view.findViewById(R.id.educat);
            jpid = (TextView) view.findViewById(R.id.thumbnail);
/*
            overflow = (ImageView) view.findViewById(R.id.overflow);
            overflow2 = (ImageView) view.findViewById(R.id.overflow2);
            overflow3 = (ImageView) view.findViewById(R.id.overflow3);
*/
            idx = "";


        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataSet.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void refereshListData()
    {
        for (JobPostDataModelUser mc : dataSet) { add(mc); }
    }

    public void add(JobPostDataModelUser mc) {
        dataSet.add(mc);
        dataSetAll.add(mc);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(ArrayList<JobPostDataModelUser> mcList) {
        for (JobPostDataModelUser mc : mcList) { add(mc); }
    }

    public void remove(JobPostDataModelUser city) {
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
        add(new JobPostDataModelUser());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = dataSet.size() - 1;
        JobPostDataModelUser item = getItem(position);
        if (item != null) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public JobPostDataModelUser getItem(int position) {
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


/*    private void showPopupMenu(View view,String id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_item_menu_usr_list, popup.getMenu());
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
    }*/
}
