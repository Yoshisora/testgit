import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.sql.PreparedStatement;
import java.util.*;
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




// Declaring a WebServlet called Movies_Servlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            //HttpSession session = request.getSession();
            //Map<String, String> pricedict = (Map<String, String>) session.getAttribute("pricedict");
            //System.out.println("HelloWorld");

            // Declare our statement
            Statement statement = conn.createStatement();
            /*
            String query2 = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                    "where m.id = sim.movieId and sim.starId = s.id and m.id = ? LIMIT 3;";

            String query3 = "SELECT * from genres as g, genres_in_movies as gim, movies as m " +
                    "where m.id = gim.movieId and gim.genreId = g.id and m.id = ? LIMIT 3;";*/


            String query1 = "SELECT m.id, m.title, m.year, m.director, r.rating, s.name as starname, s.id as sid,  g.name as genrename from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r where m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId ORDER BY r.rating DESC LIMIT 1000";

            //p2 stuff
            String selectfrom = "SELECT m.id, m.title, m.year, m.director, r.rating, s.name as starname, s.id as sid,  g.name as genrename from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r ";
            String fromcnts = ", (select s.id as sid, s.name, count(m.id) as cnt from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id group by s.id) as cnts ";
            String where = " WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId and cnts.sid = s.id ";
            String searchstr = "";
            String tit = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String starname = request.getParameter("starname");

            // GETPARAMETER PROBLEM
            //String titleinit = "s";
            String titleinit = request.getParameter("titleinit");
            String genre = request.getParameter("genre");

            System.out.println(titleinit);


            if (tit != null && !tit.isEmpty())
            {
                searchstr = searchstr + "and m.title like '%"+tit+"%' ";
            }
            if (year != null && !year.isEmpty())
            {
                searchstr = searchstr + "and m.year = '"+year+"' ";
            }
            if (director != null && !director.isEmpty())
            {
                searchstr = searchstr + "and m.director like '%"+director+"%' ";
            }

            if (titleinit != null && !titleinit.isEmpty())
            {
                if (titleinit.equals("*"))
                {
                    searchstr = searchstr + "and m.title regexp '" + "^[^a-zA-Z0-9]' ";
                }
                else {
                    searchstr = searchstr + "and m.title like '"+titleinit+"%' ";
                }
            }


            /*
            if (!starname.isEmpty())
            {
                searchstr = searchstr + "and s.name = '"+starname+"' ";
            }

             */


            String order = " order by cnts.cnt desc, s.name asc";
            String Searchquery = selectfrom + fromcnts + where + searchstr + order;



            // Perform the query
            //ResultSet rs = statement.executeQuery(query1);
            ResultSet rs = statement.executeQuery(Searchquery);

            JsonArray jsonArray = new JsonArray();
            List<String> mylist = new ArrayList<>(20);
            //Random randomGenerator = new Random();
            // Iterate through each row of rs
            Map<String, Map<String,String>> moviemap = new HashMap<>();
            while (rs.next()) {
                System.out.println("HelloWorldgg");
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_star = rs.getString("starname");
                String movie_genre = rs.getString("genrename");
                String star_id = rs.getString("sid");
                System.out.println("83");
                if(!mylist.contains(movie_id))
                {
                mylist.add(movie_id);}


                if(moviemap.containsKey(movie_id) == false){
                    System.out.println("87");
                    Map<String,String> info = new HashMap<>();
                    info.put("movie_title", movie_title);
                    info.put("movie_year", movie_year);
                    info.put("movie_director", movie_director);
                    info.put("movie_rating", movie_rating);
                    info.put("movie_stars", movie_star);
                    info.put("movie_genres", movie_genre);
                    info.put("star_ids", star_id);
                    /*
                    HttpSession session = request.getSession();
                    Map<String, String> pricedict = (Map<String, String>) session.getAttribute("pricedict");
                    */
                    //Random randomGenerator = new Random();
                    //Integer randomnumber = randomGenerator.nextInt(51) + 50;
                    //String myrandom = Integer.toString(randomnumber);
                    /*
                    if(movie_title.equals("Mata Hari")){
                        System.out.println("The fking price of this film is ");
                        System.out.println(myrandom);
                    }
                    if (pricedict == null){
                        pricedict = new HashMap<>();
                        pricedict.put(movie_title, myrandom);
                        session.setAttribute("price_dict", pricedict);
                        System.out.println("175");
                    }
                    else{
                        synchronized (pricedict){
                            pricedict.put(movie_title, myrandom);
                            System.out.println("180");
                        }
                    }*/



                    moviemap.put(movie_id, info);
                    System.out.println("95");
                }
                else{
                    Map<String,String> info_dict = moviemap.get(movie_id);
                    String stars = info_dict.get("movie_stars");
                    if (!stars.contains(movie_star))
                    {

                        stars += "," + movie_star;
                    }
                    info_dict.put("movie_stars", stars);
                    String starids = info_dict.get("star_ids");
                    if (!starids.contains(star_id))
                    {

                        starids += "," + star_id;
                    }
                    info_dict.put("star_ids", starids);
                    String genres = info_dict.get("movie_genres");
                    if (!genres.contains(movie_genre))
                    {

                        genres += "," + movie_genre;
                    }
                    info_dict.put("movie_genres", genres);

                }
                System.out.println("106");


                // Create a JsonObject based on the data we retrieve from rs

            }
            /*
            JsonObject myjsonObject = new JsonObject();
            HttpSession thesession = request.getSession();
            Map<String, String> thedict = (Map<String, String>) thesession.getAttribute("price_dict");

            myjsonObject.addProperty("price_dict", thedict.get("Mata Hari"));
            jsonArray.add(myjsonObject);
            */

            System.out.println("Helloworld3");
            //String tempstar;
            for (String movie_id : mylist){
            //for (String movie_id : moviemap.keySet()){
                Map<String,String> info_dict = moviemap.get(movie_id);
                //tempstar = info_dict
                if (starname != null && !starname.isEmpty())
                {
                    if (!info_dict.get("movie_stars").toLowerCase().contains(starname.toLowerCase()))
                    {
                        continue;
                    }
                }
                if (genre != null && !genre.isEmpty())
                {
                    if (!info_dict.get("movie_genres").toLowerCase().contains(genre.toLowerCase()))
                    {
                        continue;
                    }
                }
                JsonObject jsonObject = new JsonObject();
                //System.out.println(info_dict.get("movie_stars"));
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", info_dict.get("movie_title"));
                jsonObject.addProperty("movie_year", info_dict.get("movie_year"));
                jsonObject.addProperty("movie_director", info_dict.get("movie_director"));
                jsonObject.addProperty("movie_rating", info_dict.get("movie_rating"));
                jsonObject.addProperty("movie_stars", info_dict.get("movie_stars"));
                jsonObject.addProperty("movie_genres", info_dict.get("movie_genres"));
                jsonObject.addProperty("star_ids", info_dict.get("star_ids"));



                jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String move = request.getParameter("moveforward");
        System.out.println(move);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        Integer pagenumber =  (Integer) session.getAttribute("page_number");
        if(pagenumber == null){
            pagenumber = 1;
        }

        if(move != null){
            synchronized (pagenumber) {
                if (move.equals("true")) {
                    pagenumber++;
                } else {
                    pagenumber--;
                }
            }
        }
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("page_number", pagenumber);
        response.getWriter().write(responseJsonObject.toString());
        /*
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<String>();
            previousItems.add(item);
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                previousItems.add(item);
            }
        }

        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());

        */
    }
}