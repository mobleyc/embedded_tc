package cpm;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class Runner {

    public static void main(String[] args) throws Exception {
        String appBaseDir = getAppBase(args);
        int port = getPort(args);
        String contextPath = "/";

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        String baseDir = new File(appBaseDir).getAbsolutePath();
        StandardContext ctx = (StandardContext) tomcat.addWebapp(contextPath, baseDir);

        Tomcat.addServlet(ctx, "hello", new HttpServlet() {
            protected void service(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                Writer w = resp.getWriter();
                w.write("Hello, World!");
                w.flush();
            }
        });
        ctx.addServletMapping("/*", "hello");

        StandardHost vhost = (StandardHost) tomcat.getHost();
        AccessLogValve alValve = new AccessLogValve();
        alValve.setDirectory("logs");
        alValve.setRotatable(true);
        alValve.setPrefix("access_");
        alValve.setSuffix(".log");
        // ref: http://tomcat.apache.org/tomcat-6.0-doc/api/org/apache/catalina/valves/AccessLogValve.html
        alValve.setPattern("%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\" %D");
        alValve.setEnabled(true);
        vhost.addValve(alValve);

        tomcat.start();
        tomcat.getServer().await();
    }

    private static String getAppBase(String[] args) {
        final String DEFAULT = "src/main/webapp/";

        if(0 == args.length) {
            return DEFAULT;
        }

        if(null == args[0] || args[1].isEmpty()) {
            return DEFAULT;
        }

        return args[0];
    }

    private static int getPort(String[] args) {
        final int DEFAULT = 8080;

        if(args.length < 2) {
            return DEFAULT;
        }

        if(null == args[1] || args[1].isEmpty()) {
            return DEFAULT;
        }

        return Integer.valueOf(args[1]);
    }

}
