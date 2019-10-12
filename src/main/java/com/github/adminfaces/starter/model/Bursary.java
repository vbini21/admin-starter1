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
public class Bursary implements Serializable, Comparable<Bursary> {

    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(Bursary.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names;
    private String dob;
    private String sex;
    private String yos;
    private String study_level;
    private String admno;
    private String inst;
    private String inst_contact;
    private String inst_location;
    private String inst_email;
    private String school_type;
    private String school_category;
    private String village;
    private String course_type;
    private String disability;
    private String guardian_name;
    private String guardian_idno;
    private String parent_contact;
    private String date_created;
    private String created_by;

    public Bursary() {
    }

    public void init() {
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public Bursary(String opno) {
        this.opno = opno;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(Bursary o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getYos() {
        return yos;
    }

    public void setYos(String yos) {
        this.yos = yos;
    }

    public String getStudy_level() {
        return study_level;
    }

    public void setStudy_level(String study_level) {
        this.study_level = study_level;
    }

    public String getAdmno() {
        return admno;
    }

    public void setAdmno(String admno) {
        this.admno = admno;
    }

    public String getInst() {
        return inst;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }

    public String getInst_contact() {
        return inst_contact;
    }

    public void setInst_contact(String inst_contact) {
        this.inst_contact = inst_contact;
    }

    public String getInst_location() {
        return inst_location;
    }

    public void setInst_location(String inst_location) {
        this.inst_location = inst_location;
    }

    public String getInst_email() {
        return inst_email;
    }

    public void setInst_email(String inst_email) {
        this.inst_email = inst_email;
    }

    public String getSchool_type() {
        return school_type;
    }

    public void setSchool_type(String school_type) {
        this.school_type = school_type;
    }

    public String getSchool_category() {
        return school_category;
    }

    public void setSchool_category(String school_category) {
        this.school_category = school_category;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getCourse_type() {
        return course_type;
    }

    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getGuardian_name() {
        return guardian_name;
    }

    public void setGuardian_name(String guardian_name) {
        this.guardian_name = guardian_name;
    }

    public String getGuardian_idno() {
        return guardian_idno;
    }

    public void setGuardian_idno(String guardian_idno) {
        this.guardian_idno = guardian_idno;
    }

    public String getParent_contact() {
        return parent_contact;
    }

    public void setParent_contact(String parent_contact) {
        this.parent_contact = parent_contact;
    }

    public Bursary parent_contact(String parent_contact) {
        this.parent_contact = parent_contact;
        return this;
    }

    public Bursary guardian_idno(String guardian_idno) {
        this.guardian_idno = guardian_idno;
        return this;
    }

    public Bursary guardian_name(String guardian_name) {
        this.guardian_name = guardian_name;
        return this;
    }

    public Bursary disability(String disability) {
        this.disability = disability;
        return this;
    }

    public Bursary course_type(String course_type) {
        this.course_type = course_type;
        return this;
    }

    public Bursary village(String village) {
        this.village = village;
        return this;
    }

    public Bursary school_category(String school_category) {
        this.school_category = school_category;
        return this;
    }

    public Bursary school_type(String school_type) {
        this.school_type = school_type;
        return this;
    }

    public Bursary inst_email(String inst_email) {
        this.inst_email = inst_email;
        return this;
    }

    public Bursary dob(String dob) {
        this.dob = dob;
        return this;
    }

    public Bursary sex(String sex) {
        this.sex = sex;
        return this;
    }

    public Bursary yos(String yos) {
        this.yos = yos;
        return this;
    }

    public Bursary study_level(String study_level) {
        this.study_level = study_level;
        return this;
    }

    public Bursary admno(String admno) {
        this.admno = admno;
        return this;
    }

    public Bursary inst(String inst) {
        this.inst = inst;
        return this;
    }

    public Bursary inst_contact(String inst_contact) {
        this.inst_contact = inst_contact;
        return this;
    }

    public Bursary inst_location(String inst_location) {
        this.inst_location = inst_location;
        return this;
    }

    public Bursary opno(String opno) {
        this.opno = opno;
        return this;
    }

    public Bursary names(String names) {
        this.names = names;
        return this;
    }

    public Bursary date_created(String date_created) {
        this.date_created = date_created;
        return this;
    }

    public Bursary created_by(String created_by) {
        this.created_by = created_by;
        return this;
    }
}
