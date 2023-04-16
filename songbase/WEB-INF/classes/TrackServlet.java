// To save as "ebookshop\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;            // Tomcat 10
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
//import javax.servlet.*;            // Tomcat 9
//import javax.servlet.http.*;
//import javax.servlet.annotation.*;

@WebServlet("/track")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class TrackServlet extends HttpServlet {

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
      //out.println("body {background-color:#2F2F2F;}");

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/songbase?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
      ) {
            String track_id = request.getParameter("track_id");
            String sqlStr3 = "select * from trackbase where track_id = '" + track_id + "'";
            ResultSet rset3 = stmt.executeQuery(sqlStr3);
            rset3.next();
            out.println("header {background-color: #333;color: #fff;padding: 20px;display: flex;justify-content: space-between;align-items: center;}");
            out.println(".link {color: #fff;text-decoration: none;} .link:hover {color: #00FF00;}");
            out.println("body {background-color:rgba(47, 47, 47, 1);background-image:linear-gradient(to bottom, rgba(255, 255, 255, 0.3), rgba(47, 47, 47, 1)),url('https://i.scdn.co/image/" + rset3.getString("image_url") + "');height: 1500px;background-size: cover;background-position: center;background-repeat:no-repeat;color: black;padding: 50px;}");
            out.println("h2 {color:white;}");
            out.println("h3 {color:white;}");
            out.println("p {color:white;}");
            out.println("th {color:white;}");
            out.println("a {color:white;}");
            out.println("td:not(.pos_change) {color:white;}");
            out.println("</style>");
            out.println("<body><header>");
            out.println("<img src='logo.png' alt='Logo' class='logo'>");
		    out.println("<a href='songbase_search.html' class='link'>Home</a>");
	        out.println("</header>");
            out.println("<br><br><center><img src='https://i.scdn.co/image/" + rset3.getString("image_url") + "' alt='' height=500 width=500 />");
            out.println("<h2>" + rset3.getString("title") + "</h2>");
            out.println("<h3>" + rset3.getString("artists") + "</h3>");
            out.println("<p>" + rset3.getString("year") + " | " + rset3.getString("song_length") + "</p>");
            String peak_pos = rset3.getString("peak_pos");
            String peak_streams = rset3.getString("peak_streams");
            out.println("<p>" + rset3.getString("weeks_on") + " Weeks | Peak: " + rset3.getString("peak_pos") + " (" + rset3.getString("peak_streams") + ")</p>");
            out.println("<table><tr>");
            out.println("<th text-align='center';font-size:20px;>Week</th>");
            out.println("<th text-align='center';font-size:20px;>Pos</th>");
            out.println("<th text-align='center';font-size:20px;>Streams</th></tr>");
            String sqlStr = "select * from weeklist";
            ResultSet rset = stmt.executeQuery(sqlStr);
            int total_weeks = 1;
            while (rset.next()) {
                total_weeks = Integer.parseInt(rset.getString("week_num"));
            }
            boolean b = false;
            for (int i = 1; i <= total_weeks; i++) {
                String sqlStr2 = "select week_id from weeklist where week_num = " + i;
                ResultSet rset2 = stmt.executeQuery(sqlStr2);
                rset2.next();
                String week_id = rset2.getString("week_id");
                String sqlStr4 = "select * from " + week_id;
                ResultSet rset4 = stmt.executeQuery(sqlStr4);
                boolean track_present = false;
                while (rset4.next()) {
                    if (track_id.equals(rset4.getString("track_id"))) {
                        track_present = true;
                        b = true;
                        out.println("<tr><td><a href='query?date=" + rset4.getString("date").replace("/", "_") + "' class='link'>" + rset4.getString("date") + "</a></td>");
                        if (rset4.getString("pos").equals(peak_pos)) {
                            out.println("<td><b>" + rset4.getString("pos") + "</b></td>");
                        } else {
                            out.println("<td>" + rset4.getString("pos") + "</td>");
                        }
                        if (rset4.getString("streams").equals(peak_streams)) {
                            out.println("<td><b>" + rset4.getString("streams") + "</b></td></tr>");
                        } else {
                            out.println("<td>" + rset4.getString("streams") + "</td></tr>");
                        }
                    }
                }
                if (track_present == false && b == true) {
                    out.println("<tr><td>--------------</td><td>------</td><td>--------</td></tr>");
                    b = false;
                }
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