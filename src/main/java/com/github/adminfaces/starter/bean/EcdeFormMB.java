/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Ecde;
import com.github.adminfaces.starter.service.EcdeService;
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
import java.util.ArrayList;
import java.util.List;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;

/**
 * @author ben
 */
@Named
@ViewScoped
public class EcdeFormMB implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(EcdeFormMB.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private Ecde ecde;
    protected PreparedStatement ps;
    protected ResultSet rs;
    @Inject
    EcdeService ecdeService;

    public void init() {
        if (Faces.isAjaxRequest()) {
            return;
        }
        if (has(opno)) {
            ecde = ecdeService.findByOpno(opno);
        } else {
            opno = String.valueOf(getNextPatientCode());
            ecde = new Ecde(opno);
        }
    }

    public int getNextPatientCode() {
        int patientcode = 0;
        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT tid FROM tbl_temis_ecde_centres ORDER BY tid DESC LIMIT 1");
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

    public Ecde getReception() {
        return ecde;
    }

    public void setCar(Ecde ecde) {
        this.ecde = ecde;
    }

    public void remove() throws IOException {
        if (has(ecde) && has(ecde.getOpno())) {
            ecdeService.remove(ecde);
            addDetailMessage("Ecde: " + ecde.getNames()
                    + " removed successfully");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("ecde-list.jsf");
        }
    }

    public boolean isExistingPatientCode(int pcode) {
        boolean existing = false;
        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT count(tid) as row_count FROM tbl_temis_ecde_centres WHERE tid =" + pcode);
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
        if (!isExistingPatientCode(Integer.valueOf(ecde.getOpno()))) {
            ecdeService.insert(ecde);       
        } else {
            ecdeService.update(ecde);
        }
    }

    public void clear() {
        ecde = new Ecde();
        opno = null;
    }

    public boolean isNew() {
        return ecde == null || ecde.getOpno() == null;
    }

    public List<String> completeModelSchool(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_schools where names like '%" + query + "%'");
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
