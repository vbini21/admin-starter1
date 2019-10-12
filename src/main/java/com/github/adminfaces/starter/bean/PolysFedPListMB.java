package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.PolyFedP;
import com.github.adminfaces.starter.service.PolyFedPService;
import com.github.adminfaces.starter.util.Db;
import com.github.adminfaces.starter.util.UtilsPolyFedP;
import com.github.adminfaces.starter.bean.helpers.Refreshers;
import com.github.adminfaces.starter.bean.helpers.Time;
import static com.github.adminfaces.starter.util.Db.ps;
import static com.github.adminfaces.starter.util.Db.rs;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.net.Inet4Address;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class PolysFedPListMB implements Serializable {
	private static Logger logger = Logger.getLogger(PolysFedPListMB.class);
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	PolyFedPService polyFedPService;
	@Inject
	LogonMB logonMB;
	String name;

	LazyDataModel<PolyFedP> patients;

	Filter<PolyFedP> filter = new Filter<>(new PolyFedP());

	List<PolyFedP> selectedPatients; // patients selected in checkbox column

	List<PolyFedP> filteredValue;// datatable filteredValue attribute (column filters)
	private static String savedTempDateFromView;
	private static String selectedDateYYYYMMDD;

	@PostConstruct
	public void initDataModel() {
		patients = new LazyDataModel<PolyFedP>() {
			@Override
			public List<PolyFedP> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				com.github.adminfaces.starter.infra.model.SortOrder order = null;
				if (sortOrder != null) {
					order = sortOrder.equals(SortOrder.ASCENDING)
							? com.github.adminfaces.starter.infra.model.SortOrder.ASCENDING
							: sortOrder.equals(SortOrder.DESCENDING)
									? com.github.adminfaces.starter.infra.model.SortOrder.DESCENDING
									: com.github.adminfaces.starter.infra.model.SortOrder.UNSORTED;
				}
				filter.setFirst(first).setPageSize(pageSize).setSortField(sortField).setSortOrder(order)
						.setParams(filters);
				List<PolyFedP> list = polyFedPService.paginate(filter);
				setRowCount((int) polyFedPService.count(filter));
				return list;
			}

			@Override
			public int getRowCount() {
				return super.getRowCount();
			}

			@Override
			public PolyFedP getRowData(String key) {
				return polyFedPService.findByOpno(Integer.valueOf(key));
			}
		};
	}

	public void clear() {
		filter = new Filter<PolyFedP>(new PolyFedP());
	}

	public List<String> completeModel(String query) {
		List<String> result = polyFedPService.getNames(query);
		return result;
	}

	public List<String> completeModelEcde(String query) {
		Db.connect();
		List<String> data = new ArrayList<>();
		try {
			ps = Db.conn
					.prepareStatement("SELECT names FROM tbl_temis_polytechnic where names like '%" + query + "%'");
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

	public void delete() {
		int numPatients = 0;
		for (PolyFedP selectedCar : selectedPatients) {
			numPatients++;
			polyFedPService.remove(selectedCar);
		}
		selectedPatients.clear();
		addDetailMessage(numPatients + " PolyFedP deleted successfully!");
	}

	public List<PolyFedP> getFilteredValue() {
		return filteredValue;
	}

	public void setFilteredValue(List<PolyFedP> filteredValue) {
		this.filteredValue = filteredValue;
	}

	public LazyDataModel<PolyFedP> getPatients() {
		return patients;
	}

	public void setPatients(LazyDataModel<PolyFedP> patients) {
		this.patients = patients;
	}

	public Filter<PolyFedP> getFilter() {
		return filter;
	}

	public void setFilter(Filter<PolyFedP> filter) {
		this.filter = filter;
	}

	public List<PolyFedP> getSelectedPatients() {
		return selectedPatients;
	}

	public void setSelectedPatients(List<PolyFedP> selectedPatients) {
		this.selectedPatients = selectedPatients;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void saveQuantityFeedP(int rowkey, double oldqty, double newQuantity, String categoryID) {
		String msg = "";
		try {
			Db.connect();
			int userid = logonMB.getCurrentUserId();
			String itemname = Refreshers.getItemName(rowkey);
			String itemunit = Refreshers.getItemUnit(rowkey);
			SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat targetFormat = new SimpleDateFormat("yyyyMMdd");
			String selected_date = currentDateYYYYMMDD();
			if (UtilsPolyFedP.items_date_toView != null) {
				selected_date = dynamicDateFormater(originalFormat, targetFormat, UtilsPolyFedP.items_date_toView);
			}
			String SQL = "SELECT * from tbl_temis_items_updated_poly where items_date = '" + selected_date
					+ "' AND item_id=" + rowkey;

			String IP = Inet4Address.getLocalHost().getHostAddress();
			ps = Db.conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			if (rs.next()) {
				// update item quantity
				logger.info(Time.CurrentTime() + "|" + "Update Existing Polytechnic Feeding Item quantity Process" + "|"
						+ "INFO" + "|" + "Processing - Update Existing Polytechnic Feeding Item quantity " + "|" + itemname
						+ " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|"
						+ "Begin Updating Existing Polytechnic Feeding Item quantity");
				String sqlUpdate = "UPDATE tbl_temis_items_updated_poly " + "SET qty = ? "
						+ "WHERE item_id = ? and  items_date = ?";

				Db.ps = Db.conn.prepareStatement(sqlUpdate);
				ps.setDouble(1, newQuantity);
				ps.setInt(2, rowkey);
				ps.setString(3, selected_date);
				int i = ps.executeUpdate();

				logger.info(Time.CurrentTime() + "|" + "Update Existing Polytechnic Feeding Item quantity Process" + "|"
						+ "INFO" + "|" + "Processing - Update Existing Polytechnic Feeding Item quantity " + "|" + itemname
						+ " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|"
						+ "Finished Update Existing Polytechnic Feeding Item quantity");
				msg = itemname + " Edited Successfully. \nOld Qty:" + oldqty + ",\nNew Qty:" + newQuantity;
			} else {
				// record new
				logger.info(Time.CurrentTime() + "|" + "Create New Polytechnic Feeding Item quantity Process" + "|" + "INFO"
						+ "|" + "Processing - Create New Polytechnic Feeding Item quantity Process" + "|" + itemname + " |"
						+ userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|"
						+ "Begin Creating Polytechnic Feeding Item quantity Process");

				String sql = "INSERT INTO `tbl_temis_items_updated_poly`(item_id,unit,qty,items_date,date_created,created_by) VALUES (?,?,?,?,?,?)";
				ps = Db.conn.prepareStatement(sql);
				ps.setInt(1, rowkey);// item_id
				ps.setString(2, itemunit);//
				ps.setString(3, String.valueOf(newQuantity));//
				ps.setString(4, selected_date);//
				ps.setTimestamp(5, Db.getCurrentTimeStamp());
				ps.setInt(6, userid);// received_by
				int i = ps.executeUpdate();

				logger.info(Time.CurrentTime() + "|" + "Create New Polytechnic Feeding Item quantity Process" + "|" + "INFO"
						+ "|" + "Finished - Create New Polytechnic Feeding Item quantity Process " + "|" + itemname + " |"
						+ userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|"
						+ "Finished Creating Polytechnic Feeding Item quantity Process");
				msg = itemname + " :New Qty:" + newQuantity + " Saved";
			}
			Db.disconnect();
		} catch (Exception e) {
			logger.error(e.toString());
		}if(!msg.isEmpty()) {
			addDetailMessage(msg);
		}
	}

	public void resfreshlist(String date) {
		logger.info("REFRESH");
		PolyFedP polyFedP;
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
					SQL = "SELECT * FROM tbl_temis_items_updated_poly where items_date = '" + date + "' AND item_id="
							+ Integer.valueOf(finalArraylist.get(x).get(y));
					logger.info(SQL);
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

		UtilsPolyFedP.patients = new ArrayList<>();
		for (int x = 0; x < finalArraylist.size(); x++) {
			polyFedP = new PolyFedP(Integer.valueOf(finalArraylist.get(x).get(0)))
					.name(finalArraylist.get(x).get(1)).unit(finalArraylist.get(x).get(2))
					.qty(finalArraylist.get(x).get(3)).date_created(finalArraylist.get(x).get(4))
					.created_by(finalArraylist.get(x).get(5));
			UtilsPolyFedP.patients.add(polyFedP);
		}

		Db.disconnect();
	}

	public static String currentDateYYYYMMDD() {
		String pattern = "yyyyMMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String currentDate = simpleDateFormat.format(new Date());
		return currentDate;
	}

	public static String dynamicDateFormater(SimpleDateFormat originalFormat, SimpleDateFormat targetFormat,
			String dateToParse) {
		Date date;
		try {
			date = originalFormat.parse(dateToParse);
			String ret = targetFormat.format(date);
			return ret;
		} catch (ParseException ex) {
			// Handle Exception.
			logger.error(ex.getMessage());
			return null;
		}
	}

	public void onCellEdit(CellEditEvent event) {
		String msg = "";
		Object oldQuantity = event.getOldValue();
		Object newQuantity = event.getNewValue();
		String rowkey = event.getRowKey();
		if (newQuantity != null && !newQuantity.equals(oldQuantity)) {
			try {
				String categoryID = logonMB.getCategoryID();
				if (categoryID.contentEquals("3")) {
					double oldqty = 0;
					try {
						oldqty = Double.valueOf(oldQuantity.toString());
					} catch (Exception e) {
						// do nothing
					}
					double newqty = Double.valueOf(newQuantity.toString());
					int item_id = Integer.valueOf(rowkey);
					saveQuantityFeedP(item_id, oldqty, newqty, categoryID);
				} else {
					msg = "Cannot add Item Quantity, Please Request Authorised Polytechnic Teacher to add the Item Quantity";
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
				msg = "No quantity or Invalid Quantity Entered";
			}
		}
		if(!msg.isEmpty()) {
			addDetailMessage(msg);
		}
		
	}

	public void onDateSelect(SelectEvent event) {
		try {
			SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
			String date = targetFormat.format(event.getObject());
			String parsed_selected_date = dynamicDateFormater(originalFormat, targetFormat, date);
			UtilsPolyFedP.items_date_toView = parsed_selected_date;
			savedTempDateFromView = parsed_selected_date;
			
			targetFormat = new SimpleDateFormat("yyyyMMdd");
			selectedDateYYYYMMDD = dynamicDateFormater(originalFormat, targetFormat, date);
			resfreshlist(selectedDateYYYYMMDD);
			Refreshers.refreshPage();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public String getItems_date_fromView() {
		return UtilsPolyFedP.items_date_toView;
	}

	public void setItems_date_fromView(String items_date_fromView) {
		UtilsPolyFedP.items_date_toView = savedTempDateFromView;
	}
}
