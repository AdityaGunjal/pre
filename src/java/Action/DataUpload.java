/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Action;

import DBconnection.SQLconnection;
import Networks.DRIVE_Network;
import com.oreilly.servlet.MultipartRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author java4
 */
public class DataUpload extends HttpServlet {

    File file;
    final String filepath = "D:/";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            HttpSession user = request.getSession(true);
            String encryptedtext = user.getAttribute("EncryptText").toString();
            String encryptedtext1 = user.getAttribute("EncryptText1").toString();
            String encryptTime = user.getAttribute("encryptTime").toString();
            String Dkey = user.getAttribute("Dkey").toString();
            String RDkey = user.getAttribute("RDkey").toString();
            String filecontent = user.getAttribute("filecontent").toString();
            String doname = user.getAttribute("doname").toString();
            String doid = user.getAttribute("doid").toString();
            String domail = user.getAttribute("domail").toString();
            String fname = (String) user.getAttribute("filename");
            String fpath = (String) user.getAttribute("filepath");
            String f1, f2, f3;
            f1 = fpath + "/" + fname;
            f2 = fpath + "\\" + fname + "2";
            f3 = fpath + "\\" + fname + "3";
            System.out.println("--------->>" + f1);

            String ori_block1 = (String) user.getAttribute("ori_block1");
            String ori_block2 = (String) user.getAttribute("ori_block2");
            String ori_block3 = (String) user.getAttribute("ori_block3");

            System.out.println("Welcome File Upload");
            MultipartRequest m = new MultipartRequest(request, filepath);
            String keyword = m.getParameter("keyword");
            String block1 = m.getParameter("block1");
            String block2 = m.getParameter("block2");
            String block3 = m.getParameter("block3");
            String CeasarBlock;
            String plain = (String) user.getAttribute("filecontent");
            List<String> list = new ArrayList<>();
            list.add(block1);
            list.add(block2);
            list.add(block3);

            int num = 0;
            String dump = "";
            String dump1 = "";
            String pack = "";
            boolean status = false;
            for (String key : list) {
                num++;
                pack = "\\cloud" + num + "//";
                dump1 = keyword + num;
                dump = "D:/Filesplit/" + dump1 + ".txt";

                FileWriter bw = new FileWriter("" + dump + "");

                bw.write(key);
                bw.close();
                System.out.println(dump1 + " : " + key);
                System.out.println("Directory----->" + pack);
                status = new DRIVE_Network().upload(new File(dump), pack);
                System.out.println("Status----->" + status);

            }

            int hash1 = block1.hashCode();
            int hash2 = block2.hashCode();
            int hash3 = block3.hashCode();
            System.out.println("Hash 1 : " + hash1);
            System.out.println("Hash 2 : " + hash2);
            System.out.println("Hash 3 : " + hash3);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);

            PreparedStatement pstm = null;
            Connection con = SQLconnection.getconnection();
            if (true) {
                try {

                    String query = "insert into do_files (doid, doname, enc_data, dkey, time, filekeyword, filename, data, block1, block2, block3, hash1, hash2, hash3, ori_block1, ori_block2, ori_block3, rdkey, reencrypt_data, encryptTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
                    pstm = con.prepareStatement(query);
                    pstm.setString(1, doid);
                    pstm.setString(2, doname);
                    pstm.setString(3, encryptedtext);
                    pstm.setString(4, Dkey);
                    pstm.setString(5, time);
                    pstm.setString(6, keyword);
                    pstm.setString(7, fname);
                    pstm.setString(8, filecontent);
                    pstm.setString(9, block1);
                    pstm.setString(10, block2);
                    pstm.setString(11, block3);
                    pstm.setInt(12, hash1);
                    pstm.setInt(13, hash2);
                    pstm.setInt(14, hash3);
                    pstm.setString(15, ori_block1);
                    pstm.setString(16, ori_block2);
                    pstm.setString(17, ori_block3);
                    pstm.setString(18, RDkey);
                    pstm.setString(19, encryptedtext1);
                    pstm.setString(20, encryptTime);
                    int row = pstm.executeUpdate();
                    if (row > 0) {
                        response.sendRedirect("uploadFile.jsp?File_uploaded");
                    } else {
                        response.sendRedirect("uploadFile.jsp?Upload_Failed");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void split(String FilePath, long splitlen) {
        long leninfile = 0, leng = 0;
        int count = 1, data;
        try {
            File filename = new File(FilePath);
//RandomAccessFile infile = new RandomAccessFile(filename, "r");
            InputStream infile = new BufferedInputStream(new FileInputStream(filename));
            data = infile.read();
            while (data != -1) {
                filename = new File(FilePath + count + ".sp");
//RandomAccessFile outfile = new RandomAccessFile(filename, "rw");
                OutputStream outfile = new BufferedOutputStream(new FileOutputStream(filename));
                while (data != -1 && leng < splitlen) {
                    outfile.write(data);
                    leng++;
                    data = infile.read();
                }
                leninfile += leng;
                leng = 0;
                outfile.close();
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
