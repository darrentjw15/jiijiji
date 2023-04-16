// To save as "ebookshop\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;            // Tomcat 10
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
//import javax.servlet.*;            // Tomcat 9
//import javax.servlet.http.*;
//import javax.servlet.annotation.*;

@WebServlet("/query")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class QueryServlet extends HttpServlet {

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
      out.println(".link {color: #fff;text-decoration: none;} .link:hover {color: #00FF00;} .view {color:yellow;text-decoration: none;} .view:hover {color: #00FF00;}");
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
         // Step 3: Execute a SQL SELECT query
         String sqlStr2 = "select * from weeklist where week_id = '" + request.getParameter("date") + "'";
         ResultSet rset2 = stmt.executeQuery(sqlStr2);
         rset2.next();
         out.println("<h2>Spotify Singapore Top 200 Chart&nbsp&nbsp&nbsp|&nbsp&nbsp&nbspWeek of " + rset2.getString("week_name") + "<h2>");

         String sqlStr = "select * from " + request.getParameter("date") + " order by pos asc";
         ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server

         out.println("<center><table>");
         out.println("<tr>");
         //out.println("<th width=5%></th>");
         out.println("<th text-align='center';font-size:20px;>Pos</th>");
         out.println("<th text-align='center';font-size:20px;>+/-</th>");
         out.println("<th></th>");
         out.println("<th style=font-size:20px;>Artist(s)</th>");
         out.println("<th style=font-size:20px;>Title</th>");
         out.println("<th style=font-size:20px;>Streams</th>");
         out.println("<th style=font-size:20px;>Streams+</th>");
         out.println("<th style=font-size:20px;>Weeks On</th>");
         out.println("<th style=font-size:20px;>Peak Pos</th>");
         out.println("<th style=font-size:20px;>Peak Streams</th>");
         out.println("<th style=font-size:20px;>Year</th>");
         out.println("<th style=font-size:20px;>Length</th>");
         out.println("</tr>");
         while(rset.next()) {
            out.println("<tr>");
            //out.println("<td><input type='checkbox' name='id' value='" + rset.getString("id") + "'/></td>");
            out.println("<td style=text-align:'center'>" + rset.getString("pos") + "</td>");
            if (rset.getString("pos_change").contains("+")) {
                out.println("<td class='pos_change' style=text-align:center;color:#5FEC2F;>" + rset.getString("pos_change") + "</td>");
            } else if (rset.getString("pos_change").contains("-")) {
                out.println("<td class='pos_change' style=text-align:center;color:red;>" + rset.getString("pos_change") + "</td>");
            } else if (rset.getString("pos_change").contains("NEW")) {
                out.println("<td class='pos_change' style=text-align:center;color:yellow;>" + rset.getString("pos_change") + "</td>");
            } else if (rset.getString("pos_change").contains("RE")) {
                out.println("<td class='pos_change' style=text-align:center;color:yellow;>" + rset.getString("pos_change") + "</td>");
            } else {
                out.println("<td class='pos_change' style=text-align:center;color:grey;>=</td>");
            }
            out.println("<td><img src=" + "'https://i.scdn.co/image/" + rset.getString("image_url") + "'" + " alt=" + "''" + " height=75 width=75 /></td>");
            out.println("<td style=font-size:20px;>" + rset.getString("artists") + "</td>");
            out.println("<td style=font-size:20px;><a href='track?track_id=" + rset.getString("track_id") + "' class='link'>" + rset.getString("title") + "</td>");
            out.println("<td style=font-size:20px;>" + rset.getString("streams") + "</td>");
            if (rset.getString("streams_change").contains("nan")) {
                out.println("<td style=font-size:20px;>" + "</td>");
            } else if (rset.getString("streams_change").contains("+")) {
                out.println("<td style=font-size:20px;color:#5FEC2F;>" + rset.getString("streams_change") + "</td>");
            } else if (rset.getString("streams_change").contains("-")) {
                out.println("<td style=font-size:20px;color:red;>" + rset.getString("streams_change") + "</td>");
            } else {
                out.println("<td style=font-size:20px;color:grey;>" + rset.getString("streams_change") + "</td>");
            }
            out.println("<td style=font-size:20px;>" + rset.getString("weeks_on") + "</td>");
            out.println("<td style=font-size:20px;>" + rset.getString("peak_pos") + "</td>");
            out.println("<td style=font-size:20px;>" + rset.getString("peak_streams") + "</td>");
            out.println("<td style=font-size:20px;>" + rset.getString("year") + "</td>");
            out.println("<td style=font-size:20px;>" + rset.getString("song_length") + "</td>");
            out.println("<td style=font-size:20px;color:green;><a href='https://open.spotify.com/artist/" + rset.getString("artist_id") + "' class='view'>View Artist</td>");
            out.println("<td style=font-size:20px;color:green;><a href='https://open.spotify.com/track/" + rset.getString("track_id") + "' class='view'>View Track</td>");
            out.println("</tr>");
         }
         out.println("</table></center>");
      } catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
      out.println("</body></html>");
      out.close();
   }
}