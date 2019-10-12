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
public class User implements Serializable, Comparable<User> {

    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(User.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names;
    private String username;
    private String email;
    private String account_type;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public User() {
        init();
    }

    public User(String opno) {
        this.opno = opno;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(User o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public User opno(String opno) {
        this.opno = opno;
        return this;
    }

    public User names(String names) {
        this.names = names;
        return this;
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public User account_type(String account_type) {
        this.account_type = account_type;
        return this;
    }

    public User date_created(String date_created) {
        this.date_created = date_created;
        return this;
    }

    public User created_by(String created_by) {
        this.created_by = created_by;
        return this;
    }

}
