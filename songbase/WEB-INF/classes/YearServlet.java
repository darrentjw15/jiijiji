// To save as "ebookshop\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;            // Tomcat 10
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
//import javax.servlet.*;            // Tomcat 9
//import javax.servlet.http.*;
//import javax.servlet.annotation.*;

@WebServlet("/year")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class YearServlet extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      // Print an HTML page as the output of the query
      out.println("<html>");
      out.println("<head><title>Query Response</title></head>");
      out.println("<style>");
      out.println("header {background-color: #333;color: #fff;padding: 20px;display: flex;justify-content: space-between;align-items: center;}");
      out.println("body {background-color:#2F2F2F;}");
      out.println(".link {color: #fff;text-decoration: none;} .link:hover {color: #00FF00;}");
      out.println("h2 {color:white;}");
      out.println("th {color:white;}");
      out.println("td:not(.pos_change) {color:white;}");
      out.println("</style>");
      out.println("<body><header>");
      out.println("<img src='logo.png' alt='Logo' class='logo'>");
      out.println("<a href='songbase_search.html' class='link'>Home</a>");
	  out.println("</header>");

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/songbase?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
      ) {
            String year = request.getParameter("yearly");
            String[] strArray = new String[52];
            int arrayPoint = 0;
            // Step 3: Execute a SQL SELECT query
            String sqlStr = "select * from weeklist";
            ResultSet rset = stmt.executeQuery(sqlStr);
            while (rset.next()) {
                if (rset.getString("week_id").contains(year)) {
                    strArray[arrayPoint] = rset.getString("week_id");
                    arrayPoint += 1;
                }
            }
            out.println("<center><table><tr><th>Week</th><th>No. 1 Single</th><th>Peak Streams</th></tr>");
            for (int i = 0; i < strArray.length; i++) {
                String sqlStr2 = "select * from weeklist where week_id = '" + strArray[i] + "'";
                ResultSet rset2 = stmt.executeQuery(sqlStr2);
                rset2.next();
                out.println("<tr><td><a href=query?date=" + rset2.getString("week_id") + " class='link'>" + rset2.getString("week_name") + "</td>");
                String week_id = strArray[i];
                String sqlStr4 = "select * from " + week_id + " where pos = 1";
                ResultSet rset4 = stmt.executeQuery(sqlStr4);
                rset4.next();
                out.println("<td>" + rset4.getString("artists") + " - " + rset4.getString("title") + "</td>");
                out.println("<td style=text-align:center;>" + rset4.getString("streams") + "</td></tr>");
            }
      } catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
      out.println("</center></body></html>");
      out.close();
   }
}