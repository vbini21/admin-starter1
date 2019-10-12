package com.github.adminfaces.starter.util;

import com.github.adminfaces.starter.model.Infra;

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
public class UtilsInfra extends Db implements Serializable {

    /**
     *
     */
    private static Logger logger = Logger.getLogger(UtilsInfra.class);
    private static final long serialVersionUID = 1L;
    private List<Infra> patients;

    @PostConstruct
    public void init() {
        Db.connect();
        logger.info("SELECT * FROM tbl_temis_infra");
        patients = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT * FROM tbl_temis_infra");
            rs = ps.executeQuery();
            create(rs, patients);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
    }

    private static void create(ResultSet rset, List<Infra> patients) {
        Infra infra;
        int i = 1;
        try {
            while (rset.next()) {
                infra = new Infra(String.valueOf(i)).opno(rset.getString("tid")).
                        names(rset.getString("ecde")).class_perm(rset.getString("class_perm")).
                        class_temp(rset.getString("class_temp")).class_req(rset.getString("class_req")).
                        toilets(rset.getString("toilets")).toilets_req(rset.getString("toilets_req")).
                        desks(rset.getString("desks")).fencing(rset.getString("fencing")).sponsor(rset.getString("sponsor")).
                        date_created(rset.getString("date_created")).created_by(rset.getString("created_by"));
                patients.add(infra);
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
    public List<Infra> getPatients() {
        return patients;
    }
}
