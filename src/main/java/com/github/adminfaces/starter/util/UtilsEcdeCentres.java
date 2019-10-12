package com.github.adminfaces.starter.util;

import com.github.adminfaces.starter.model.Ecde;

import org.apache.log4j.Logger;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmpestano on 07/02/17.
 */
@ApplicationScoped
public class UtilsEcdeCentres extends Db implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(UtilsEcdeCentres.class);
    private static final long serialVersionUID = 1L;
    private List<Ecde> patients;

    @PostConstruct
    public void init() {
        Db.connect();
        logger.info("SELECT * FROM tbl_temis_ecde_centres");
        patients = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT * FROM tbl_temis_ecde_centres");
            rs = ps.executeQuery();
            create(rs, patients);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
    }

    private static void create(ResultSet rset, List<Ecde> patients) {
        Ecde user;
        int i = 1;
        try {
            while (rset.next()) {
                user = new Ecde(String.valueOf(i)).opno(rset.getString("tid")).
                        names(rset.getString("names")).date_created(rset.getString("date_created"))
                        .created_by(rset.getString("created_by"));
                patients.add(user);
                i += 1;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static void addDetailMessage(String message) {
        addDetailMessage(message, null);
    }

    public static void addDetailMessage(String message, FacesMessage.Severity severity) {

        FacesMessage facesMessage = Messages.create("").detail(message).get();
        if (severity != null && severity != FacesMessage.SEVERITY_INFO) {
            facesMessage.setSeverity(severity);
        }
        Messages.add(null, facesMessage);
    }

    @Produces
    public List<Ecde> getPatients() {
        return patients;
    }

}
