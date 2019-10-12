/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.Ecde;
import com.github.adminfaces.starter.bean.helpers.Refreshers;
import com.github.adminfaces.starter.bean.helpers.Time;
//import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.util.Db;
import com.github.adminfaces.template.exception.BusinessException;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.net.Inet4Address;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;
import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

/**
 * @author rmpestano Reception Business logic
 */
public class EcdeService implements Serializable {

	@Inject
	List<Ecde> allPatients;

	@Inject
	private LogonMB logonMB;
	protected PreparedStatement ps;
	protected Statement stmt;
	protected ResultSet rs;
	private static Logger logger = Logger.getLogger(EcdeService.class);

	public List<Ecde> listByModel(String names) {
		return allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(names)).collect(Collectors.toList());

	}

	public List<Ecde> paginate(Filter<Ecde> filter) {
		List<Ecde> pagedPatients = new ArrayList<>();
		if (has(filter.getSortOrder()) && !SortOrder.UNSORTED.equals(filter.getSortOrder())) {
			pagedPatients = allPatients.stream().sorted((c1, c2) -> {
				if (filter.getSortOrder().isAscending()) {
					return c1.getOpno().compareTo(c2.getOpno());
				} else {
					return c2.getOpno().compareTo(c1.getOpno());
				}
			}).collect(Collectors.toList());
		}

		int page = filter.getFirst() + filter.getPageSize();
		if (filter.getParams().isEmpty()) {
			pagedPatients = pagedPatients.subList(filter.getFirst(),
					page > allPatients.size() ? allPatients.size() : page);
			return pagedPatients;
		}

		List<Predicate<Ecde>> predicates = configFilter(filter);

		List<Ecde> pagedList = allPatients.stream().filter(predicates.stream().reduce(Predicate::or).orElse(t -> true))
				.collect(Collectors.toList());

		if (page < pagedList.size()) {
			pagedList = pagedList.subList(filter.getFirst(), page);
		}

		if (has(filter.getSortField())) {
			pagedList = pagedList.stream().sorted((c1, c2) -> {
				boolean asc = SortOrder.ASCENDING.equals(filter.getSortOrder());
				if (asc) {
					return c1.getOpno().compareTo(c2.getOpno());
				} else {
					return c2.getOpno().compareTo(c1.getOpno());
				}
			}).collect(Collectors.toList());
		}
		return pagedList;
	}

	private List<Predicate<Ecde>> configFilter(Filter<Ecde> filter) {
		List<Predicate<Ecde>> predicates = new ArrayList<>();
		if (filter.hasParam("id")) {
			Predicate<Ecde> idPredicate = (Ecde c) -> c.getOpno().equals(filter.getParam("id"));
			predicates.add(idPredicate);
		}

		if (has(filter.getEntity())) {
			Ecde filterEntity = filter.getEntity();
			if (has(filterEntity.getOpno())) {
				Predicate<Ecde> opnoPredicate = (Ecde c) -> c.getOpno().equals(filterEntity.getOpno());
				predicates.add(opnoPredicate);
			}

			if (has(filterEntity.getNames())) {
				Predicate<Ecde> namesPredicate = (Ecde c) -> c.getNames().toLowerCase()
						.contains(filterEntity.getNames().toLowerCase());
				predicates.add(namesPredicate);
			}

		}
		return predicates;
	}

	public List<String> getOpno(String query) {
		return allPatients.stream().filter(c -> c.getOpno().toLowerCase().contains(query.toLowerCase()))
				.map(Ecde::getOpno).collect(Collectors.toList());
	}

	public List<String> getNames(String query) {
		return allPatients.stream().filter(c -> c.getNames().toLowerCase().contains(query.toLowerCase()))
				.map(Ecde::getNames).collect(Collectors.toList());
	}

	public void insert(Ecde reception) {
		String msg;
		validate(reception);
		Db.connect();
		try {
			String IP = Inet4Address.getLocalHost().getHostAddress();
			String ecde = reception.getNames();
			int school_id = Refreshers.getSchoolID(reception.getSchool());

			int userid = logonMB.getCurrentUserId();
			logger.info(Time.CurrentTime() + "|" + "Create New Ecde Process" + "|" + "INFO" + "|"
					+ "Processing - Create New Ecde " + "|" + ecde + " |" + userid + "|" + IP + "|"
					+ "jdbc:mysql://localhost/temis" + "|" + "Begin Creating Ecde");

			String sql = "INSERT INTO `tbl_temis_ecde_centres`(names,school_id,date_created,created_by) VALUES (?,?,?,?)";
			ps = Db.conn.prepareStatement(sql);
			ps.setString(1, ecde);// names
			ps.setInt(2, school_id);// names
			ps.setTimestamp(3, Db.getCurrentTimeStamp());// date created
			ps.setInt(4, userid);// created_by

			int i = ps.executeUpdate();
			// logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
			allPatients.add(reception);

			msg = "ECDE: " + reception.getNames() + " created successfully";
			logger.info(Time.CurrentTime() + "|" + "Create New Ecde Process" + "|" + "INFO" + "|"
					+ "Finished - Create New Ecde " + "|" + ecde + " |" + userid + "|" + IP + "|"
					+ "jdbc:mysql://localhost/temis" + "|" + "Finished Creating Ecde");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msg = "ECDE: " + reception.getNames() + " not created, Please Try Again";
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		Db.disconnect();
		addDetailMessage(msg);
	}

	public void validate(Ecde reception) {
		BusinessException be = new BusinessException();
		if (!has(reception.getNames())) {
			be.addException(new BusinessException("Ecde Name Cannot be empty"));
		}
		if (allPatients.stream()
				.filter(c -> c.getNames().equalsIgnoreCase(reception.getNames()) && c.getNames() != c.getNames())
				.count() > 0) {
			be.addException(new BusinessException("Ecde name must be unique"));
		}
		if (has(be.getExceptionList())) {
			throw be;
		}
	}

	public int deletePatient(Ecde reception) {
		Db.connect();
		int ret = 0;
		try {
			String IP = Inet4Address.getLocalHost().getHostAddress();
			String acctypename = reception.getNames();
			int tid = Integer.valueOf(reception.getOpno());
			int userid = logonMB.getCurrentUserId();

			logger.info(Time.CurrentTime() + "|" + "Delete Ecde Process" + "|" + "INFO" + "|"
					+ "Processing - Delete Ecde " + "|" + acctypename + " |" + userid + "|" + IP + "|"
					+ "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting Ecde");

			String sqldelete = "DELETE FROM tbl_temis_ecde_centres WHERE tid= " + tid;

			stmt = Db.conn.createStatement();
			stmt.executeUpdate(sqldelete);

			logger.info(Time.CurrentTime() + "|" + "Delete Ecde Process" + "|" + "INFO" + "|"
					+ "Finished - Delete Ecde " + "|" + acctypename + " |" + userid + "|" + IP + "|"
					+ "jdbc:mysql://localhost/temis" + "|" + "Finished Delete Ecde");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ret = 1;
			e.printStackTrace();
			logger.error(e.getStackTrace());
		}
		Db.disconnect();
		return ret;
	}

	public void remove(Ecde reception) {
		deletePatient(reception);
		allPatients.remove(reception);
	}

	public long count(Filter<Ecde> filter) {
		return allPatients.stream().filter(configFilter(filter).stream().reduce(Predicate::or).orElse(t -> true))
				.count();
	}

	public Ecde findByOpno(String opno) {
		return allPatients.stream().filter(c -> c.getOpno().equals(opno)).findFirst()
				.orElseThrow(() -> new BusinessException("Ecde not found with ID# " + opno));
	}

	public void update(Ecde reception) {
		validate(reception);
		allPatients.remove(allPatients.indexOf(reception));
		allPatients.add(reception);
	}
}
