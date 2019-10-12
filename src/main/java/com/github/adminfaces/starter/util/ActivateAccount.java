/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.util;

import com.github.adminfaces.template.config.AdminConfig;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.omnifaces.util.Faces;

/**
 *
 * @author SKYWARD DIGITAL
 */
@WebServlet(name = "ActivateAccount", urlPatterns = {"/ActivateAccount"})
public class ActivateAccount extends HttpServlet {

    @Inject
    private AdminConfig adminConfig;
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(ActivateAccount.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        /*     try (PrintWriter out = response.getWriter()) {           
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ActivateAccount</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ActivateAccount at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
         */
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        String email = request.getParameter("key1");
        String hash = request.getParameter("key2");

        Db.connect();
        try {
            ps = Db.conn.prepareStatement("SELECT email,hash,active FROM tbl_temis_users where email=? and hash=? and active=1 LIMIT 1");
            ps.setString(1, email);
            ps.setString(2, hash);
            logger.error("ps"+ps.toString());
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PreparedStatement pst1 = Db.conn.prepareStatement("UPDATE tbl_temis_users SET active='1' WHERE email=? AND hash=?");
                pst1.setString(1, email);
                pst1.setString(2, hash);
                
                int i = pst1.executeUpdate();
                if (i == 1) {                    
                    Faces.getExternalContext().getFlash().setKeepMessages(true);
                    Faces.redirect(adminConfig.getIndexPage());
                } else {                    
                    Faces.getExternalContext().getFlash().setKeepMessages(true);
                    Faces.redirect(adminConfig.getIndexPage());
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        Db.disconnect();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
