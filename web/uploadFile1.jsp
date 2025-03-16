<%-- 
    Document   : index
    Created on : 26 Aug, 2024, 4:51:26 PM
    Author     : JAVA-JP
--%>

<%@page import="java.io.FileWriter"%>
<%@page import="com.sun.org.apache.xerces.internal.impl.dv.util.Base64"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%><%@page import="java.io.File"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%>
<%@page import="javax.crypto.KeyGenerator"%>
<%@page import="javax.crypto.SecretKey"%>
<%@page import="Action.Encryption"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="Action.SplitFile"%>
<%@page import="java.security.SecureRandom"%>
<%@page import="java.util.Random"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="DBconnection.SQLconnection"%>
<%@page import="java.sql.Connection"%>
<%

    final String filepath = "D:/";
    String f1, f2, f3;

    try {
        MultipartRequest m = new MultipartRequest(request, filepath);
        String filekeyword = m.getParameter("keyword");
        File file = m.getFile("fileToUpload");
        String filename = file.getName().toLowerCase();
        session.setAttribute("filename", filename);
        session.setAttribute("filepath", filepath);

        Connection con = SQLconnection.getconnection();
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("Select * from do_files where filekeyword ='" + filekeyword + "'");
        if (rs.next()) {

            response.sendRedirect("uploadFile.jsp?msg=FileKeyword_Already_Exists");
        }

        BufferedReader br = new BufferedReader(new FileReader(filepath + filename));
        StringBuffer sb = new StringBuffer();
        String temp;

        while ((temp = br.readLine()) != null) {
            sb.append(temp);
            sb.append("\n");
        }

        session.setAttribute("filecontent", sb.toString());
        KeyGenerator Attrib_key = KeyGenerator.getInstance("AES");
        Attrib_key.init(128);
        SecretKey secretKey = Attrib_key.generateKey();
        System.out.println("++++++++ key:" + secretKey);
        session.setAttribute("secretKey", secretKey);

        Encryption e = new Encryption();
        String encryptedtext = e.encrypt(sb.toString(), secretKey);
        session.setAttribute("EncryptText", encryptedtext);
        byte[] b = secretKey.getEncoded();
        String Dkey = Base64.encode(b);
        System.out.println("converted secretkey to string: " + Dkey);
        session.setAttribute("Dkey", Dkey);

        BufferedReader brd = new BufferedReader(new FileReader(filepath + filename));
        StringBuffer sbd = new StringBuffer();
        String tmp = null;

        while ((tmp = brd.readLine()) != null) {
            sbd.append(tmp + "\n");
        }

        KeyGenerator Attrib_key1 = KeyGenerator.getInstance("AES");
        Attrib_key1.init(128);
        SecretKey secretKey1 = Attrib_key1.generateKey();
        System.out.println("++++++++ key1:" + secretKey1);

        long aTime = System.nanoTime();
        Encryption e1 = new Encryption();
        String encryptedtxt1 = e1.encrypt(sbd.toString(), secretKey1);
        session.setAttribute("EncryptText1", encryptedtxt1);
        long bTime = System.nanoTime();
        float encryptTime = (bTime - aTime) / 1000;
        System.out.println("Time taken to Encrypt File: " + encryptTime + " microseconds.");
        session.setAttribute("encryptTime", encryptTime);

        byte[] b1 = secretKey1.getEncoded();
        String RDkey = Base64.encode(b1);
        System.out.println("converted secretkey to string:" + RDkey);
        session.setAttribute("RDkey", RDkey);

        SplitFile splitFile = new SplitFile();
        splitFile.split(filepath + "\\" + filename);
        f1 = filepath + "\\" + filename + "1";
        f2 = filepath + "\\" + filename + "2";
        f3 = filepath + "\\" + filename + "3";

        BufferedReader br1 = new BufferedReader(new FileReader(f1));
        BufferedReader br2 = new BufferedReader(new FileReader(f2));
        BufferedReader br3 = new BufferedReader(new FileReader(f3));

        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        String temp1 = null;
        String temp2 = null;
        String temp3 = null;

        while ((temp1 = br1.readLine()) != null) {
            sb1.append(temp1);
            sb1.append("\n");
        }
        while ((temp2 = br2.readLine()) != null) {
            sb2.append(temp2);
            sb2.append("\n");
        }
        while ((temp3 = br3.readLine()) != null) {
            sb3.append(temp3);
            sb3.append("\n");
        }
        System.out.println("\n Block 1 : " + sb1.toString());
        System.out.println("\n Block 2 : " + sb2.toString());
        System.out.println("\n Block 3 : " + sb3.toString());

        session.setAttribute("ori_block1", sb1.toString());
        session.setAttribute("ori_block2", sb2.toString());
        session.setAttribute("ori_block3", sb3.toString());

        String encryptedtext1 = e.encrypt(sb1.toString(), secretKey);
        String encryptedtext2 = e.encrypt(sb2.toString(), secretKey);
        String encryptedtext3 = e.encrypt(sb3.toString(), secretKey);

%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <title>Proxy Re-Encryption Approach to Secure Data Sharing</title>
        <meta content="" name="description">
        <meta content="" name="keywords">

        <!-- Favicons -->
        <link href="assets/img/favicon.png" rel="icon">
        <link href="assets/img/apple-touch-icon.png" rel="apple-touch-icon">

        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Raleway:300,300i,400,400i,500,500i,600,600i,700,700i|Poppins:300,300i,400,400i,500,500i,600,600i,700,700i" rel="stylesheet">

        <!-- Vendor CSS Files -->
        <link href="assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="assets/vendor/icofont/icofont.min.css" rel="stylesheet">
        <link href="assets/vendor/boxicons/css/boxicons.min.css" rel="stylesheet">
        <link href="assets/vendor/owl.carousel/assets/owl.carousel.min.css" rel="stylesheet">
        <link href="assets/vendor/remixicon/remixicon.css" rel="stylesheet">
        <link href="assets/vendor/venobox/venobox.css" rel="stylesheet">
        <link href="assets/vendor/aos/aos.css" rel="stylesheet">

        <!-- Template Main CSS File -->
        <link href="assets/css/style.css" rel="stylesheet">
    </head>
    <body>

        <!-- ======= Header ======= -->
        <header id="header" class="fixed-top">
            <div class="container-fluid d-flex">

                <div class="logo mr-auto">
                    <h1 class="text-light"><a><span>Re-Encryption</span></a></h1>
                    <!-- Uncomment below if you prefer to use an image logo -->
                    <!-- <a href="index.html"><img src="assets/img/logo.png" alt="" class="img-fluid"></a>-->
                </div>

                <nav class="nav-menu d-none d-lg-block">
                    <ul>
                        <li><a href="doHome.jsp">Home</a></li>
                        <li><a style="color:#eb5d1e" href="uploadFile.jsp">Upload File</a></li>
                        <li><a href="myFiles.jsp">My Files</a></li>
                        <li><a href="requestedFiles.jsp">Requested Files</a></li>
                        <li><a href="index.jsp">Logout</a></li>
                    </ul>
                </nav><!-- .nav-menu -->

            </div>
        </header><!-- End Header -->

        <!-- ======= Hero Section ======= -->
        <section id="hero" class="d-flex align-items-center">

            <div class="container">
                <div class="row">
                    <div class="col-lg-12 pt-5 pt-lg-0 order-2 order-lg-1">
                        <br><br><h1>A Proxy Re-Encryption Approach to Secure Data Sharing in the
                            Internet of Things Based on Blockchain</h1>
                    </div>
                    <div class="col-lg-6 order-1 order-lg-2 hero-img">
                        <img src="assets/img/hero-img.svg" class="img-fluid animated" alt="">
                    </div>
                </div>
            </div>

        </section><!-- End Hero -->

        <main id="main">

            <!-- ======= About Section ======= -->
            <section id="contact" class="contact">
                <div class="container" data-aos="fade-up">
                    <center><h3>Upload File</h3></center><br>
                    <div class="row mt-5">
                        <div class="col-md-2">
                        </div>
                        <div class="col-md-9">
                            <form action="DataUpload" method="post" role="form" enctype="multipart/form-data">
                                <div class="form-group">
                                    <input type="hidden" name="keyword" value="<%=filekeyword%>" readonly="">
                                    <label>File Name :</label>
                                    <input type="text" class="form-control" name="filename" value="<%=filename%>" readonly=""><br>
                                    <label>Block 1 :</label>
                                    <textarea name="block1" readonly="" class="form-control" style="height: 120px; resize: none;"><%=encryptedtext1%></textarea><br>
                                    <label>Block 2 :</label>
                                    <textarea name="block2" readonly="" class="form-control"style="height: 120px; resize: none;"><%=encryptedtext2%></textarea><br>
                                    <label>Block 3 :</label>
                                    <textarea name="block3" readonly="" class="form-control"style="height: 120px; resize: none;"><%=encryptedtext3%></textarea><br>
                                </div>
                                <div class="form-group">
                                    <button type="submit" class="btn btn-success btn-lg">Upload</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </section><!-- End About Section -->
            <%   } catch (Exception e) {
                    e.printStackTrace();
                }
            %>
        </main><!-- End #main -->

        <!-- ======= Footer ======= -->
        <footer id="footer">
            <div class="container py-4">
                <center>&copy;  <strong><span>2024</span></strong>.</center>
            </div>
        </footer><!-- End Footer -->

        <a href="#" class="back-to-top"><i class="icofont-simple-up"></i></a>

        <!-- Vendor JS Files -->
        <script src="assets/vendor/jquery/jquery.min.js"></script>
        <script src="assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="assets/vendor/jquery.easing/jquery.easing.min.js"></script>
        <script src="assets/vendor/php-email-form/validate.js"></script>
        <script src="assets/vendor/owl.carousel/owl.carousel.min.js"></script>
        <script src="assets/vendor/waypoints/jquery.waypoints.min.js"></script>
        <script src="assets/vendor/counterup/counterup.min.js"></script>
        <script src="assets/vendor/isotope-layout/isotope.pkgd.min.js"></script>
        <script src="assets/vendor/venobox/venobox.min.js"></script>
        <script src="assets/vendor/aos/aos.js"></script>

        <!-- Template Main JS File -->
        <script src="assets/js/main.js"></script>

    </body>

</html>
