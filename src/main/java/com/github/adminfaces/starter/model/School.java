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
public class School implements Serializable, Comparable<School> {

    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(School.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names; 
    private String email;
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

    public School() {
        init();
    }

    public School(String opno) {
        this.opno = opno;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(School o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public School opno(String opno) {
        this.opno = opno;
        return this;
    }

    public School names(String names) {
        this.names = names;
        return this;
    }
    
    public School email(String email) {
        this.email = email;
        return this;
    }

    public School phone(String phone) {
        this.phone = phone;
        return this;
    }
    public School date_created(String date_created) {
        this.date_created = date_created;
        return this;
    }

    public School created_by(String created_by) {
        this.created_by = created_by;
        return this;
    }
    
}
