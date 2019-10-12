/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.infra.model.SortOrder;
import com.github.adminfaces.starter.infra.security.LogonMB;
import static com.github.adminfaces.starter.infra.security.LogonMB.hashpassword;
import com.github.adminfaces.starter.model.User;
//import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.util.Db;
import com.github.adminfaces.starter.util.SendingEmail;
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
import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
/**
 * @author rmpestano Reception Business logic
 */
public class UserService implements Serializable {

    @Inject
    List<User> allPatients;

    @Inject
    private LogonMB logonMB;
    protected PreparedStatement ps;
    protected Statement stmt;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(UserService.class);

    public String CurrentTime() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public List<User> listByModel(String names) {
        return allPatients.stream()
                .filter(c -> c.getNames().equalsIgnoreCase(names))
                .collect(Collectors.toList());

    }

    public List<User> paginate(Filter<User> filter) {
        List<User> pagedPatients = new ArrayList<>();
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

        List<Predicate<User>> predicates = configFilter(filter);

        List<User> pagedList = allPatients.stream().filter(predicates
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

    private List<Predicate<User>> configFilter(Filter<User> filter) {
        List<Predicate<User>> predicates = new ArrayList<>();
        if (filter.hasParam("id")) {
            Predicate<User> idPredicate = (User c) -> c.getOpno().equals(filter.getParam("id"));
            predicates.add(idPredicate);
        }

        if (has(filter.getEntity())) {
            User filterEntity = filter.getEntity();
            if (has(filterEntity.getOpno())) {
                Predicate<User> opnoPredicate = (User c) -> c.getOpno().equals(filterEntity.getOpno());
                predicates.add(opnoPredicate);
            }

            if (has(filterEntity.getNames())) {
                Predicate<User> namesPredicate = (User c) -> c.getNames().toLowerCase().contains(filterEntity.getNames().toLowerCase());
                predicates.add(namesPredicate);
            }

        }
        return predicates;
    }

    public List<String> getOpno(String query) {
        return allPatients.stream().filter(c -> c.getOpno()
                .toLowerCase().contains(query.toLowerCase()))
                .map(User::getOpno)
                .collect(Collectors.toList());
    }

    public List<String> getNames(String query) {
        return allPatients.stream().filter(c -> c.getNames()
                .toLowerCase().contains(query.toLowerCase()))
                .map(User::getNames)
                .collect(Collectors.toList());
    }

    public List<String> getUserLevels(String query) {
        return allPatients.stream().filter(c -> c.getAccount_type()
                .toLowerCase().contains(query.toLowerCase()))
                .map(User::getAccount_type)
                .collect(Collectors.toList());
    }

    public void insert(User reception) {
        String msg;
        validate(reception);
        Db.connect();
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String acctypename = reception.getNames();
            int userid = logonMB.getCurrentUserId();
            logger.info(CurrentTime() + "|" + "Create New User Account Type Process" + "|" + "INFO" + "|" + "Processing - Create New User Account Type "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Creating User Account Type");

            String userlevel = reception.getAccount_type();
            int level = 3;
            try {
                ps = Db.conn.prepareStatement("SELECT tid FROM tbl_temis_account_types where name ='" + userlevel + "' LIMIT 1");
                rs = ps.executeQuery();
                while (rs.next()) {
                    level = rs.getInt("tid");
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            String sql = "INSERT INTO `tbl_temis_users`(names,username,phone,email,password,account_type,hash,date_created,created_by) VALUES (?,?,?,?,?,?,?,?,?)";
            ps = Db.conn.prepareStatement(sql);
            ps.setString(1, acctypename);//names
            ps.setString(2, reception.getUsername());//username
            ps.setString(3, reception.getPhone());//phone
            ps.setString(4, reception.getEmail());//email
            String randomPassword = randomPassword();
            logger.error("randomPassword:"+randomPassword);
            String hashedpassword = hashpassword(randomPassword);
            logger.error("hashedpassword:"+hashedpassword);
            ps.setString(5, hashedpassword);//hashedPassword
            ps.setInt(6, level);//account type
            String myHash = myHash(reception.getEmail(), reception.getPhone());
            ps.setString(7, myHash);//hash
            ps.setTimestamp(8, Db.getCurrentTimeStamp());
            ps.setInt(9, userid);//received_by
            
            int i = ps.executeUpdate();
            //logger.error("reception.getNames()"+reception.getNames()+"reception.getNames()");
            allPatients.add(reception);
            msg = "User: " + reception.getNames() + " created successfully.An Email has also been sent to "+reception.getEmail()+" for Account Activation";            
            logger.info(CurrentTime() + "|" + "Create New User Account Type Process" + "|" + "INFO" + "|" + "Finished - Create New User Account Type "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Creating User Account Type");
            SendingEmail sendingEmail = new SendingEmail(reception.getEmail(), myHash, randomPassword, reception.getUsername());
            sendingEmail.sendEmail();
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	msg = "User: " + reception.getNames() + " not created, Please Try Again";
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
        addDetailMessage(msg);
    }

    public String randomPassword() {
        int length = 5;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        System.out.println(generatedString);
        return generatedString;
    }

    public String myHash(String email, String Phone) {
        String hash = "";
        String password = email + Phone;

        MessageDigest md;
        try {
            // Select the message digest for the hash computation -> SHA-256
            md = MessageDigest.getInstance("SHA-256");

            // Generate the random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Passing the salt to the digest for the computation
            md.update(salt);

            // Generate the salted hash
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hash;
    }

    public void validate(User reception) {
        BusinessException be = new BusinessException();
        if (!has(reception.getNames())) {
            be.addException(new BusinessException("User Name Cannot be empty"));
        }
        if (allPatients.stream().filter(c -> c.getNames().equalsIgnoreCase(reception.getNames())
                && c.getNames() != c.getNames()).count() > 0) {
            be.addException(new BusinessException("User name must be unique"));
        }
        if (has(be.getExceptionList())) {
            throw be;
        }
    }

    public int deletePatient(User reception) {
        Db.connect();
        int ret = 0;
        try {
            String IP = Inet4Address.getLocalHost().getHostAddress();
            String acctypename = reception.getNames();
            int tid = Integer.valueOf(reception.getOpno());
            int userid = logonMB.getCurrentUserId();

            logger.info(CurrentTime() + "|" + "Delete User Account Type Process" + "|" + "INFO" + "|" + "Processing - Delete User Account Type "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Begin Deleting User Account Type");

            String sqldelete = "DELETE FROM tbl_temis_users WHERE tid= " + tid;
            logger.error("sqldelete" + sqldelete);
            stmt = Db.conn.createStatement();
            stmt.executeUpdate(sqldelete);

            logger.info(CurrentTime() + "|" + "Delete User Account Type Process" + "|" + "INFO" + "|" + "Finished - Delete User Account Type "
                    + "|" + acctypename + " |" + userid + "|" + IP + "|" + "jdbc:mysql://localhost/temis" + "|" + "Finished Delete User Account Type");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            ret = 1;
            e.printStackTrace();
            logger.error(e.getStackTrace());
        }
        Db.disconnect();
        return ret;
    }

    public void remove(User reception) {
        deletePatient(reception);
        allPatients.remove(reception);
    }

    public long count(Filter<User> filter) {
        return allPatients.stream()
                .filter(configFilter(filter).stream()
                        .reduce(Predicate::or).orElse(t -> true))
                .count();
    }

    public User findByOpno(String opno) {
        return allPatients.stream()
                .filter(c -> c.getOpno().equals(opno))
                .findFirst()
                .orElseThrow(() -> new BusinessException("User not found with ID# " + opno));
    }

    public void update(User reception) {
        validate(reception);
        allPatients.remove(allPatients.indexOf(reception));
        allPatients.add(reception);
    }
}
