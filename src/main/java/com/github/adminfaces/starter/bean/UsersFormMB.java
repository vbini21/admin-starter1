/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.User;
import com.github.adminfaces.starter.service.UserService;
import com.github.adminfaces.starter.util.Db;
import com.github.adminfaces.starter.util.SendingEmail;

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

/**
 * @author ben
 */
@Named
@ViewScoped
public class UsersFormMB implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(UsersFormMB.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private User user;
    protected PreparedStatement ps;
    protected ResultSet rs;
    @Inject
    UserService userService;

    public void init() {
        if (Faces.isAjaxRequest()) {
            return;
        }
        if (has(opno)) {
            user = userService.findByOpno(opno);
        } else {
            opno = String.valueOf(getNextPatientCode());
            user = new User(opno);
        }
    }

    public int getNextPatientCode() {
        int patientcode = 0;
        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT tid FROM tbl_temis_users ORDER BY tid DESC LIMIT 1");
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

    public User getReception() {
        return user;
    }

    public void setCar(User user) {
        this.user = user;
    }

    public void remove() throws IOException {
        if (has(user) && has(user.getOpno())) {
            userService.remove(user);
            addDetailMessage("User: " + user.getNames()
                    + " removed successfully");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("users-list.jsf");
        }
    }

    public boolean isExistingPatientCode(int pcode) {
        boolean existing = false;
        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT count(tid) as row_count FROM tbl_temis_users WHERE tid =" + pcode);
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
            userService.insert(user);       
        } else {
            userService.update(user);
        }
    }

    public void clear() {
        user = new User();
        opno = null;
    }

    public boolean isNew() {
        return user == null || user.getOpno() == null;
    }

}
