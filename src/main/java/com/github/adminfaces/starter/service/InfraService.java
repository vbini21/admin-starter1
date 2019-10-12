/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.Infra;
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
public class InfraService implements Serializable {

	@Inject
	List<Infra> allPatients;

	@Inject
	private LogonMB logonMB;
	protected PreparedStatement ps;
	protected Statement stmt;
	protected ResultSet rs;
	private static Logger logger = Logger.getLogger(InfraService.class);

	public String CurrentTime() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}

	public List<Infra> listByModel(String names) {
		return allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(names)).collect(Collectors.toList());

	}

	public List<Infra> paginate(Filter<Infra> filter) {
		List<Infra> pagedPatients = new ArrayList<>();
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

		List<Predicate<Infra>> predicates = configFilter(filter);

		List<Infra> pagedList = allPatients.stream().filter(predicates.stream().reduce(Predicate::or).orElse(t -> true))
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

	private List<Predicate<Infra>> configFilter(Filter<Infra> filter) {
		List<Predicate<Infra>> predicates = new ArrayList<>();
		if (filter.hasParam("id")) {
			Predicate<Infra> idPredicate = (Infra c) -> c.getOpno().equals(filter.getParam("id"));
			predicates.add(idPredicate);
		}

		if (has(filter.getEntity())) {
			Infra filterEntity = filter.getEntity();
			if (has(filterEntity.getOpno())) {
				Predicate<Infra> opnoPredicate = (Infra c) -> c.getOpno().equals(filterEntity.getOpno());
				predicates.add(opnoPredicate);
			}

			if (has(filterEntity.getNames())) {
				Predicate<Infra> namesPredicate = (Infra c) -> c.getNames().toLowerCase()
						.contains(filterEntity.getNames().toLowerCase());
				predicates.add(namesPredicate);
			}

		}
		return predicates;
	}

	public List<String> getOpno(String query) {
		return allPatients.stream().filter(c -> c.getOpno().toLowerCase().contains(query.toLowerCase()))
				.map(Infra::getOpno).collect(Collectors.toList());
	}

	public List<String> getNames(String query) {
		return allPatients.stream().filter(c -> c.getNames().toLowerCase().contains(query.toLowerCase()))
				.map(Infra::getNames).collect(Collectors.toList());
	}

	public List<String> getSponsors(String query) {
		return allPatients.stream().filter(c -> c.getSponsor().toLowerCase().contains(query.toLowerCase()))
				.map(Infra::getSponsor).collect(Collectors.toList());
	}

	public void insert(Infra reception) {
		String msg;
		validate(reception);
		String ecde = reception.getNames();
		Db.connect();
		try {
			String IP = Inet4Address.getLocalHost().getHostAddress();
			int userid = logonMB.getCurrentUserId();
			logger.info(CurrentTime() + "|" + "Create New Infra Account Type Process" + "|" + "INFO" + "|"
					+ "Processing - Create New Infra Account Type " + "|" + reception.getNames() + " |" + userid + "|"
					+ IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating Infra Account Type");

			String sql = "INSERT INTO `tbl_temis_infra`(ecde,class_perm,class_temp,class_req,toilets,toilets_req,desks,fencing,sponsor,date_created,created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
			ps = Db.conn.prepareStatement(sql);
			ps.setString(1, ecde);// names
			ps.setString(2, reception.getClass_perm());// permaanent
			ps.setString(3, reception.getClass_temp());// temporary
			ps.setString(4, reception.getClass_req());// required
			ps.setString(5, reception.getToilets());// toilets
			ps.setString(6, reception.getToilets_req());// permaanent
			ps.setString(7, reception.getDesks());// temporary
			ps.setString(8, reception.getFencing());// required
			ps.setString(9, reception.getSponsor());// toilets
			ps.setTimestamp(10, Db.getCurrentTimeStamp());
			ps.setInt(11, userid);// received_by

			int i = ps.executeUpdate();
			// logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
			allPatients.add(reception);
			msg = "Infra Account Type: " + reception.getNames() + " created successfully";
			logger.info(CurrentTime() + "|" + "Create New Infra Account Type Process" + "|" + "INFO" + "|"
					+ "Finished - Create New Infra Account Type " + "|" + ecde + " |" + userid + "|" + IP + "|"
					+ "jdbc:mysql://localhost/temis" + "|" + "Finished Creating Infra Account Type");
		} catch (Exception e) {
			// TODO Auto-generated catch block

			msg = "Infra Account Type: " + reception.getNames() + " not created, Please Try Again";
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		Db.disconnect();
		addDetailMessage(msg);
	}

	public void validate(Infra reception) {
		BusinessException be = new BusinessException();
		if (!has(reception.getNames())) {
			be.addException(new BusinessException("Infra Name Cannot be empty"));
		}
		if (allPatients.stream()
				.filter(c -> c.getNames().equalsIgnoreCase(reception.getNames()) && c.getNames() != c.getNames())
				.count() > 0) {
			be.addException(new BusinessException("Infra name must be unique"));
		}
		if (has(be.getExceptionList())) {
			throw be;
		}
	}

	public int deletePatient(Infra reception) {
		Db.connect();
		int ret = 0;
		try {
			String IP = Inet4Address.getLocalHost().getHostAddress();
			String acctypename = reception.getNames();
			int tid = Integer.valueOf(reception.getOpno());
			int userid = logonMB.getCurrentUserId();

			logger.info(CurrentTime() + "|" + "Delete Infra Account Type Process" + "|" + "INFO" + "|"
					+ "Processing - Delete Infra Account Type " + "|" + acctypename + " |" + userid + "|" + IP + "|"
					+ "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting Infra Account Type");

			String sqldelete = "DELETE FROM tbl_temis_infra WHERE tid= " + tid;
			logger.error("sqldelete" + sqldelete);
			stmt = Db.conn.createStatement();
			stmt.executeUpdate(sqldelete);

			logger.info(CurrentTime() + "|" + "Delete Infra Account Type Process" + "|" + "INFO" + "|"
					+ "Finished - Delete Infra Account Type " + "|" + acctypename + " |" + userid + "|" + IP + "|"
					+ "jdbc:mysql://localhost/temis" + "|" + "Finished Delete Infra Account Type");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ret = 1;
			e.printStackTrace();
			logger.error(e.getStackTrace());
		}
		Db.disconnect();
		return ret;
	}

	public void remove(Infra reception) {
		deletePatient(reception);
		allPatients.remove(reception);
	}

	public long count(Filter<Infra> filter) {
		return allPatients.stream().filter(configFilter(filter).stream().reduce(Predicate::or).orElse(t -> true))
				.count();
	}

	public Infra findByOpno(String opno) {
		return allPatients.stream().filter(c -> c.getOpno().equals(opno)).findFirst()
				.orElseThrow(() -> new BusinessException("Infra not found with ID# " + opno));
	}

	public void update(Infra reception) {
		validate(reception);
		allPatients.remove(allPatients.indexOf(reception));
		allPatients.add(reception);
	}
}
