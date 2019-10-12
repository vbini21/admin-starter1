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
public class Infra implements Serializable, Comparable<Infra> {
    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(Infra.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names;
    private String class_perm;
    private String class_temp;
    private String class_req;
    private String toilets;
    private String toilets_req;
    private String desks;
    private String fencing;
    private String sponsor;
    private String date_created;
    private String created_by;

    public Infra() {
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

    public Infra(String opno) {
        this.opno = opno;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(Infra o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public Infra opno(String opno) {
        this.opno = opno;
        return this;
    }

    public Infra names(String names) {
        this.names = names;
        return this;
    }

    public Infra date_created(String date_created) {
        this.date_created = date_created;
        return this;
    }

    public Infra created_by(String created_by) {
        this.created_by = created_by;
        return this;
    }

    public String getClass_perm() {
        return class_perm;
    }

    public void setClass_perm(String class_perm) {
        this.class_perm = class_perm;
    }

    public String getClass_temp() {
        return class_temp;
    }

    public void setClass_temp(String class_temp) {
        this.class_temp = class_temp;
    }

    public String getClass_req() {
        return class_req;
    }

    public void setClass_req(String class_req) {
        this.class_req = class_req;
    }

    public String getToilets() {
        return toilets;
    }

    public void setToilets(String toilets) {
        this.toilets = toilets;
    }

    public String getToilets_req() {
        return toilets_req;
    }

    public void setToilets_req(String toilets_req) {
        this.toilets_req = toilets_req;
    }

    public String getDesks() {
        return desks;
    }

    public void setDesks(String desks) {
        this.desks = desks;
    }

    public String getFencing() {
        return fencing;
    }

    public void setFencing(String fencing) {
        this.fencing = fencing;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }
   
    public Infra class_perm(String class_perm){
        this.class_perm = class_perm;
        return this;
    }
    
    public Infra class_temp(String class_temp){
        this.class_temp = class_temp;
        return this;
    }
    
    public Infra class_req(String class_req){
        this.class_req = class_req;
        return this;
    }
    
    public Infra toilets(String toilets){
        this.toilets = toilets;
        return this;
    }
    
    public Infra toilets_req(String toilets_req){
        this.toilets_req = toilets_req;
        return this;
    }
    
    public Infra desks(String desks){
        this.desks = desks;
        return this;
    }
    
    public Infra fencing(String fencing){
        this.fencing = fencing;
        return this;
    }
    
    public Infra sponsor(String sponsor){
        this.sponsor = sponsor;
        return this;
    }
}
