package com.karigarjobs.provider;

public class JobPostDataModel {


    String jid;
    String cname;
    String catname;
    String catnum;
    String title;
    String location;
    String expdate;
    String iscontract;
    String dnshift;
    String salmin;
    String salmax;
    String jpdate;
    String vacencyno;
    String otlimithour;
    String intermode;
    String intertime;
    String jobblist;


    public JobPostDataModel() {

    }

    public void set(String jid,String cname,String catname,String catnum,String title,String location,String expdate,String iscontract,String dnshift,String salmin,String salmax,String jpdate,String otlim,String vacency,String intermd,String intertm ,String jblist)
    {
        this.jid = jid;
        this.cname= cname;
        this.catname = catname;
        this.catnum = catnum;
        this.title = title;
        this.location = location;
        this.expdate = expdate;
        this.iscontract = iscontract;
        this.dnshift = dnshift;
        this.salmin = salmin;
        this.salmax = salmax;
        this.jpdate = jpdate;
        this.vacencyno = vacency;
        this.otlimithour = otlim;
        this.intermode = intermd;
        this.intertime = intertm;
        this.jobblist = jblist;
    }

    public String getjpid() { return jid; }

    public String getComname() { return cname; }

    public String getcatname() { return catname; }

    public String getjptitle() { return title; }

    public String getlocation() { return location; }

    public String getexpdate() { return expdate; }
    public String getcontract() { return iscontract; }
    public String getshift() { return dnshift; }
    public String getminsal() { return salmin; }
    public String getsalmax() { return salmax; }
    public String getpostdate() { return jpdate; }



    public void setStrtitle(String val) { this.title = val; }
    public void setJPId(String val) { this.jid = val; }
    public void setContract(String val) { this.iscontract = val; }



}
