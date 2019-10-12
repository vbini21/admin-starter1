package com.github.adminfaces.starter.infra.security;

import com.github.adminfaces.template.session.AdminSession;

import org.apache.log4j.Logger;
import org.omnifaces.util.Faces;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.github.adminfaces.starter.util.Db.ps;
import static com.github.adminfaces.starter.util.Db.rs;
import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import com.github.adminfaces.starter.util.Db;
import com.github.adminfaces.template.config.AdminConfig;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * Created by rmpestano on 12/20/14.
 *
 * This is just a login example.
 *
 * AdminSession uses isLoggedIn to determine if user must be redirect to login
 * page or not. By default AdminSession isLoggedIn always resolves to true so it
 * will not try to redirect user.
 *
 * If you already have your authorization mechanism which controls when user
 * must be redirect to initial page or logon you can skip this class.
 */
@Named
@SessionScoped
@Specializes
public class LogonMB extends AdminSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String currentUser;
	private String email;	
	private String password;
	private String category;
	private String categoryID;
	private int ecdeID;
	private boolean remember;
	private int currentUserId;
	private int polytechnic_id;
	private String guardian_idno;
	private String email_applicant;
	@Inject
	private AdminConfig adminConfig;
	protected PreparedStatement ps;
	protected ResultSet rs;
	private static Logger logger = Logger.getLogger(LogonMB.class);

	public void login() throws IOException {
		Db.connect();
		String hashedpassword = hashpassword(password);
		if (category.equals("ADMIN")) {		
			category = "ALL";
			categoryID = "1";
			try {
				String sql = "select * from tbl_temis_users where (username = '" + email + "' OR email = '" + email
						+ "') AND (account_type = 1 OR account_type = 2) AND password = '" + hashedpassword
						+ "' and active ='1'";
				ps = Db.conn.prepareStatement(sql);
				logger.info("ADMIN");
				logger.info(sql);

				rs = ps.executeQuery();

				if (rs.next()) {
					currentUserId = rs.getInt("tid");
					currentUser = email;
					addDetailMessage("Logged in successfully as <b>" + email + "</b>");
					Faces.getExternalContext().getFlash().setKeepMessages(true);
					Faces.redirect(adminConfig.getIndexPage());

				} else {
					sql = "select * from tbl_temis_users where (username = '" + email + "' OR email = '" + email
							+ "') AND password = '" + hashedpassword + "' and active ='0'";
					ps = Db.conn.prepareStatement(sql);
					logger.info(sql);
					rs = ps.executeQuery();

					if (rs.next()) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								"Please click on the link sent to your email for account activation."));
						FacesContext.getCurrentInstance().validationFailed();
					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage("Invalid username or email and password combination"));
						logger.info(email + " Invalid username or email and password combination");
						FacesContext.getCurrentInstance().validationFailed();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		if (category.equals("ECDE")) {
			category = "ECDE";
			categoryID = "2";
			try {
				String sql = "select * from tbl_temis_users where (username = '" + email + "' OR email = '" + email
						+ "') AND account_type = 3  AND password = '" + hashedpassword + "' and active ='1'";
				ps = Db.conn.prepareStatement(sql);
				logger.info("SCHOOL");
				logger.info(sql);

				rs = ps.executeQuery();

				if (rs.next()) {
					currentUserId = rs.getInt("tid");
					String lookupEmail = rs.getString("email");
					sql = "select ecde_id,email from tbl_temis_teachers_ecde where email = '" + lookupEmail + "'";
					logger.info(sql);
					ps = Db.conn.prepareStatement(sql);
					logger.info(sql);

					rs = ps.executeQuery();

					if (rs.next()) {
						currentUser = email;
						ecdeID = rs.getInt("ecde_id");
						addDetailMessage("Logged in successfully as <b>" + email + "</b>");
						Faces.getExternalContext().getFlash().setKeepMessages(true);
						Faces.redirect(adminConfig.getIndexPage());

					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage("Invalid username or email and password combination"));
						logger.info(email + " is not registered as an ECDE Teacher");
						FacesContext.getCurrentInstance().validationFailed();
					}
				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Invalid username or email and password combination"));
					logger.info(email + " Invalid username or email and password combination");
					FacesContext.getCurrentInstance().validationFailed();

				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		if (category.equals("POLYTECHNIC")) {
			category = "POLYTECHNIC";
			categoryID = "3";
			try {
				String sql = "select * from tbl_temis_users where (username = '" + email + "' OR email = '" + email
						+ "') AND account_type = 3 AND password = '" + hashedpassword + "' and active ='1'";
				ps = Db.conn.prepareStatement(sql);
				logger.info("POLYTECHNIC");
				logger.info(sql);

				rs = ps.executeQuery();
				if (rs.next()) {
					currentUserId = rs.getInt("tid");
					String lookupEmail = rs.getString("email");
					sql = "select polytechnic_id,email from tbl_temis_polystaff where email = '" + lookupEmail + "'";
					ps = Db.conn.prepareStatement(sql);
					logger.info(sql);
					rs = ps.executeQuery();

					if (rs.next()) {
						currentUser = email;
						polytechnic_id = rs.getInt("polytechnic_id");
						addDetailMessage("Logged in successfully as <b>" + email + "</b>");
						Faces.getExternalContext().getFlash().setKeepMessages(true);
						Faces.redirect(adminConfig.getIndexPage());
					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage("Invalid username or email and password combination"));
						logger.info(email + " is not registered as a Polytechnic Teacher");
						FacesContext.getCurrentInstance().validationFailed();
					}

				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Invalid username or email and password combination"));
					FacesContext.getCurrentInstance().validationFailed();
					logger.info(email + " Invalid username or email and password combination");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		Db.disconnect();
	}
	
	public static String hashpassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] passBytes = password.getBytes();
			md.reset();
			byte[] digested = md.digest(passBytes);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digested.length; i++) {
				sb.append(Integer.toHexString(0xff & digested[i]));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isLoggedIn() {

		return currentUser != null;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRemember() {
		return remember;
	}

	public void setRemember(boolean remember) {
		this.remember = remember;
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}

	public int getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(int currentUserId) {
		this.currentUserId = currentUserId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

	public int getEcdeID() {
		return ecdeID;
	}

	public void setEcdeID(int ecdeID) {
		this.ecdeID = ecdeID;
	}

	public int getPolytechnic_id() {
		return polytechnic_id;
	}

	public void setPolytechnic_id(int polytechnic_id) {
		this.polytechnic_id = polytechnic_id;
	}
	
	public String checkID(){
		// Do any action that you would.
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Guardian IDNO is "+guardian_idno));
		FacesContext.getCurrentInstance().validationFailed();
		return "";
	}

	public String getGuardian_idno() {
		return guardian_idno;
	}

	public void setGuardian_idno(String guardian_idno) {
		this.guardian_idno = guardian_idno;
	}

	public String getEmail_applicant() {
		return email_applicant;
	}

	public void setEmail_applicant(String email_applicant) {
		this.email_applicant = email_applicant;
	}
	
	

}
