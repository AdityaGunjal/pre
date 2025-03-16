/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Action;

/**
 *
 * @author JAVA-JP
 */
import DBconnection.SQLconnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author java1
 */
/**
 *
 * @author Lenovo
 */
@WebServlet("/download")
public class Download extends HttpServlet {

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

            String fileid = request.getParameter("fid");
            String filename = request.getParameter("filename");
            String rdkey = request.getParameter("rdkey");

            HttpSession user = request.getSession();
            String uid = user.getAttribute("duid").toString();
            String uname = user.getAttribute("duname").toString();
            String umail = user.getAttribute("dumail").toString();

            Connection conn = SQLconnection.getconnection();
            Statement st = conn.createStatement();
            Statement st1 = conn.createStatement();
            Statement st2 = conn.createStatement();

            ResultSet rs = st2.executeQuery(" Select * from request where fid ='" + fileid + "' AND status='Approved' AND rdkey='" + rdkey + "'");

            if (rs.next()) {

                ResultSet rs1 = st.executeQuery(" Select * from do_files where id ='" + fileid + "' AND filename ='" + filename + "'  AND rdkey='" + rdkey + "'");
                if (rs1.next()) {
                    String doid = rs1.getString("doid");
                    String doname = rs1.getString("doname");
                    String file = rs1.getString("reencrypt_data");
                    String dkey = rs1.getString("dkey");

                    System.out.println("rdkey-- " + rdkey);
                    long aTime = System.nanoTime();
                    Decryption d1 = new Decryption();
                    String decrypted = d1.decrypt(file, rdkey);
                    long bTime = System.nanoTime();
                    float decryptTime = (bTime - aTime) / 1000;

                    System.out.print("\nPlain Text             --------  " + file);
                    System.out.print("\nDecrypted Text             --------  " + decrypted);

                    System.out.println("filename,fileid==" + filename + fileid);

                    String is = decrypted;

                    response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
                    out.write(is);
                    out.close();
                    int i = st1.executeUpdate("insert into download (uid, uname, filename, time , fileid , doname ,doid, decrypt_time)values('" + uid + "','" + uname + "','" + filename + "',now(),'" + fileid + "','" + doname + "','" + doid + "','" + decryptTime + "')");

                } else {
                    System.out.println("file not found...");
                }
            } else {
                response.sendRedirect("Requested_files.jsp?failed");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            response.sendRedirect("Requested_files.jsp?download_failed");
        } catch (IOException ex) {
            ex.printStackTrace();
            response.sendRedirect("Requested_files.jsp?download_failed");
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
            throws ServletException, IOException {
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
