package com.karigarjobs.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.karigarjobs.R;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.karigarjobs.user.HomeActivityU.objSing;
import static com.karigarjobs.user.HomeActivityU.startJopPostInform;

public class ListJobPostUserReq extends RecyclerView.Adapter<ListJobPostUserReq.ViewHolder>{


    private static final int ITEM = 0;
    private static final int LOADING = 1;
    public static ArrayList<JobPostDataModelUsrApl> dataSet;
    private boolean isLoadingAdded = false;
    Context context;

    ListJobPostUserReq(Context context)
    {
        this.context=context;
        this.dataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Toolbar toolbar = (Toolbar) (((Activity)context).findViewById(R.id.toolbar));
        toolbar.setTitle(R.string.title_activity_prejpapl);

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.job_post_req_card_view, viewGroup, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final JobPostDataModelUsrApl data = dataSet.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                String strtitle = context.getResources().getString(R.string.title_jobname) + ":" + data.getjptitle();
                holder.title.setText(strtitle);

                ArrayList<String> dnshift = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.work_shift)));
                String strshift = dnshift.get(Integer.parseInt(data.dnshift) - 1);
                String strv = context.getResources().getString(R.string.title_jobworkshift) + ":" + strshift;
                holder.dnshift.setText(strv);
                String apldt =  context.getResources().getString(R.string.title_jpapplied_dt) + ":" + convertFormat(data.usraplydate);
                holder.jpdate.setText(apldt);
                holder.idx = String.valueOf(position);
                strv = context.getResources().getString(R.string.title_job_post_id) + data.jid;
                holder.jpid.setText(strv);

                holder.jpid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        showalertmsg(position);
/*                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                });

                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        showalertmsg(position);
/*                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                });


                holder.dnshift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        showalertmsg(position);
/*                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                });


                holder.jpid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //holder.jpid
                        showalertmsg(position);
/*                        try {
                            startJopPostInform(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
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


    public void showalertmsg(int pos)
    {
        JobPostDataModelUsrApl data = dataSet.get(pos);
        String caddr = data.offaddr;
        String caddrp = data.offaddrpin;
        String cpername1 = data.comp_name;
        String cperdesg1 = data.comp_desg;
        String cperph1 = data.comp_mob;
        String cemail = data.comemail;

        String msgstr = data.jid+":"+cpername1+"-"+cperdesg1+"\n";
        msgstr = msgstr + caddr+","+caddrp+"\n"+cemail+"\n"+cperph1+"\n";

        new AlertDialog.Builder( context)
                .setTitle(R.string.alert_applyjob_info)
                .setMessage(msgstr)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
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
            dnshift = (TextView) view.findViewById(R.id.daynightshift);
            jpdate = (TextView) view.findViewById(R.id.jobpostdt);
            jpid = (TextView) view.findViewById(R.id.jobpostid);
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


    public void add(JobPostDataModelUsrApl mc) {
        dataSet.add(mc);
        notifyItemInserted(dataSet.size() - 1);
    }

    public void addAll(ArrayList<JobPostDataModelUsrApl> mcList) {
        for (JobPostDataModelUsrApl mc : mcList) { add(mc); }
    }

    public void remove(JobPostDataModelUsrApl city) {
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
        add(new JobPostDataModelUsrApl());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = dataSet.size() - 1;
        JobPostDataModelUsrApl item = getItem(position);
        if (item != null) {
            dataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public JobPostDataModelUsrApl getItem(int position) {
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


    private void showPopupMenu(View view,String id) {
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
                case R.id.action_viewdetail:
                    //Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();

                    startJobInform(this.usrid);
                    return true;
                case R.id.action_viewstatus:
                    //Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
/*                    HomeActivity.parent = "VIEWUSR";
                    //((HomeActivity)context).startjobapplications(dataSet.get(Integer.parseInt(this.usrid)).jid);
                    Intent mIntent = new Intent(context, HomeActivity.class);
                    Bundle b = new Bundle();
                    b.putString("jpid", dataSet.get(Integer.parseInt(this.usrid)).jid); //Your id
                    mIntent.putExtras(b);
                    context.startActivity(mIntent);
                    ((HomeActivity)context).finish();*/
                    return true;
                case R.id.action_sendfeed:
                    startjobfeedback(this.usrid);
                    return true;
                default:
            }
            return false;
        }
    }

    public void startJobInform(String idx)
    {

        try {
            int pos = Integer.parseInt(idx);
            JobPostDataModelUser data = new JobPostDataModelUser();
            data.jid = dataSet.get(pos).jid;
            data.comid = dataSet.get(pos).comid;
            data.cname = dataSet.get(pos).cname;
            data.catname = dataSet.get(pos).catname;
            data.title = dataSet.get(pos).title;
            data.detail = dataSet.get(pos).detail;
            data.location = dataSet.get(pos).location;
            data.expdate = dataSet.get(pos).expdate;
            data.iscontract = dataSet.get(pos).iscontract;
            data.dnshift = dataSet.get(pos).dnshift;
            data.salmin = dataSet.get(pos).salmin;
            data.salmax = dataSet.get(pos).salmax;
            data.jpdate = dataSet.get(pos).jpdate;
            data.otlimithour = dataSet.get(pos).otlimithour;
            data.vacencyno = dataSet.get(pos).vacencyno;
            data.intermode = dataSet.get(pos).intermode;
            data.intertime = dataSet.get(pos).intertime;
            data.jobblist = dataSet.get(pos).jobblist;

            startJopPostInform(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startjobfeedback(final String idx)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View alertLayout = inflater.inflate(R.layout.alert_feedbk_dialog, null);
        final EditText feedinput = alertLayout.findViewById(R.id.editText3);
        //final TextInputEditText etPassword = alertLayout.findViewById(R.id.tiet_password);
        RadioGroup rb1 = alertLayout.findViewById(R.id.radiogp_ratefeed);
        rb1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                boolean checked = ((RadioButton) alertLayout.findViewById(checkedId)).isChecked();
                switch(checkedId) {
                    case R.id.radio_verybad:
                        if (checked)
                            // vewry bad
                            ((TextView) alertLayout.findViewById(R.id.textView5)).setText("1");
                            break;
                    case R.id.radio_bad:
                        if (checked)
                            // bad
                            ((TextView) alertLayout.findViewById(R.id.textView5)).setText("2");
                            break;
                    case R.id.radio_average:
                        if (checked)
                            // average
                            ((TextView) alertLayout.findViewById(R.id.textView5)).setText("3");
                            break;
                    case R.id.radio_good:
                        if (checked)
                            // good
                            ((TextView) alertLayout.findViewById(R.id.textView5)).setText("4");
                            break;
                    case R.id.radio_excellent:
                        if (checked)
                            // excellent
                            ((TextView) alertLayout.findViewById(R.id.textView5)).setText("5");
                            break;
                }
            }
        });


        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getResources().getString(R.string.usr_jp_feedbk));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(context.getResources().getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton(context.getResources().getString(R.string.action_submitpwd), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userfb = feedinput.getText().toString();
                String feedrateres = ((TextView) alertLayout.findViewById(R.id.textView5)).getText().toString();
                if(!feedrateres.isEmpty())
                {
                    int pos = Integer.parseInt(idx);
                    JobPostDataModelUser data = new JobPostDataModelUser();
                     String jsid = dataSet.get(pos).jsid;
                     //send feedback
                    objSing.sendusrfeedback(jsid,feedrateres,userfb,"");
                }

                //String pass = etPassword.getText().toString();
                //Toast.makeText(getBaseContext(), "Username: " + user + " Password: " + pass, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
