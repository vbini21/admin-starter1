/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.Att;
import com.github.adminfaces.starter.model.User;
import com.github.adminfaces.starter.util.Db;
import com.github.adminfaces.template.exception.BusinessException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.omnifaces.util.Faces;

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
import javax.inject.Named;

/**
 * @author rmpestano Att Business logic
 */
@Named
public class SchAttService implements Serializable {

    @Inject List<Att> allPatients;    

    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(SchAttService.class);
    
	public String CurrentTime() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public List<Att> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getNames().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<Att> paginate(Filter<Att> filter) {
        List<Att> pagedPatients = new ArrayList<>();
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

        List<Predicate<Att>> predicates = configFilter(filter);

        List<Att> pagedList = allPatients.stream().filter(predicates
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

    private List<Predicate<Att>> configFilter(Filter<Att> filter) {
        List<Predicate<Att>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<Att> idPredicate = (Att c) -> c.getOpno().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            Att filterEntity = filter.getEntity();
            if (has(filterEntity.getOpno())) {
                Predicate<Att> opnoPredicate = (Att c) -> c.getOpno().equals(filterEntity.getOpno());
                predicates.add(opnoPredicate);
            }

            if (has(filterEntity.getNames())) {
                Predicate<Att> namesPredicate = (Att c) -> c.getNames().toLowerCase().contains(filterEntity.getNames().toLowerCase());
                predicates.add(namesPredicate);
            }

        }
        return predicates;
    }

    public List<String> getOpno(String query) {
        return allPatients.stream().filter(c -> c.getOpno()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Att::getOpno)
                .collect(Collectors.toList());
    }

    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getNames()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Att::getNames)
                .collect(Collectors.toList());
    }

    public void insert(Att reception) {
        Db.connect();
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String Teacher = reception.getNames();
            int userid = logonMB.getCurrentUserId();
            
            logger.info(CurrentTime() + "|" + "Create New School Attendance Process" + "|" + "INFO" + "|" + "Processing - Create New School Attendance "
                    + "|" + Teacher + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating School Attendance");
            
            String sql = "INSERT INTO `tbl_temis_schatt`(name,selectedDate,status,date_created,created_by) VALUES (?,?,?,?,?)";
            ps = Db.conn.prepareStatement(sql);
            ps.setString(1, Teacher);//names
            ps.setString(2, reception.getSelectedDate());//names
            ps.setString(3, reception.getStatus());//names
            ps.setTimestamp(4, Db.getCurrentTimeStamp());
            ps.setInt(5, userid);//received_by
            int i = ps.executeUpdate();
            //logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
            allPatients.add(reception);

            logger.info(CurrentTime() + "|" + "Create New School Attendance Process" + "|" + "INFO" + "|" + "Finished - Create New School Attendance "
                    + "|" + Teacher + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating School Attendance");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
    }

    public long count(Filter<Att> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public void update(Att reception) {
        allPatients.remove(allPatients.indexOf(reception));
        allPatients.add(reception);
    }
    
    public Att findByOpno(String opno) {
        return allPatients.stream()
                .filter(c -> c.getOpno().equals(opno))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Teacher not found with ID# " + opno));
    }
}
