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
public class Ecde implements Serializable, Comparable<Ecde> {

	/**
	 *
	 */
	protected PreparedStatement ps;
	protected ResultSet rs;
	private static Logger logger = Logger.getLogger(Ecde.class);
	private static final long serialVersionUID = 1L;
	private String opno;
	private String names;
	private String school;
	private String date_created;
	private String created_by;

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public void init() {
	}

	public String getDate_created() {
		return date_created;
	}

	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}

	public Ecde() {
		init();
	}

	public Ecde(String opno) {
		this.opno = opno;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	@Override
	public int compareTo(Ecde o) {
		return this.opno.compareTo(o.getOpno());
	}

	public String getOpno() {
		return opno;
	}

	public void setOpno(String opno) {
		this.opno = opno;
	}

	public Ecde school(String school) {
		this.school = school;
		return this;
	}

	public Ecde opno(String opno) {
		this.opno = opno;
		return this;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public Ecde names(String names) {
		this.names = names;
		return this;
	}

	public Ecde date_created(String date_created) {
		this.date_created = date_created;
		return this;
	}

	public Ecde created_by(String created_by) {
		this.created_by = created_by;
		return this;
	}

}
