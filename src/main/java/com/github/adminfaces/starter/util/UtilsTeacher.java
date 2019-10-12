package com.github.adminfaces.starter.util;

import com.github.adminfaces.starter.model.Teacher;

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
public class UtilsTeacher extends Db implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(UtilsTeacher.class);
    private static final long serialVersionUID = 1L;
    private List<Teacher> patients;

    @PostConstruct
    public void init() {
        Db.connect();
        logger.info("SELECT * FROM tbl_temis_teachers_ecde");
        patients = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT * FROM tbl_temis_teachers_ecde");
            rs = ps.executeQuery();
            create(rs, patients);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
    }

    private static void create(ResultSet rset, List<Teacher> patients) {
        Teacher teacher;
        int i = 1;
        try {
            while (rset.next()) {
                teacher = new Teacher(String.valueOf(i)).opno(rset.getString("tid")).names(rset.getString("names")).ecde(rset.getString("ecde_id")).
                        email(rset.getString("email")).school(rset.getString("school_id")).tscno(rset.getString("tscno")).idno(rset.getString("idno")).
                        academic_q(rset.getString("academic_q")).prof_q(rset.getString("prof_q")).sponsor(rset.getString("sponsor")).
                        phone(rset.getString("phone")).date_created(rset.getString("date_created")).created_by(rset.getString("created_by"));
                patients.add(teacher);
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
    public List<Teacher> getPatients() {
        return patients;
    }
}
