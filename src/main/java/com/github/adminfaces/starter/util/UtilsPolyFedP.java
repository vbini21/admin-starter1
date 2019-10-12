package com.github.adminfaces.starter.util;

import com.github.adminfaces.starter.bean.PolysFedPListMB;
import com.github.adminfaces.starter.model.PolyFedP;

import org.apache.log4j.Logger;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;

import static com.github.adminfaces.starter.util.Db.rs;

import java.io.Serializable;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rmpestano on 07/02/17.
 */
@ApplicationScoped

public class UtilsPolyFedP extends Db implements Serializable {

	/**
	 *
	 */
	private static Logger logger = Logger.getLogger(UtilsPolyFedP.class);
	private static final long serialVersionUID = 1L;
	public static List<PolyFedP> patients;
	public static String items_date_toView;

	public static String currentDateYYYYMMDD() {
		String pattern = "yyyyMMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String currentDate = simpleDateFormat.format(new Date());
		return currentDate;
	}

	@PostConstruct
	public void init() {
		// To display date on menu selection
		SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			// To display date on menu selection
			String parsed_selected_date = PolysFedPListMB.dynamicDateFormater(originalFormat, targetFormat,
					items_date_toView);
			items_date_toView = parsed_selected_date;
		} catch (Exception e) {
			// To display date on date selection event
			originalFormat = new SimpleDateFormat("yyyyMMdd");
			targetFormat = new SimpleDateFormat("dd/MM/yyyy");
			String parsed_selected_date = PolysFedPListMB.dynamicDateFormater(originalFormat, targetFormat,
					currentDateYYYYMMDD());
			items_date_toView = parsed_selected_date;
		}

		logger.info("EXCECUTED");
		PolyFedP schoolFedP;
		Db.connect();
		ArrayList<ArrayList<String>> finalArraylist = new ArrayList<>();
		ArrayList<String> innerArraylist = new ArrayList<>();
		String SQL = "SELECT * from tbl_temis_items_props where category ='POLYTECHNIC'";
		logger.info(SQL);
		try {
			ps = Db.conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			while (rs.next()) {
				innerArraylist = new ArrayList<>();
				innerArraylist.add(rs.getString("tid"));// tid
				innerArraylist.add(rs.getString("name"));// name
				innerArraylist.add(rs.getString("unit")); // unit
				innerArraylist.add(""); // qty
				innerArraylist.add(""); // date_created
				innerArraylist.add(""); // created_by
				finalArraylist.add(innerArraylist);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		for (int x = 0; x < finalArraylist.size(); x++) {
			for (int y = 0; y < innerArraylist.size(); y++) {
				try {
					SQL = "SELECT * FROM tbl_temis_items_updated_poly where items_date = '" + currentDateYYYYMMDD()
							+ "' AND item_id=" + Integer.valueOf(finalArraylist.get(x).get(y));
					ps = Db.conn.prepareStatement(SQL);
					rs = ps.executeQuery();
					if (rs.next()) {
						String qty = "";
						try {
							qty = rs.getString("qty");
						} catch (Exception e) {
						}
						String date_created = "";
						try {
							date_created = rs.getString("date_created");
						} catch (Exception e) {
						}
						String created_by = "";
						try {
							created_by = rs.getString("created_by");
						} catch (Exception e) {
						}
						finalArraylist.get(x).set(3, qty);
						finalArraylist.get(x).set(4, date_created);
						finalArraylist.get(x).set(5, created_by);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		patients = new ArrayList<>();
		for (int x = 0; x < finalArraylist.size(); x++) {
			schoolFedP = new PolyFedP(Integer.valueOf(finalArraylist.get(x).get(0)))
					.name(finalArraylist.get(x).get(1)).unit(finalArraylist.get(x).get(2))
					.qty(finalArraylist.get(x).get(3)).date_created(finalArraylist.get(x).get(4))
					.created_by(finalArraylist.get(x).get(5));
			patients.add(schoolFedP);
		}

		Db.disconnect();
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
	public List<PolyFedP> getPatients() {
		return patients;
	}

	public void setPatients(List<PolyFedP> patients) {
		UtilsPolyFedP.patients = patients;
	}

	public String getItems_date_toView() {
		return items_date_toView;
	}

	public void setItems_date_toView(String items_date_toView) {

		UtilsPolyFedP.items_date_toView = items_date_toView;
	}

}
