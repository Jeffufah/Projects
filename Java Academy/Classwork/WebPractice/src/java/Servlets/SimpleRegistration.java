/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Owner
 */
@WebServlet(name = "SimpleRegistration", urlPatterns = {"/SimpleRegistration"})
public class SimpleRegistration extends HttpServlet {

    private PreparedStatement pstmt;

    /** Initialize variables */
    public void init() throws ServletException {
    initializeJdbc();
    }
    
    
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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SimpleRegistration</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SimpleRegistration at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
            throws ServletException, IOException 
    {
        processRequest(request, response);
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
            throws ServletException, IOException 
    {
        
        PrintWriter out = response.getWriter();

        // Obtain parameters from the client
        String lastName = request.getParameter("lastName");
        String firstName = request.getParameter("firstName");
        String mi = request.getParameter("mi");
        String phone = request.getParameter("telephone");
        String email = request.getParameter("email");
        String address = request.getParameter("street");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String zip = request.getParameter("zip");

        try 
        {
            if (lastName.length() == 0 || firstName.length() == 0) {
                out.println("Last Name and First Name are required");
            } 
            else 
            {
                storeStudent(lastName, firstName, mi, phone, email,
                        address, city, state, zip);

                out.println(firstName + " " + lastName
                        + " is now registered in the database");
            }
        } 
        catch (Exception ex) 
        {
            out.println("Error: " + ex.getMessage());
        } 
        finally 
        {
            out.close(); // Close stream
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() 
    {
        return "Short description";
    }// </editor-fold>

    /** Initialize database connection */
    
    private void initializeJdbc() 
    {
        try 
        {
            // Load the JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded");

            // Establish a connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/WebPractice", "root", "");
            System.out.println("Database connected");

            // Create a Statement
            pstmt = conn.prepareStatement("insert into Address "
                    + "(lastName, firstName, mi, telephone, email, street, city, "
                    + "state, zip) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Store a student record to the database
     */
    private void storeStudent(String lastName, String firstName,
            String mi, String phone, String email, String address,
            String city, String state, String zip) throws SQLException 
    {
        pstmt.setString(1, lastName);
        pstmt.setString(2, firstName);
        pstmt.setString(3, mi);
        pstmt.setString(4, phone);
        pstmt.setString(5, email);
        pstmt.setString(6, address);
        pstmt.setString(7, city);
        pstmt.setString(8, state);
        pstmt.setString(9, zip);
        pstmt.executeUpdate();
    }
}
