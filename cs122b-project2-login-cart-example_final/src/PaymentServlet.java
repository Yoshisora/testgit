import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.time.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import javax.xml.transform.Result;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

    //new code
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //String item = request.getParameter("item");
        //System.out.println(item);
        HttpSession session = request.getSession();

        // get the total payment from session;
        Map<String, Integer> mydict = (Map<String, Integer>)session.getAttribute("cart");
        Integer count = 0;
        synchronized (mydict)
        {
            for (Integer quan : mydict.values())
            {
                count = count + quan;
            }
            count = count *5;
        }


        JsonObject responseJsonObject = new JsonObject();

        responseJsonObject.addProperty("total", count);

        response.getWriter().write(responseJsonObject.toString());
    }




    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String creditcard = request.getParameter("creditcard");
        String expdate = request.getParameter("expdate");

        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        Integer cid = user.userid;
        Map<String, Integer> mydict = (Map<String, Integer>)session.getAttribute("cart");
        //String date = "2023-04-30";
        LocalDate today = LocalDate.now();
        String date = today.toString();
        JsonObject responseJsonObject = new JsonObject();

        //new code

        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Generate a SQL query
            String query = String.format("SELECT count(*) as c from creditcards where firstName='%s' and lastName='%s' and expiration='%s' and id = '%s'", firstname, lastname, expdate, creditcard);

            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);


            rs.next();
            Integer count = rs.getInt("c");
            if (count > 0)
            {

                //insert into sales
                String insert = "insert into sales(customerId, movieId, saleDate, quantity) values (";
                //updatequery = insert + cid + ",'";
                synchronized (mydict){
                    if(mydict!=null && !mydict.isEmpty())
                    {
                        for (String movie : mydict.keySet())
                        {
                            String idquery = "select id from movies where title='"+movie+"' limit 1";
                            ResultSet rs2 = statement.executeQuery(idquery);

                            rs2.next();
                            String mid = rs2.getString("id");
                            rs2.close();

                            String updatequery = insert + cid + ",'"+mid+"','"+date+"',"+mydict.get(movie)+")";
                            Integer up = statement.executeUpdate(updatequery);

                        }
                    }
                }



                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");


            }
            else
            {

                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Payment failed");
                responseJsonObject.addProperty("message", "Payment failed: invalid credit card information");

            }


            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);
            // Output Error Massage to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
            return;
        }


        response.getWriter().write(responseJsonObject.toString());
    }
}
