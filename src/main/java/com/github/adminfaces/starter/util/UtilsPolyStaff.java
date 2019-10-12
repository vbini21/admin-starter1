package com.github.adminfaces.starter.util;

import com.github.adminfaces.starter.model.PolyStaff;

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
public class UtilsPolyStaff extends Db implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(UtilsPolyStaff.class);
    private static final long serialVersionUID = 1L;
    private List<PolyStaff> patients;

    @PostConstruct
    public void init() {
        Db.connect();
        logger.info("SELECT * FROM tbl_temis_polystaff");
        patients = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT * FROM tbl_temis_polystaff");
            rs = ps.executeQuery();
            create(rs, patients);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
    }

    private static void create(ResultSet rset, List<PolyStaff> patients) {
        PolyStaff polyStaff;
        int i = 1;
        try {        	
            while (rset.next()) {
                polyStaff = new PolyStaff(String.valueOf(i)).opno(rset.getString("tid")).names(rset.getString("names")).phone(rset.getString("phone")).
                        email(rset.getString("email")).polytechnic(rset.getString("polytechnic_id")).tradeArea(rset.getString("tradeArea")).qualification(rset.getString("qualification")).terms_of_emp(rset.getString("terms_of_emp")).
                        job_grp(rset.getString("job_grp")).date_created(rset.getString("date_created")).created_by(rset.getString("created_by"));
                patients.add(polyStaff);
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
    public List<PolyStaff> getPatients() {
        return patients;
    }
}
