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
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
@WebServlet(name = "ConfirmServlet", urlPatterns = "/api/confirmation")
public class ConfirmServlet extends HttpServlet {


    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Map<String, Integer> mycart = (Map<String, Integer>) session.getAttribute("cart");

        JsonArray jsonArray = new JsonArray();


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Generate a SQL query
            //String query = String.format("SELECT count(*) as c from creditcards where firstName='%s' and lastName='%s' and expiration='%s' and id = '%s'", firstname, lastname, expdate, creditcard);


            // Perform the query
            //ResultSet rs = statement.executeQuery(query);

            String salequery = "select id from sales order by id desc limit "+ mycart.keySet().size();

            // Log to localhost log
            request.getServletContext().log("query：" + salequery);
            ResultSet rs = statement.executeQuery(salequery);
            List<String> titles = new ArrayList<String>(mycart.keySet());
            Collections.reverse(titles);

            for(String title: titles){
                JsonObject jsonObject = new JsonObject();
                rs.next();
                jsonObject.addProperty("saleid", rs.getInt("id"));
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("quantity", mycart.get(title));


                jsonArray.add(jsonObject);
            }

            //clear cart
            session.setAttribute("cart", null);

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


        //JsonArray previousItemsJsonArray = new JsonArray();
        //previousItems.forEach(previousItemsJsonArray::add);


        response.getWriter().write(jsonArray.toString());
        response.setStatus(200);


    }
    /*
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movietitle = request.getParameter("title");
        String mode = request.getParameter("mode");
        System.out.println(movietitle);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        Map<String, Integer> mycart = (Map<String, Integer>) session.getAttribute("cart");
        if(mode.equals("increase")) {
            if (mycart == null) {
                mycart = new HashMap<>();
                mycart.put(movietitle, 1);
                session.setAttribute("cart", mycart);
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (mycart) {
                    if (mycart.containsKey(movietitle)) {
                        Integer currentnum = mycart.get(movietitle);
                        mycart.put(movietitle, currentnum + 1);
                    } else {
                        mycart.put(movietitle, 1);
                    }
                }
            }
        }

        else if(mode.equals("decrease")){
            synchronized (mycart) {
                Integer currentnum = mycart.get(movietitle);
                if(currentnum == 1){
                    mycart.remove(movietitle);
                }
                else{
                    mycart.put(movietitle, currentnum - 1);
                }
            }
        }
        else{
            synchronized (mycart) {
                mycart.remove(movietitle);
            }
        }

        JsonObject responseJsonObject = new JsonObject();

        //JsonArray previousItemsJsonArray = new JsonArray();
        //previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.addProperty("dictlength", mycart.size());

        response.getWriter().write(responseJsonObject.toString());
    }

     */
}