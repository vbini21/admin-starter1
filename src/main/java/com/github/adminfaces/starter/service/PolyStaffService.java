/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.bean.helpers.Refreshers;
import com.github.adminfaces.starter.bean.helpers.Time;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.PolyStaff;
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
public class PolyStaffService implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
    List<PolyStaff> allPatients;
    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(PolyStaffService.class);

  
    public List<PolyStaff> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getNames().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<PolyStaff> paginate(Filter<PolyStaff> filter) {
        List<PolyStaff> pagedPatients = new ArrayList<>();
        if (has(filter.getSortOrder()) && !SortOrder.UNSORTED.equals(filter.getSortOrder())) {
            pagedPatients = allPatients.stream().
                    sorted((c1, c2) -> {
                        if (filter.getSortOrder().isAscending()) {
                            return c1.getOpno().compareTo(c2.getOpno());
                        } else {
                            return c2.getOpno().compareTo(c1.getOpno());
                        }
                    })
                    .collect(Collectors.toList());
        }

        int page = filter.getFirst() + filter.getPageSize();
        if (filter.getParams().isEmpty()) {
            pagedPatients = pagedPatients.subList(filter.getFirst(), page > allPatients.size() ? allPatients.size() : page);
            return pagedPatients;
        }

        List<Predicate<PolyStaff>> predicates = configFilter(filter);

        List<PolyStaff> pagedList = allPatients.stream().filter(predicates
                .stream().reduce(Predicate::or).orElse(t -> true))
                .collect(Collectors.toList());

        if (page < pagedList.size()) {
            pagedList = pagedList.subList(filter.getFirst(), page);
        }

        if (has(filter.getSortField())) {
            pagedList = pagedList.stream().
                    sorted((c1, c2) -> {
                        boolean asc = SortOrder.ASCENDING.equals(filter.getSortOrder());
                        if (asc) {
                            return c1.getOpno().compareTo(c2.getOpno());
                        } else {
                            return c2.getOpno().compareTo(c1.getOpno());
                        }
                    })
                    .collect(Collectors.toList());
        }
        return pagedList;
    }

    private List<Predicate<PolyStaff>> configFilter(Filter<PolyStaff> filter) {
        List<Predicate<PolyStaff>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<PolyStaff> idPredicate = (PolyStaff c) -> c.getOpno().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            PolyStaff filterEntity = filter.getEntity();
            if (has(filterEntity.getOpno())) {
                Predicate<PolyStaff> opnoPredicate = (PolyStaff c) -> c.getOpno().equals(filterEntity.getOpno());
                predicates.add(opnoPredicate);
            }

            if (has(filterEntity.getNames())) {
                Predicate<PolyStaff> namesPredicate = (PolyStaff c) -> c.getNames().toLowerCase().contains(filterEntity.getNames().toLowerCase());
                predicates.add(namesPredicate);
            }
            
            if (has(filterEntity.getPolytechnic())) {
                Predicate<PolyStaff> polytechnicPredicate = (PolyStaff c) -> c.getPolytechnic().toLowerCase().contains(filterEntity.getPolytechnic().toLowerCase());
                predicates.add(polytechnicPredicate);
            }

        }
        return predicates;
    }

    public List<String> getOpno(String query) {
        return allPatients.stream().filter(c -> c.getOpno()
                .toLowerCase().contains(query.toLowerCase()))
                .map(PolyStaff::getOpno)
                .collect(Collectors.toList());
    }

    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getNames()
                .toLowerCase().contains(query.toLowerCase()))
                .map(PolyStaff::getNames)
                .collect(Collectors.toList());
    }

    public List<String> getPolytechnic(String query) {
        return allPatients.stream().filter(c -> c.getPolytechnic()
                .toLowerCase().contains(query.toLowerCase()))
                .map(PolyStaff::getPolytechnic)
                .collect(Collectors.toList());
    }

    public void insert(PolyStaff polyStaff) {
       String msg;
        validate(polyStaff);
        Db.connect();
        int PolytechnicID = Refreshers.getPolytechnicID(polyStaff.getPolytechnic());
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String polyStaffname = polyStaff.getNames();
            int userid = logonMB.getCurrentUserId();
            logger.info(Time.CurrentTime() + "|" + "Create New PolyStaff Process" + "|" + "INFO" + "|" + "Processing - Create New PolyStaff "
                    + "|" + polyStaffname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating PolyStaff");
            
            String sql = "INSERT INTO `tbl_temis_polystaff`(names,polytechnic_id,phone,email,tradeArea,qualification,terms_of_emp,job_grp,date_created,created_by)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?)";
            ps = Db.conn.prepareStatement(sql);
 
            ps.setString(1, polyStaffname);//names
            ps.setInt(2, PolytechnicID);//phone
            ps.setString(3, polyStaff.getPhone());//phone
            ps.setString(4, polyStaff.getEmail());//email        
            ps.setString(5, polyStaff.getTradeArea());//school
            ps.setString(6, polyStaff.getQualification());//
            ps.setString(7, polyStaff.getTerms_of_emp());//
            ps.setString(8, polyStaff.getJob_grp());//   
            ps.setTimestamp(9, Db.getCurrentTimeStamp());
            ps.setInt(10, userid);//received_by

            int i = ps.executeUpdate();
            //logger.error("polyStaff.getNames()"+polyStaff.getNames()+"polyStaff.getNames()");
            allPatients.add(polyStaff);
            msg = "Polytechnic Staff: " + polyStaff.getNames() + " created successfully";
            logger.info(Time.CurrentTime() + "|" + "Create New PolyStaff Process" + "|" + "INFO" + "|" + "Finished - Create New PolyStaff "
                    + "|" + polyStaffname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating PolyStaff");
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	msg = "Polytechnic Staff: " + polyStaff.getNames() + " not created, Please Try Again";
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        addDetailMessage(msg);
    }

    public void validate(PolyStaff polyStaff) {
        BusinessException be = new BusinessException();
        if (!has(polyStaff.getNames())) {
            be.addException(new BusinessException("PolyStaff Name Cannot be empty"));
        }
        if (allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(polyStaff.getNames())
                && c.getNames() != c.getNames()).count() > 0) {
            be.addException(new BusinessException("PolyStaff name must be unique"));
        }
        if (has(be.getExceptionList())) {
            throw be;
        }
    }

    public int deletePatient(PolyStaff polyStaff) {
        Db.connect();
        int ret = 0;
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String acctypename = polyStaff.getNames();
            int tid = Integer.valueOf(polyStaff.getOpno());
            int userid = logonMB.getCurrentUserId();

            logger.info(Time.CurrentTime() + "|" + "Delete PolyStaff Process" + "|" + "INFO" + "|" + "Processing - Delete PolyStaff "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting PolyStaff");

            String sqldelete = "DELETE FROM tbl_temis_polystaff WHERE tid= " + tid;
            logger.error("sqldelete" + sqldelete);
            stmt = Db.conn.createStatement();
            stmt.executeUpdate(sqldelete);

            logger.info(Time.CurrentTime() + "|" + "Delete PolyStaff Process" + "|" + "INFO" + "|" + "Finished - Delete PolyStaff "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Delete PolyStaff");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ret = 1;
            e.printStackTrace();
            logger.error(e.getStackTrace());
        }
        Db.disconnect();
        return ret;
    }

    public void remove(PolyStaff polyStaff) {
        deletePatient(polyStaff);
        allPatients.remove(polyStaff);
    }

    public long count(Filter<PolyStaff> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public PolyStaff findByOpno(String opno) {
        return allPatients.stream()
                .filter(c -> c.getOpno().equals(opno))
                .findFirst()
                .orElseThrow(() -> new BusinessException("PolyStaff not found with ID# " + opno));
    }

    public void update(PolyStaff polyStaff) {
        validate(polyStaff);
        allPatients.remove(allPatients.indexOf(polyStaff));
        allPatients.add(polyStaff);
    }
}
