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
public class Att implements Serializable, Comparable<Att> {
    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(Att.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names;
    private String selectedDate;
    private String status;

    public Att() {
        init();
    }

    public Att(String opno) {
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
    public int compareTo(Att o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public Att opno(String opno) {
        this.opno = opno;
        return this;
    }

    public Att names(String names) {
        this.names = names;
        return this;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Att status(String status) {
        this.status = status;
        return this;
    }

	public String getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}
	
	public Att selectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
        return this;
    }
}
