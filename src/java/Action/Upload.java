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
import com.oreilly.servlet.MultipartRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author java3
 */
public class Upload extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    File file;
    final String filepath = "D:/";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            MultipartRequest m = new MultipartRequest(request, filepath);
            String filekeyword = m.getParameter("keyword");
            File file = m.getFile("fileToUpload");
            String filename = file.getName().toLowerCase();

            String data = "null";

            Connection con = SQLconnection.getconnection();

            BufferedReader br = new BufferedReader(new FileReader(filepath + filename));
            StringBuffer sb = new StringBuffer();
            String temp = null;

            while ((temp = br.readLine()) != null) {
                sb.append(temp + "\n");
            }

            KeyGenerator Attrib_key = KeyGenerator.getInstance("AES");
            Attrib_key.init(128);
            SecretKey secretKey = Attrib_key.generateKey();
            System.out.println("++++++++ key:" + secretKey);

            Encryption e = new Encryption();
            String encryptedtext = e.encrypt(sb.toString(), secretKey);

            int hash1 = encryptedtext.hashCode();
            byte[] b = secretKey.getEncoded();
            String Dkey = Base64.getEncoder().encodeToString(b);
            System.out.println("converted secretkey to string:" + Dkey);

            BufferedReader br1 = new BufferedReader(new FileReader(filepath + filename));
            StringBuffer sb1 = new StringBuffer();
            String temp1 = null;

            while ((temp1 = br1.readLine()) != null) {
                sb1.append(temp1 + "\n");
            }

            KeyGenerator Attrib_key1 = KeyGenerator.getInstance("AES");
            Attrib_key1.init(128);
            SecretKey secretKey1 = Attrib_key1.generateKey();
            System.out.println("++++++++ key1:" + secretKey1);

            long aTime = System.nanoTime();
            
            Encryption e1 = new Encryption();
            String encryptedtext1 = e1.encrypt(sb1.toString(), secretKey1);
            
            long bTime = System.nanoTime();
            float encryptTime = (bTime - aTime) / 1000;
            System.out.println("Time taken to Encrypt File: " + encryptTime + " microseconds.");
            
            //storing re-encrypted file
            FileWriter fw1 = new FileWriter(file);
            fw1.write(encryptedtext1);
            int hash2 = encryptedtext1.hashCode();
            fw1.close();
            byte[] b1 = secretKey1.getEncoded();
            String RDkey = java.util.Base64.getEncoder().encodeToString(b1);
            System.out.println("converted secretkey to string:" + RDkey);

            HttpSession user = request.getSession(true);
            String doname = user.getAttribute("doname").toString();
            String doid = user.getAttribute("doid").toString();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);
            System.out.println("current Date " + time);

            //boolean status = new DRIVE_Network().upload(file);
           // if (status) {

                Statement st = con.createStatement();

                System.out.println("Check------------------------------------------------------------------>>>>>");

                int i = st.executeUpdate("insert into do_files(doid, doname, data_file, dkey, time, filename, data, filekeyword, reencrypt_file, rdkey, hashcode, enc_time)values('" + doid + "','" + doname + "','" + encryptedtext + "','" + Dkey + "','" + time + "','" + file.getName() + "','" + sb.toString() + "', '" + filekeyword + "', '" + encryptedtext1 + "', '" + RDkey + "','" + hash2 + "','"+ encryptTime +"')");
                if (i != 0) {
                    response.sendRedirect("uploadFile.jsp?File_uploaded");

                } else {
                    System.out.println("Error in SQL Syntex");
                }
            //}
        } catch (Exception e) {
            out.println(e);
        } finally {
            out.close();
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
