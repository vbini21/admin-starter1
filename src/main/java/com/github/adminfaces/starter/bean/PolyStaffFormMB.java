/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.PolyStaff;
import com.github.adminfaces.starter.service.PolyStaffService;
import com.github.adminfaces.starter.util.Db;

import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ben
 */
@Named
@ViewScoped
public class PolyStaffFormMB implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(PolyStaffFormMB.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private PolyStaff user;
    protected PreparedStatement ps;
    protected ResultSet rs;
    @Inject
    PolyStaffService polyStaffService;

    public void init() {
        if (Faces.isAjaxRequest()) {
            return;
        }
        if (has(opno)) {
            user = polyStaffService.findByOpno(opno);
        } else {
            opno = String.valueOf(getNextPatientCode());
            user = new PolyStaff(opno);
        }
    }

    public int getNextPatientCode() {
        int patientcode = 0;
        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT tid FROM tbl_temis_polystaff ORDER BY tid DESC LIMIT 1");
            rs = ps.executeQuery();
            while (rs.next()) {
                int lastpatientcode = rs.getInt("tid");
                patientcode = lastpatientcode + 1;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return patientcode;
    }

    public String getOpno() {
        return opno;
    }

    public void setopno(String opno) {
        this.opno = opno;
    }

    public PolyStaff getReception() {
        return user;
    }

    public void setCar(PolyStaff user) {
        this.user = user;
    }

    public void remove() throws IOException {
        if (has(user) && has(user.getOpno())) {
            polyStaffService.remove(user);
            addDetailMessage("PolyStaff: " + user.getNames()
                    + " removed successfully");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("teachers-list.jsf");
        }
    }

    public boolean isExistingPatientCode(int pcode) {
        boolean existing = false;
        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT count(tid) as row_count FROM tbl_temis_polystaff WHERE tid =" + pcode);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("row_count") > 0) {
                    existing = true;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return existing;
    }

    public void save() {     
        if (!isExistingPatientCode(Integer.valueOf(user.getOpno()))) {
            polyStaffService.insert(user);
        } else {
            polyStaffService.update(user);
        }
    }

    public void clear() {
        user = new PolyStaff();
        opno = null;
    }

    public boolean isNew() {
        return user == null || user.getOpno() == null;
    }

    public List<String> completeModelEcde(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_ecde_centres where names like '%" + query + "%'");
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("names"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return data;
    }

    public List<String> completeModelTradeArea(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_tradearea where names like '%" + query + "%'");
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("names"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return data;
    }

    public List<String> completeModelTOE(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_terms_of_emp where names like '%" + query + "%'");
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("names"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return data;
    }

    public List<String> completeModelPolytechnic(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_polytechnic where names like '%" + query + "%'");
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("names"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return data;
    }
    
    public List<String> completeModelJobGroups(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_terms_job_grps where names like '%" + query + "%'");
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("names"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return data;
    }

    public List<String> completeModelProfessional_q(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_prof_qualifications where names like '%" + query + "%'");
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("names"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return data;
    }
}
