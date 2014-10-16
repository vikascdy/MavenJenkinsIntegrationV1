<!DOCTYPE HTML>
<html>
<head>

    <%

        final String ROOT_PATH = "";

        String JSON_SERVLET_PATH = (String) application.getAttribute("JSON_SERVLET_PATH");

//        For Development Using Proxy Servlet
//        JSON_SERVLET_PATH = JSON_SERVLET_PATH == null ? "/proxy/rest/service/Dashboard-Service/" : JSON_SERVLET_PATH;

//        For Production Without Using Proxy Servlet
        JSON_SERVLET_PATH = JSON_SERVLET_PATH == null ? "/rest/service/Dashboard Service/" : JSON_SERVLET_PATH;

    %>

    <meta charset="UTF-8">
    <title>Edifecs&reg; Dashboard Designer</title>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <!-- <x-compile> -->
    <!-- <x-bootstrap> -->
    <link rel="stylesheet" type="text/css" href="resources/css/main.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/colorpicker.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/icons.css"/>

    <link rel="stylesheet" type="text/css" href="../packages/ext-theme-edifecs/resources/css/baseLayout.css"/>
    <link rel="stylesheet" type="text/css" href="../packages/ext-theme-edifecs/resources/css/statusIcons.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/dashboardHeader.css"/>
    <link rel="stylesheet" type="text/css" href="../packages/ext-theme-edifecs/build/resources/css/actionIcons.css"/>
    <link rel="stylesheet" href="bootstrap.css">

<%--        For Development      --%>
    <%--<script src="../ext/ext-all-debug.js"></script>--%>

<%--        For Production       --%>
    <script src="../ext/ext-all.js"></script>

    <script src="bootstrap.js"></script>

    <!--jsBeautify CSS and JS for code formatting-->
    <script type="text/javascript" src="resources/jsBeautify/beautify.js"></script>

    <!--JSON minify-->
    <script type="text/javascript" src="resources/js/minify.json.js"></script>

    <!--Path.js for Navigation-->
    <script type="text/javascript" src="resources/js/path.js"></script>

    <!--Scaffolding Model-->
    <script type="text/javascript" src="resources/js/bancha-scaffold-debug.js"></script>

    <!-- </x-bootstrap> -->
    <script src="app.js"></script>
    <!-- </x-compile> -->


    <script src="resources/src-min-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>
    <style type="text/css" media="screen">
        #editor {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
        }
    </style>


    <script type="text/javascript">
        var JSON_SERVLET_PATH = '<%= JSON_SERVLET_PATH %>';
    </script>

</head>
<body>
<div id="site-loading">
    <img src="resources/images/site-loading.gif" alt="Loading..."/>
    <p>Loading Dashboard Designer...</p>
</div>
</body>
</html>
