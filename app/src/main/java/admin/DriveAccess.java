/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import java.io.File;
import java.util.StringTokenizer;

import ro.polak.http.ServerConfig;
import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;
import ro.polak.utilities.Utilities;

public class DriveAccess extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        HTMLDocument doc = new HTMLDocument("Drive Access");
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>Drive Access</h1></div>");

        if (!AccessControl.getConfig(serverConfig).get("_managementEnableDriveAccess").equals("On")) {
            doc.writeln("<div class=\"alert alert-warning\" role=\"alert\">Drive Access option has been disabled in configuration.</div>");
            doc.writeln("<p>See <b>httpd.conf</b>, parameter <b>_managementEnableDriveAccess</b> must be <b>On</b>.</p>");
            response.getPrintWriter().print(doc.toString());
            return;
        }

        File[] roots = File.listRoots();

        /* checking if ? in string */
        if (!request.getQueryString().equals("")) {
            doc.write("<p>Drive: ");
            for (int i = 0; i < roots.length; i++) {
                doc.writeln("<a href=\"/admin/DriveAccess.dhtml?" + roots[i].getAbsolutePath() + "\"><b>" + roots[i].getAbsolutePath().charAt(0) + "</b></a> ");
            }
            doc.writeln("</p>");

            String path = request.getQueryString();

            doc.writeln("<ol class=\"breadcrumb\">");
            StringTokenizer st = new StringTokenizer(path.replace('\\', '/'), "/");
            String currentPath = "";
            String token;
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                currentPath += token + "/";
                doc.writeln("<li><a href=\"/admin/DriveAccess.dhtml?" + currentPath + "\">" + token + "</a></li>");
            }

            // if( roots.length == 1 && roots[0] != null && path.length() == 0 )
            // {
            // response.sendRedirect("/admin/DriveAccess.dhtml?"+roots[0].getAbsolutePath());
            // }

            doc.writeln("</ol>");

            File f = new File(path);

            if (f.exists() && f.isDirectory()) {
                StringBuilder files = new StringBuilder();
                StringBuilder directories = new StringBuilder();
                String fileNames[] = f.list();
                File f2;
                if (fileNames == null) {
                    doc.writeln("<div class=\"alert alert-danger\" role=\"alert\"><strong>Oh snap!</strong> Unable to read files.</div>");
                } else {
                    for (int i = 0; i < fileNames.length; i++) {
                        f2 = new File(path + fileNames[i]);
                        if (f2.isDirectory()) {
                            directories
                                    .append("<p class=\"filemanager\"><img src=\"/assets/img/folder.png\" alt=\"folder\" /> <a href=\"/admin/DriveAccess.dhtml?"
                                            + Utilities.URLEncode(path
                                            + fileNames[i])
                                            + "/\">"
                                            + fileNames[i] + "</a></p>");
                        } else {
                            files.append("<p class=\"filemanager\"><img src=\"/assets/img/file.png\" alt=\"file\" /> <a href=\"/admin/GetFile.dhtml?"
                                    + Utilities.URLEncode(path + fileNames[i])
                                    + "\">"
                                    + fileNames[i]
                                    + "</a> "
                                    + Utilities.fileSizeUnits(f2.length())
                                    + "</p>");
                        }
                    }
                }
                doc.write(directories.toString());
                doc.write(files.toString());

            } else {
                doc.writeln("<div class=\"alert alert-danger\" role=\"alert\"><strong>Oh snap!</strong> Path does not exist or drive not mounted.</div>");
            }

        } else {
            for (int i = 0; i < roots.length; i++) {
                doc.writeln("<p class=\"filemanager\"><img src=\"/assets/img/drive.png\" alt=\"drive\" /> <a href=\"/admin/DriveAccess.dhtml?"
                        + roots[i].getAbsolutePath()
                        + "\"><b>"
                        + roots[i].getAbsolutePath().charAt(0)
                        + "</b> "
                        + (roots[i].getTotalSpace() / (1024 * 1024))
                        + " MB</a></p>");
            }
        }

        response.getPrintWriter().print(doc.toString());
    }
}
