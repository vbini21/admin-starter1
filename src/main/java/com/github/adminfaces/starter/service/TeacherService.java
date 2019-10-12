/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.Teacher;
import com.github.adminfaces.starter.bean.helpers.Refreshers;
import com.github.adminfaces.starter.bean.helpers.Time;
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

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;

/**
 * @author rmpestano Reception Business logic
 */
public class TeacherService implements Serializable {

    @Inject
    List<Teacher> allPatients;
    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(TeacherService.class);

    public List<Teacher> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getNames().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<Teacher> paginate(Filter<Teacher> filter) {
        List<Teacher> pagedPatients = new ArrayList<>();
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

        List<Predicate<Teacher>> predicates = configFilter(filter);

        List<Teacher> pagedList = allPatients.stream().filter(predicates
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

    private List<Predicate<Teacher>> configFilter(Filter<Teacher> filter) {
        List<Predicate<Teacher>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<Teacher> idPredicate = (Teacher c) -> c.getOpno().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            Teacher filterEntity = filter.getEntity();
            if (has(filterEntity.getOpno())) {
                Predicate<Teacher> opnoPredicate = (Teacher c) -> c.getOpno().equals(filterEntity.getOpno());
                predicates.add(opnoPredicate);
            }

            if (has(filterEntity.getNames())) {
                Predicate<Teacher> namesPredicate = (Teacher c) -> c.getNames().toLowerCase().contains(filterEntity.getNames().toLowerCase());
                predicates.add(namesPredicate);
            }

        }
        return predicates;
    }

    public List<String> getOpno(String query) {
        return allPatients.stream().filter(c -> c.getOpno()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Teacher::getOpno)
                .collect(Collectors.toList());
    }

    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getNames()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Teacher::getNames)
                .collect(Collectors.toList());
    }

    public List<String> getPhones(String query) {
        return allPatients.stream().filter(c -> c.getPhone()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Teacher::getPhone)
                .collect(Collectors.toList());
    }

    public void insert(Teacher reception) {     
    	 String msg;
        validate(reception);
        Db.connect();        
        int schoolID = Refreshers.getSchoolID(reception.getSchool());
        int ecdeID = Refreshers.getEcdeID(reception.getEcde());
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String teachername = reception.getNames();
            int userid = logonMB.getCurrentUserId();
            logger.info(Time.CurrentTime() + "|" + "Create New Teacher Process" + "|" + "INFO" + "|" + "Processing - Create New Teacher "
                    + "|" + teachername + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating Teacher");

            String sql = "INSERT INTO `tbl_temis_teachers_ecde`(names,ecde_id,phone,email,school_id,tscno,idno,academic_q,prof_q,sponsor,date_created,created_by)"
                    + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            
            ps = Db.conn.prepareStatement(sql);
            ps.setString(1, teachername);//names
            ps.setInt(2, ecdeID);//ecde
            ps.setString(3, reception.getPhone());//phone
            ps.setString(4, reception.getEmail());//email        
            ps.setInt(5, schoolID);//school
            ps.setString(6, reception.getTscno());//tscno
            ps.setString(7, reception.getIdno());//idno
            ps.setString(8, reception.getAcademic_q());//academic q   
            ps.setString(9, reception.getProf_q());//prof q
            ps.setString(10, reception.getSponsor());//sponsor  
            ps.setTimestamp(11, Db.getCurrentTimeStamp());
            ps.setInt(12, userid);//received_by
            logger.info(ps.toString());
            int i = ps.executeUpdate();
            allPatients.add(reception);
            msg = "Teacher: " + reception.getNames() + " created successfully";
            logger.info(Time.CurrentTime() + "|" + "Create New Teacher Process" + "|" + "INFO" + "|" + "Finished - Create New Teacher "
                    + "|" + teachername + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating Teacher");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            msg = "Teacher: " + reception.getNames() + " not created, Please Try Again";
        }
        Db.disconnect();
        addDetailMessage(msg);
        
    }

    public void validate(Teacher reception) {
        BusinessException be = new BusinessException();
        if (!has(reception.getNames())) {
            be.addException(new BusinessException("Teacher Name Cannot be empty"));
        }
        if (allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(reception.getNames())
                && c.getNames() != c.getNames()).count() > 0) {
            be.addException(new BusinessException("Teacher name must be unique"));
        }
        if (has(be.getExceptionList())) {
            throw be;
        }
    }

    public int deletePatient(Teacher reception) {
        Db.connect();
        int ret = 0;
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String acctypename = reception.getNames();
            int tid = Integer.valueOf(reception.getOpno());
            int userid = logonMB.getCurrentUserId();

            logger.info(Time.CurrentTime() + "|" + "Delete Teacher Process" + "|" + "INFO" + "|" + "Processing - Delete Teacher "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting Teacher");

            String sqldelete = "DELETE FROM tbl_temis_teachers_ecde WHERE tid= " + tid;
            logger.error("sqldelete" + sqldelete);
            stmt = Db.conn.createStatement();
            stmt.executeUpdate(sqldelete);

            logger.info(Time.CurrentTime() + "|" + "Delete Teacher Process" + "|" + "INFO" + "|" + "Finished - Delete Teacher "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Delete Teacher");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ret = 1;
            e.printStackTrace();
            logger.error(e.getStackTrace());
        }
        Db.disconnect();
        return ret;
    }

    public void remove(Teacher reception) {
        deletePatient(reception);
        allPatients.remove(reception);
    }

    public long count(Filter<Teacher> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public Teacher findByOpno(String opno) {
        return allPatients.stream()
                .filter(c -> c.getOpno().equals(opno))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Teacher not found with ID# " + opno));
    }

    public void update(Teacher reception) {
        validate(reception);
        allPatients.remove(allPatients.indexOf(reception));
        allPatients.add(reception);
    }
}
