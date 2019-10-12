/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import org.apache.log4j.Logger;

@ManagedBean(name="reception")
@SessionScoped
public class Reception implements Serializable, Comparable<Reception> {
    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(Reception.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names;

    public Reception() {
        init();
    }

    public Reception(String opno) {
        this.opno = opno;
    }

    public void init() {
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(Reception o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public Reception opno(String opno) {
        this.opno = opno;
        return this;
    }

    public Reception names(String names) {
        this.names = names;
        return this;
    }
}
