package com.karigarjobs.provider;

public class UserJobReqDataModel {

    String uid;
    String uname;
    String age;
    String caddr;
    String caddrpin;
    String mobile;
    String idtype;
    String idnum;
    String updatetm;
    String jobcat;

    //for exp
    String cname[];
    String ccatname[];
    String jtitle[];
    String jlocation[];
    String jsdate[];
    String jedate[];


    //for edcation
    String eduname[];
    String edudetail[];
    String edudate[];

    String curdate;
    String jsid;

    UserJobReqDataModel()
    {


    }

    public void set(String uid,String uname,String age,String caddr,String caddrpin,String mobile,String idtype,String idnum,String updatetm,String jobcat,
                    String[] cname,String[] ccatname ,String[] jtitle,String[] jlocation,String[] jsdate,String[] jedate,String[] eduname,String[] edudetail,String[] edudate ,String curdate,String jobsid)
    {
        this.uid = uid;
        this.uname= uname;
        this.age = age;
        this.caddr = caddr;
        this.caddrpin = caddrpin;
        this.mobile = mobile;
        this.idtype = idtype;
        this.idnum= idnum;
        this.updatetm = updatetm;
        this.jobcat = jobcat;
        this.cname =  cname;
        this.ccatname = ccatname;
        this.jtitle = jtitle;
        this.jlocation = jlocation;
        this.jsdate = jsdate;
        this.jedate = jedate;
        this.eduname = eduname;
        this.edudetail = edudetail;
        this.edudate = edudate;

        this.curdate = curdate;
        this.jsid = jobsid;

    }

    public String getUid(){ return this.uid;}
    public String getUname(){ return this.uname;}
    public String getAge() {return this.age;}
    public String getCaddr() {return this.caddr;}
    public String getCaddrpin(){return this.caddrpin;}
    public String getMobile() {return this.mobile;}
    public String getIdtype() {return this.mobile;}
    public String getIdnum() {return this.idnum;}

    public String[] getCname() {return this.cname;}
    public String[] getCcatname(){return this.ccatname;}
    public String[] getJtitle(){return this.jtitle;}
    public String[] getJlocation(){return this.jlocation;}
    public String[] getJsdate(){return this.jsdate;}
    public String[] getJedate(){return this.jedate;}

    public String[] getEduname(){return this.eduname;}
    public String[] getEdudetail(){return this.edudetail;}
    public String[] getEdudate(){return this.edudate;}
    public String getJobcat(){return this.jobcat;}
    public String getjobapldate(){return this.updatetm;}








}
