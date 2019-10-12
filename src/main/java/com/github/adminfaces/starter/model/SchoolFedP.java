/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ejb.Init;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

@Named
@SessionScoped
public class SchoolFedP implements Serializable, Comparable<SchoolFedP> {

	/**
	 *
	 */
	protected PreparedStatement ps;
	protected ResultSet rs;
	private static Logger logger = Logger.getLogger(SchoolFedP.class);
	private static final long serialVersionUID = 1L;
	int tid;
	String name;
	String unit;
	String qty;
	boolean editable;
	private String date_created;	
	private String created_by;

	public SchoolFedP() {		
		init();
	}
	public void init() {		
    }
		
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	@Override
	public int compareTo(SchoolFedP o) {
		return this.name.compareTo(o.getName());
	}

	public SchoolFedP name(String name) {
		this.name = name;
		return this;
	}

	public SchoolFedP unit(String unit) {
		this.unit = unit;
		return this;
	}

	public SchoolFedP qty(String qty) {
		this.qty = qty;
		return this;
	}

	public SchoolFedP tid(int tid) {
		this.tid = tid;
		return this;
	}

	public SchoolFedP(int tid) {
		this.tid = tid;
	}
	
	public SchoolFedP date_created(String date_created) {
		this.date_created = date_created;
		return this;
	}
	
	public SchoolFedP created_by(String created_by) {
		this.created_by = created_by;
		return this;
	}
	
	public String getDate_created() {
		return date_created;
	}

	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	
	
}
