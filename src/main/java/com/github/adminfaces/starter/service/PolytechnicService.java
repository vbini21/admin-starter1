/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.Polytechnic;
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
public class PolytechnicService implements Serializable {

    @Inject
    List<Polytechnic> allPatients;
    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(PolytechnicService.class);

    public String CurrentTime() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public List<Polytechnic> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getNames().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<Polytechnic> paginate(Filter<Polytechnic> filter) {
        List<Polytechnic> pagedPatients = new ArrayList<>();
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

        List<Predicate<Polytechnic>> predicates = configFilter(filter);

        List<Polytechnic> pagedList = allPatients.stream().filter(predicates
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

    private List<Predicate<Polytechnic>> configFilter(Filter<Polytechnic> filter) {
        List<Predicate<Polytechnic>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<Polytechnic> idPredicate = (Polytechnic c) -> c.getOpno().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            Polytechnic filterEntity = filter.getEntity();
            if (has(filterEntity.getOpno())) {
                Predicate<Polytechnic> opnoPredicate = (Polytechnic c) -> c.getOpno().equals(filterEntity.getOpno());
                predicates.add(opnoPredicate);
            }

            if (has(filterEntity.getNames())) {
                Predicate<Polytechnic> namesPredicate = (Polytechnic c) -> c.getNames().toLowerCase().contains(filterEntity.getNames().toLowerCase());
                predicates.add(namesPredicate);
            }

        }
        return predicates;
    }

    public List<String> getOpno(String query) {
        return allPatients.stream().filter(c -> c.getOpno()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Polytechnic::getOpno)
                .collect(Collectors.toList());
    }

    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getNames()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Polytechnic::getNames)
                .collect(Collectors.toList());
    }

    public List<String> getPhones(String query) {
        return allPatients.stream().filter(c -> c.getPhone()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Polytechnic::getPhone)
                .collect(Collectors.toList());
    }

    public void insert(Polytechnic reception) {
        String msg;
        validate(reception);
        Db.connect();
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String polytechnic = reception.getNames();
            int userid = logonMB.getCurrentUserId();
            logger.info(CurrentTime() + "|" + "Create New Polytechnic Process" + "|" + "INFO" + "|" + "Processing - Create New Polytechnic "
                    + "|" + polytechnic + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating Polytechnic");

            String sql = "INSERT INTO `tbl_temis_polytechnic`(names,manager,phone,tr_male,tr_female,tr_material,benches,tables,chairs,tools,machines,date_created,created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = Db.conn.prepareStatement(sql);
            ps.setString(1, polytechnic);//names
            ps.setString(2, reception.getManager());//
            ps.setString(3, reception.getPhone());//
            ps.setString(4, reception.getTr_male());//           
            ps.setString(5, reception.getTr_female());//
            ps.setString(6, reception.getTr_material());//
            ps.setString(7, reception.getBenches());//
            ps.setString(8, reception.getTables());//
            ps.setString(9, reception.getChairs());//
            ps.setString(10, reception.getTools());//
            ps.setString(11, reception.getMachines());//   
            ps.setTimestamp(12, Db.getCurrentTimeStamp());
            ps.setInt(13, userid);//received_by

            int i = ps.executeUpdate();
            //logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
            allPatients.add(reception);
msg = "Polytechnic: " + reception.getNames() + " created successfully";
            logger.info(CurrentTime() + "|" + "Create New Polytechnic Process" + "|" + "INFO" + "|" + "Finished - Create New Polytechnic "
                    + "|" + polytechnic + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating Polytechnic");
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	msg = "Polytechnic: " + reception.getNames() + " not created, Please Try Again";
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        addDetailMessage(msg);
    }

    public void validate(Polytechnic reception) {
        BusinessException be = new BusinessException();
        if (!has(reception.getNames())) {
            be.addException(new BusinessException("Polytechnic Name Cannot be empty"));
        }
        if (allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(reception.getNames())
                && c.getNames() != c.getNames()).count() > 0) {
            be.addException(new BusinessException("Polytechnic name must be unique"));
        }
        if (has(be.getExceptionList())) {
            throw be;
        }
    }

    public int deletePatient(Polytechnic reception) {
        Db.connect();
        int ret = 0;
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String acctypename = reception.getNames();
            int tid = Integer.valueOf(reception.getOpno());
            int userid = logonMB.getCurrentUserId();

            logger.info(CurrentTime() + "|" + "Delete Polytechnic Process" + "|" + "INFO" + "|" + "Processing - Delete Polytechnic "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting Polytechnic");

            String sqldelete = "DELETE FROM tbl_temis_polytechnic WHERE tid= " + tid;
            logger.error("sqldelete" + sqldelete);
            stmt = Db.conn.createStatement();
            stmt.executeUpdate(sqldelete);

            logger.info(CurrentTime() + "|" + "Delete Polytechnic Process" + "|" + "INFO" + "|" + "Finished - Delete Polytechnic "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Delete Polytechnic");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ret = 1;
            e.printStackTrace();
            logger.error(e.getStackTrace());
        }
        Db.disconnect();
        return ret;
    }

    public void remove(Polytechnic reception) {
        deletePatient(reception);
        allPatients.remove(reception);
    }

    public long count(Filter<Polytechnic> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public Polytechnic findByOpno(String opno) {
        return allPatients.stream()
                .filter(c -> c.getOpno().equals(opno))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Polytechnic not found with ID# " + opno));
    }

    public void update(Polytechnic reception) {
        validate(reception);
        allPatients.remove(allPatients.indexOf(reception));
        allPatients.add(reception);
    }
}
