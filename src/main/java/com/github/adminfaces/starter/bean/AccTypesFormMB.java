/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Reception;
import com.github.adminfaces.starter.service.ReceptionService;
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

/**
 * @author ben
 */
@Named
@ViewScoped
public class AccTypesFormMB implements Serializable {

    /**
	 * 
	 */
	private static Logger logger = Logger.getLogger(AccTypesFormMB.class);
	private static final long serialVersionUID = 1L;
	private String opno;
    private Reception reception;
    protected PreparedStatement ps;
	protected ResultSet rs;
    @Inject
    ReceptionService receptionService;

    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (has(opno)) {
            reception = receptionService.findByOpno(opno);
        } else {
            opno = String.valueOf(getNextPatientCode());
            reception = new Reception(opno);
        }
    }
    
    public int getNextPatientCode() {
        int patientcode = 0;
        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT tid FROM tbl_temis_account_types ORDER BY tid DESC LIMIT 1");
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

    public Reception getReception() {
        return reception;
    }

    public void setCar(Reception reception) {
        this.reception = reception;
    }


    public void remove() throws IOException {
        if (has(reception) && has(reception.getOpno())) {
            receptionService.remove(reception);
            addDetailMessage("Patient: " + reception.getNames()
                    + " removed successfully");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("acctypes-list.jsf");
        }
    }

    public boolean isExistingPatientCode(int pcode) {		
		boolean existing = false;
    	Db.connect();
		try {
			 ps= Db.conn.prepareStatement("SELECT count(tid) as row_count FROM tbl_temis_account_types WHERE tid ="+pcode);
        	 rs = ps.executeQuery();        
        	 while(rs.next()) {
        		  if(rs.getInt("row_count") > 0) {
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
        String msg;        
        if (!isExistingPatientCode(Integer.valueOf(reception.getOpno()))) {
            receptionService.insert(reception);
        } else {
            receptionService.update(reception);
        }
    }

    public void clear() {
        reception = new Reception();
        opno = null;
    }

    public boolean isNew() {
        return reception == null || reception.getOpno() == null;
    }

}
