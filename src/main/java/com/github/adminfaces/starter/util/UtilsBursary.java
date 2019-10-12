package com.github.adminfaces.starter.util;

import com.github.adminfaces.starter.model.Bursary;

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
public class UtilsBursary extends Db implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(UtilsBursary.class);
    private static final long serialVersionUID = 1L;
    private List<Bursary> patients;

    @PostConstruct
    public void init() {
        Db.connect();
        logger.info("SELECT * FROM tbl_temis_bursaries");
        patients = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT * FROM tbl_temis_bursaries");
            rs = ps.executeQuery();
            create(rs, patients);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
    }

    private static void create(ResultSet rset, List<Bursary> patients) {
        Bursary user;
        int i = 1;
        try {
            while (rset.next()) {
                user = new Bursary(String.valueOf(i)).opno(rset.getString("tid")).
                        names(rset.getString("names")).dob(rset.getString("dob")).sex(rset.getString("sex")).yos(rset.getString("yos")).
                        study_level(rset.getString("study_level")).admno(rset.getString("admno")).inst(rset.getString("inst")).inst_contact(rset.getString("inst_contact")).
                        inst_location(rset.getString("inst_location")).inst_email(rset.getString("inst_email")).school_type(rset.getString("school_type")).school_category(rset.getString("school_category")).
                        village(rset.getString("village")).course_type(rset.getString("course_type")).disability(rset.getString("disability")).guardian_name(rset.getString("guardian_name")).
                        guardian_idno(rset.getString("guardian_idno")).parent_contact(rset.getString("parent_contact")).
                        date_created(rset.getString("date_created")).created_by(rset.getString("created_by"));
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
    public List<Bursary> getPatients() {
        return patients;
    }

}
