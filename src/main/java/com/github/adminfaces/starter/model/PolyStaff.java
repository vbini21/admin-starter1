/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

@Named
@SessionScoped
public class PolyStaff implements Serializable, Comparable<PolyStaff> {

    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(PolyStaff.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String polytechnic;
    private String names;
    private String phone;
    private String email;
    private String tradeArea;
    private String qualification;    
    private String terms_of_emp;
    private String job_grp;   
    private String date_created;
    private String created_by;   

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void init() {
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
   
    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public PolyStaff() {
        init();
    }

    public PolyStaff(String opno) {
        this.opno = opno;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(PolyStaff o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public String getTradeArea() {
        return tradeArea;
    }

    public void setTradeArea(String tradeArea) {
        this.tradeArea = tradeArea;
    }

    public String getTerms_of_emp() {
        return terms_of_emp;
    }

    public void setTerms_of_emp(String terms_of_emp) {
        this.terms_of_emp = terms_of_emp;
    }

    public String getJob_grp() {
        return job_grp;
    }

    public void setJob_grp(String job_grp) {
        this.job_grp = job_grp;
    }
      
    public PolyStaff tradeArea(String tradeArea) {
        this.tradeArea = tradeArea;
        return this;
    }
    public PolyStaff opno(String opno) {
        this.opno = opno;
        return this;
    }

    public PolyStaff names(String names) {
        this.names = names;
        return this;
    }

    public PolyStaff qualification(String qualification) {
        this.qualification = qualification;
        return this;
    }

    public PolyStaff email(String email) {
        this.email = email;
        return this;
    }

    public PolyStaff date_created(String date_created) {
        this.date_created = date_created;
        return this;
    }

    public PolyStaff created_by(String created_by) {
        this.created_by = created_by;
        return this;
    }
    
    public PolyStaff terms_of_emp(String terms_of_emp) {
        this.terms_of_emp = terms_of_emp;
        return this;
    }
    
    public PolyStaff job_grp(String job_grp) {
        this.job_grp = job_grp;
        return this;
    }
      
    public PolyStaff phone(String phone) {
        this.phone = phone;
        return this;
    }
    public PolyStaff polytechnic(String polytechnic) {
        this.polytechnic = polytechnic;
        return this;
    }
	
	public String getPolytechnic() {
		return polytechnic;
	}

	public void setPolytechnic(String polytechnic) {
		this.polytechnic = polytechnic;
	}
   
}