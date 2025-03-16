<%-- 
    Document   : access_grant
    Created on : sept 30 , 2020, 5:14:44 AM
    Author     : Lenovo
--%>

<%@page import="Networks.Mail"%>
<%@page import="DBconnection.SQLconnection"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String id = request.getParameter("id");

    Connection con = null;
    Statement st = null;
    Statement st1 = null;
    Connection conn = SQLconnection.getconnection();
    Statement sto = conn.createStatement();
    st = conn.createStatement();

    try {
        int i = sto.executeUpdate("update do_reg set status='Approved' where id='" + id + "' ");
        System.out.println("test print==" + id);
        if (i != 0) {
            ResultSet rs = st.executeQuery(" SELECT * from do_reg where id = '" +id + "' ");
            if (rs.next()) {
                String name = rs.getString("name");
                String mail = rs.getString("email");
                String private_key = rs.getString("private_key");
                String msggg = "Hi, " + name + "Your Data Owner Registration is Approved\nPrivate Key : " + private_key ;
                Mail ma = new Mail();
                ma.secretMail(msggg, "SecretKey", mail);
                String msg = "Secret:" + msggg;
                System.out.println("success");
                System.out.println("success");
                response.sendRedirect("dataOwners.jsp?Approved");
            } else {

                System.out.println("failed");
                response.sendRedirect("dataOwners.jsp?Failed");
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
%>
