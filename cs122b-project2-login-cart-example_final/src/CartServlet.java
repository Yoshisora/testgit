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
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {


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
        for(String title: mycart.keySet()){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", title);
            jsonObject.addProperty("quantity", mycart.get(title));
            jsonArray.add(jsonObject);
        }

        //JsonArray previousItemsJsonArray = new JsonArray();
        //previousItems.forEach(previousItemsJsonArray::add);


        response.getWriter().write(jsonArray.toString());
        response.setStatus(200);


    }
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
}