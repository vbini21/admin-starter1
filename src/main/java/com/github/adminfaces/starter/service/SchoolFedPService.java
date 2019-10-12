/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.School;
import com.github.adminfaces.starter.model.SchoolFedP;
import com.github.adminfaces.starter.util.Db;
import javax.inject.Inject;
import com.github.adminfaces.template.exception.BusinessException;

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

/**
 * @author rmpestano Reception Business logic
 */
public class SchoolFedPService implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
    List<SchoolFedP> allPatients;
    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(SchoolFedPService.class);

    public String CurrentTime() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public List<SchoolFedP> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getName().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<SchoolFedP> paginate(Filter<SchoolFedP> filter) {
        List<SchoolFedP> pagedPatients = new ArrayList<>();
        if (has(filter.getSortOrder()) && !SortOrder.UNSORTED.equals(filter.getSortOrder())) {
            pagedPatients = allPatients.stream().
                    sorted((c1, c2) -> {
                        if (filter.getSortOrder().isAscending()) {
                            return c1.getName().compareTo(c2.getName());
                        } else {
                            return c2.getName().compareTo(c1.getName());
                        }
                    })
                    .collect(Collectors.toList());
        }

        int page = filter.getFirst() + filter.getPageSize();
        if (filter.getParams().isEmpty()) {
            pagedPatients = pagedPatients.subList(filter.getFirst(), page > allPatients.size() ? allPatients.size() : page);
            return pagedPatients;
        }

        List<Predicate<SchoolFedP>> predicates = configFilter(filter);

        List<SchoolFedP> pagedList = allPatients.stream().filter(predicates
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
                            return c1.getName().compareTo(c2.getName());
                        } else {
                            return c2.getName().compareTo(c1.getName());
                        }
                    })
                    .collect(Collectors.toList());
        }
        return pagedList;
    }

    private List<Predicate<SchoolFedP>> configFilter(Filter<SchoolFedP> filter) {
        List<Predicate<SchoolFedP>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<SchoolFedP> idPredicate = (SchoolFedP c) -> c.getName().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            SchoolFedP filterEntity = filter.getEntity();
            if (has(filterEntity.getName())) {
                Predicate<SchoolFedP> opnoPredicate = (SchoolFedP c) -> c.getName().equals(filterEntity.getName());
                predicates.add(opnoPredicate);
            }

        }
        return predicates;
    }

    public List<String> getName(String query) {
        return allPatients.stream().filter(c -> c.getName()
                .toLowerCase().contains(query.toLowerCase()))
                .map(SchoolFedP::getName)
                .collect(Collectors.toList());
    }

  
    public String[] insert(SchoolFedP reception) {
        String[] ret = new String[2];
        Db.connect();
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String itemname = reception.getName();
            int userid = logonMB.getCurrentUserId();
            logger.info(CurrentTime() + "|" + "Create New SchoolFedP Process" + "|" + "INFO" + "|" + "Processing - Create New SchoolFedP "
                    + "|" + itemname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating SchoolFedP");

//            String sql = "INSERT INTO `tbl_temis_items_updated_ecde`(names,ecde,phone,email,date_created,created_by) VALUES (?,?,?,?,?,?)";
//            ps = Db.conn.prepareStatement(sql);
//            ps.setString(1, itemname);//names
//            ps.setString(2, reception.getEcde());//
//            ps.setString(3, reception.getPhone());//phone
//            ps.setString(4, reception.getEmail());//email           
//            ps.setTimestamp(5, Db.getCurrentTimeStamp());
//            ps.setInt(6, userid);//received_by

//            int i = ps.executeUpdate();
            //logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
            allPatients.add(reception);

            logger.info(CurrentTime() + "|" + "Create New SchoolFedP Process" + "|" + "INFO" + "|" + "Finished - Create New SchoolFedP "
                    + "|" + itemname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating SchoolFedP");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return ret;
    }

  

    public int deletePatient(SchoolFedP reception) {
        Db.connect();
        int ret = 0;
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String itemname = reception.getName();
            int tid = reception.getTid();
            int userid = logonMB.getCurrentUserId();

            logger.info(CurrentTime() + "|" + "Delete SchoolFedP Process" + "|" + "INFO" + "|" + "Processing - Delete SchoolFedP "
                    + "|" + itemname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting SchoolFedP");

            String sqldelete = "DELETE FROM tbl_temis_items_updated_ecde WHERE tid= " + tid;
            logger.error("sqldelete" + sqldelete);
            stmt = Db.conn.createStatement();
            stmt.executeUpdate(sqldelete);

            logger.info(CurrentTime() + "|" + "Delete SchoolFedP Process" + "|" + "INFO" + "|" + "Finished - Delete SchoolFedP "
                    + "|" + itemname + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Delete SchoolFedP");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ret = 1;
            e.printStackTrace();
            logger.error(e.getStackTrace());
        }
        Db.disconnect();
        return ret;
    }

    public void remove(SchoolFedP reception) {
        deletePatient(reception);
        allPatients.remove(reception);
    }

    public long count(Filter<SchoolFedP> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public void update(SchoolFedP reception) {
        allPatients.remove(allPatients.indexOf(reception));
        allPatients.add(reception);
    }
    
    public SchoolFedP findByOpno(int tid) {
        return allPatients.stream()
                .filter(c -> c.getTid() == tid)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Item not found # " + tid));
    }
    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getName()
                .toLowerCase().contains(query.toLowerCase()))
                .map(SchoolFedP::getName)
                .collect(Collectors.toList());
    }    
}
