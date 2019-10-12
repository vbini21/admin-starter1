package com.github.adminfaces.starter.bean.helpers;

import static com.github.adminfaces.starter.util.Db.ps;
import static com.github.adminfaces.starter.util.Db.rs;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import com.github.adminfaces.starter.util.Db;

public class Refreshers {
	private static Logger logger = Logger.getLogger(Refreshers.class);
	public static void refreshPage() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
	public static String getItemName(int rowkey) {
		String SQL = "SELECT name from tbl_temis_items_props where tid = " + rowkey;		
		String itemname = "";
		try {
			ps = Db.conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			if (rs.next()) {	            
				itemname = rs.getString("name");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return itemname;
	}
	public static String getItemUnit(int rowkey) {
		String SQL = "SELECT unit from tbl_temis_items_props where tid = " + rowkey;		
		String itemunit = "";
		try {
			ps = Db.conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			if (rs.next()) {	            
				itemunit = rs.getString("unit");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return itemunit;
	}
	public static int getSchoolID(String schoolname) {
		String SQL = "SELECT tid from tbl_temis_schools where names = '" + schoolname+"'";		
		int tid = 0;
		try {
			ps = Db.conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			if (rs.next()) {	            
				tid = rs.getInt("tid");
			}
			logger.info(ps.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return tid;
	}
	
	public static int getEcdeID(String ecdename) {
		String SQL = "SELECT tid from tbl_temis_ecde_centres where names = '" + ecdename+"'";		
		int tid = 0;
		try {
			ps = Db.conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			if (rs.next()) {	            
				tid = rs.getInt("tid");
			}
			logger.info(ps.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return tid;
	}
	public static int getPolytechnicID(String polytechnicname) {
		String SQL = "SELECT tid from tbl_temis_polytechnic where names = '" + polytechnicname+"'";		
		int tid = 0;
		try {
			ps = Db.conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			if (rs.next()) {	            
				tid = rs.getInt("tid");
			}
			logger.info(ps.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return tid;
	}
	
}
