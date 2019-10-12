/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import static com.github.adminfaces.starter.infra.security.LogonMB.hashpassword;
import com.github.adminfaces.starter.model.Bursary;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author rmpestano Reception Business logic
 */
public class BursaryService implements Serializable {

    @Inject
    List<Bursary> allPatients;

    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(BursaryService.class);

    public String CurrentTime() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public List<Bursary> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getNames().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<Bursary> paginate(Filter<Bursary> filter) {
        List<Bursary> pagedPatients = new ArrayList<>();
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

        List<Predicate<Bursary>> predicates = configFilter(filter);

        List<Bursary> pagedList = allPatients.stream().filter(predicates
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

    private List<Predicate<Bursary>> configFilter(Filter<Bursary> filter) {
        List<Predicate<Bursary>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<Bursary> idPredicate = (Bursary c) -> c.getOpno().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            Bursary filterEntity = filter.getEntity();
            if (has(filterEntity.getOpno())) {
                Predicate<Bursary> opnoPredicate = (Bursary c) -> c.getOpno().equals(filterEntity.getOpno());
                predicates.add(opnoPredicate);
            }

            if (has(filterEntity.getNames())) {
                Predicate<Bursary> namesPredicate = (Bursary c) -> c.getNames().toLowerCase().contains(filterEntity.getNames().toLowerCase());
                predicates.add(namesPredicate);
            }

        }
        return predicates;
    }

    public List<String> getOpno(String query) {
        return allPatients.stream().filter(c -> c.getOpno()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Bursary::getOpno)
                .collect(Collectors.toList());
    }

    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getNames()
                .toLowerCase().contains(query.toLowerCase()))
                .map(Bursary::getNames)
                .collect(Collectors.toList());
    }


    public String [] insert(Bursary reception) {
        String [] ret = new String [2];
        validate(reception);
        Db.connect();
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String ecde = reception.getNames();
            int userid = logonMB.getCurrentUserId();
            logger.info(CurrentTime() + "|" + "Create New Bursary Account Type Process" + "|" + "INFO" + "|" + "Processing - Create New Bursary Account Type "
                    + "|" + ecde + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating Bursary Account Type");

            String sql = "INSERT INTO `tbl_temis_bursaries`(names,dob,sex,yos,study_level,admno,inst,inst_contact,inst_location,inst_email,school_type,school_category,"
                    + "village,course_type,disability,guardian_name,guardian_idno,parent_contact,date_created,created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = Db.conn.prepareStatement(sql);
            ps.setString(1, ecde);//names
            ps.setString(2, reception.getDob());//username
            ps.setString(3, reception.getSex());//phone
            ps.setString(4, reception.getYos());//email
            ps.setString(5, reception.getStudy_level());//names
            ps.setString(6, reception.getAdmno());//username
            ps.setString(7, reception.getInst());//phone
            ps.setString(8, reception.getInst_contact());//email
            ps.setString(9, reception.getInst_location());//names
            ps.setString(10, reception.getInst_email());//username
            ps.setString(11, reception.getSchool_type());//phone
            ps.setString(12, reception.getSchool_category());//email
            ps.setString(13, reception.getVillage());//names
            ps.setString(14, reception.getCourse_type());//username
            ps.setString(15, reception.getDisability());//phone
            ps.setString(16, reception.getGuardian_name());//email
            ps.setString(17, reception.getGuardian_idno());//username
            ps.setString(18, reception.getParent_contact());//phone            
            ps.setTimestamp(19, Db.getCurrentTimeStamp());
            ps.setInt(20, userid);//received_by
            
            int i = ps.executeUpdate();
            //logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
            allPatients.add(reception);

            logger.info(CurrentTime() + "|" + "Create New Bursary Account Type Process" + "|" + "INFO" + "|" + "Finished - Create New Bursary Account Type "
                    + "|" + ecde + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating Bursary Account Type");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return ret;
    }

    public void validate(Bursary reception) {
        BusinessException be = new BusinessException();
        if (!has(reception.getNames())) {
            be.addException(new BusinessException("Bursary Name Cannot be empty"));
        }
        if (allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(reception.getNames())
                && c.getNames() != c.getNames()).count() > 0) {
            be.addException(new BusinessException("Bursary name must be unique"));
        }
        if (has(be.getExceptionList())) {
            throw be;
        }
    }

    public int deletePatient(Bursary reception) {
        Db.connect();
        int ret = 0;
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String acctypename = reception.getNames();
            int tid = Integer.valueOf(reception.getOpno());
            int userid = logonMB.getCurrentUserId();

            logger.info(CurrentTime() + "|" + "Delete Bursary Account Type Process" + "|" + "INFO" + "|" + "Processing - Delete Bursary Account Type "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting Bursary Account Type");

            String sqldelete = "DELETE FROM tbl_temis_bursaries WHERE tid= " + tid;
            logger.error("sqldelete" + sqldelete);
            stmt = Db.conn.createStatement();
            stmt.executeUpdate(sqldelete);

            logger.info(CurrentTime() + "|" + "Delete Bursary Account Type Process" + "|" + "INFO" + "|" + "Finished - Delete Bursary Account Type "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Delete Bursary Account Type");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ret = 1;
            e.printStackTrace();
            logger.error(e.getStackTrace());
        }
        Db.disconnect();
        return ret;
    }

    public void remove(Bursary reception) {
        deletePatient(reception);
        allPatients.remove(reception);
    }

    public long count(Filter<Bursary> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public Bursary findByOpno(String opno) {
        return allPatients.stream()
                .filter(c -> c.getOpno().equals(opno))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Bursary not found with ID# " + opno));
    }

    public void update(Bursary reception) {
        validate(reception);
        allPatients.remove(allPatients.indexOf(reception));
        allPatients.add(reception);
    }
}
