<%-- 
    Document   : DO_login
    Created on : 9 Mar, 2024, 5:34:14 PM
    Author     : JAVA-JP
--%>
<%@page import="java.sql.Statement"%>
<%@page import="DBconnection.SQLconnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String fid = request.getParameter("fid");
    
    String uid = (String)session.getAttribute("duid");
    String uname = (String)session.getAttribute("duname");
    String umail = (String)session.getAttribute("dumail");
    System.out.println(" \n Fid :" + fid + "\n uid:" +uid);
    Connection con = SQLconnection.getconnection();
    Statement st = con.createStatement();
    Statement st1 = con.createStatement();
    try {
        ResultSet rs = st.executeQuery("Select * from do_files where id ='" + fid + "' ");
        if (rs.next()) {
            String fname = rs.getString("filename");
            String doid = rs.getString("doid");
            String dkey = rs.getString("dkey");
            String rdkey = rs.getString("rdkey");
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String time = dateFormat.format(date);
            System.out.println("Date and Time : " + time);
            int i = st1.executeUpdate("INSERT into request(filename, time, uid, uname, status, fid, doid, umail, dkey, rdkey, dostatus) values('" + fname + "','" + time + "','" + uid + "','" + uname + "','waiting','" + fid + "', '" + doid+ "','" + umail+ "','" + dkey+ "','" + rdkey+ "','waiting')");
            if (i != 0) {
                response.sendRedirect("searchFile.jsp?Requestsent");
            }
        } else {
            response.sendRedirect("searchFile.jsp?failed");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
%>

