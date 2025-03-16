<%-- 
    Document   : du_signin
    Created on : 13 Jan, 2024, 11:42:02 AM
    Author     : JAVA-JP
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="DBconnection.SQLconnection"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String mail = request.getParameter("email");
    String pass = request.getParameter("pass");
    String pkey = request.getParameter("pkey");

    System.out.println("Check User name And Password : " + mail + pass);
    Connection con = SQLconnection.getconnection();
    Statement st = con.createStatement();
    Statement sto = con.createStatement();
    try {
        ResultSet rs = st.executeQuery("SELECT * FROM du_reg where email='" + mail + "' AND password='" + pass + "' AND private_key='" + pkey + "' AND status = 'Approved' ");
        if (rs.next()) {
            session.setAttribute("duid", rs.getString("id"));
            session.setAttribute("duname", rs.getString("name"));
            session.setAttribute("dumail", rs.getString("email"));
            //session.setAttribute("attribute", rs.getString("attribute"));
            
            response.sendRedirect("duHome.jsp?Success");
        } else {
            response.sendRedirect("duLogin.jsp?Failed");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
%>