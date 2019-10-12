/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.School;
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
public class SchoolService implements Serializable {

    @Inject
    List<School> allPatients;
    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(SchoolService.class);

    public String CurrentTime() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public List<School> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getNames().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<School> paginate(Filter<School> filter) {
        List<School> pagedPatients = new ArrayList<>();
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

        List<Predicate<School>> predicates = configFilter(filter);

        List<School> pagedList = allPatients.stream().filter(predicates
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

    private List<Predicate<School>> configFilter(Filter<School> filter) {
        List<Predicate<School>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<School> idPredicate = (School c) -> c.getOpno().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            School filterEntity = filter.getEntity();
            if (has(filterEntity.getOpno())) {
                Predicate<School> opnoPredicate = (School c) -> c.getOpno().equals(filterEntity.getOpno());
                predicates.add(opnoPredicate);
            }

            if (has(filterEntity.getNames())) {
                Predicate<School> namesPredicate = (School c) -> c.getNames().toLowerCase().contains(filterEntity.getNames().toLowerCase());
                predicates.add(namesPredicate);
            }

        }
        return predicates;
    }

    public List<String> getOpno(String query) {
        return allPatients.stream().filter(c -> c.getOpno()
                .toLowerCase().contains(query.toLowerCase()))
                .map(School::getOpno)
                .collect(Collectors.toList());
    }

    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getNames()
                .toLowerCase().contains(query.toLowerCase()))
                .map(School::getNames)
                .collect(Collectors.toList());
    }

    public List<String> getPhones(String query) {
        return allPatients.stream().filter(c -> c.getPhone()
                .toLowerCase().contains(query.toLowerCase()))
                .map(School::getPhone)
                .collect(Collectors.toList());
    }

    public void insert(School reception) {
        String msg;
        validate(reception);
        Db.connect();
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String schoolname = reception.getNames();
            int userid = logonMB.getCurrentUserId();
            logger.info(CurrentTime() + "|" + "Create New School Process" + "|" + "INFO" + "|" + "Processing - Create New School "
                    + "|" + schoolname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating School");

            String sql = "INSERT INTO `tbl_temis_schools`(names,phone,email,date_created,created_by) VALUES (?,?,?,?,?)";
            ps = Db.conn.prepareStatement(sql);
            ps.setString(1, schoolname);//names
            ps.setString(2, reception.getPhone());//phone
            ps.setString(3, reception.getEmail());//email           
            ps.setTimestamp(4, Db.getCurrentTimeStamp());
            ps.setInt(5, userid);//received_by

            int i = ps.executeUpdate();
            //logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
            allPatients.add(reception);
            msg = "School: " + reception.getNames() + " created successfully";
            logger.info(CurrentTime() + "|" + "Create New School Process" + "|" + "INFO" + "|" + "Finished - Create New School "
                    + "|" + schoolname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating School");
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	msg = "School: " + reception.getNames() + " not created, Please Try Again";
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        addDetailMessage(msg);
    }

    public void validate(School reception) {
        BusinessException be = new BusinessException();
        if (!has(reception.getNames())) {
            be.addException(new BusinessException("School Name Cannot be empty"));
        }
        if (allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(reception.getNames())
                && c.getNames() != c.getNames()).count() > 0) {
            be.addException(new BusinessException("School name must be unique"));
        }
        if (has(be.getExceptionList())) {
            throw be;
        }
    }

    public int deletePatient(School reception) {
        Db.connect();
        int ret = 0;
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String acctypename = reception.getNames();
            int tid = Integer.valueOf(reception.getOpno());
            int userid = logonMB.getCurrentUserId();

            logger.info(CurrentTime() + "|" + "Delete School Process" + "|" + "INFO" + "|" + "Processing - Delete School "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting School");

            String sqldelete = "DELETE FROM tbl_temis_schools WHERE tid= " + tid;
            logger.error("sqldelete" + sqldelete);
            stmt = Db.conn.createStatement();
            stmt.executeUpdate(sqldelete);

            logger.info(CurrentTime() + "|" + "Delete School Process" + "|" + "INFO" + "|" + "Finished - Delete School "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Delete School");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ret = 1;
            e.printStackTrace();
            logger.error(e.getStackTrace());
        }
        Db.disconnect();
        return ret;
    }

    public void remove(School reception) {
        deletePatient(reception);
        allPatients.remove(reception);
    }

    public long count(Filter<School> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public School findByOpno(String opno) {
        return allPatients.stream()
                .filter(c -> c.getOpno().equals(opno))
                .findFirst()
                .orElseThrow(() -> new BusinessException("School not found with ID# " + opno));
    }

    public void update(School reception) {
        validate(reception);
        allPatients.remove(allPatients.indexOf(reception));
        allPatients.add(reception);
    }
}
