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
public class Teacher implements Serializable, Comparable<Teacher> {

    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(Teacher.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names;
    private String school;
    private String ecde;
    private String email;
    private String tscno;
    private String idno;
    private String academic_q;
    private String prof_q;
    private String sponsor;
    private String date_created;
    private String created_by;
    private String phone;

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

    public String getEcde() {
        return ecde;
    }

    public void setEcde(String ecde) {
        this.ecde = ecde;
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

    public Teacher() {
        init();
    }

    public Teacher(String opno) {
        this.opno = opno;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(Teacher o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTscno() {
        return tscno;
    }

    public void setTscno(String tscno) {
        this.tscno = tscno;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getAcademic_q() {
        return academic_q;
    }

    public void setAcademic_q(String academic_q) {
        this.academic_q = academic_q;
    }

    public String getProf_q() {
        return prof_q;
    }

    public void setProf_q(String prof_q) {
        this.prof_q = prof_q;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }
    
    public Teacher school(String school) {
        this.school = school;
        return this;
    }
    public Teacher opno(String opno) {
        this.opno = opno;
        return this;
    }

    public Teacher names(String names) {
        this.names = names;
        return this;
    }

    public Teacher ecde(String ecde) {
        this.ecde = ecde;
        return this;
    }

    public Teacher email(String email) {
        this.email = email;
        return this;
    }

    public Teacher date_created(String date_created) {
        this.date_created = date_created;
        return this;
    }

    public Teacher created_by(String created_by) {
        this.created_by = created_by;
        return this;
    }
    
    public Teacher tscno(String tscno) {
        this.tscno = tscno;
        return this;
    }
    
    public Teacher idno(String idno) {
        this.idno = idno;
        return this;
    }
    
    public Teacher academic_q(String academic_q) {
        this.academic_q = academic_q;
        return this;
    }
    
    public Teacher prof_q(String prof_q) {
        this.prof_q = prof_q;
        return this;
    }
    
    public Teacher sponsor(String sponsor) {
        this.sponsor = sponsor;
        return this;
    }
    
    public Teacher phone(String phone) {
        this.phone = phone;
        return this;
    }
   
}