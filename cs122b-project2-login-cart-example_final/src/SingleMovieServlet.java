import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            //String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                    //"where m.id = sim.movieId and sim.starId = s.id and s.id = ?";
            String query = "SELECT m.id, m.title, m.year, m.director, r.rating, s.name as starname, s.id as sid,  g.name as genrename from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r where m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId and m.id = ?";
            //String grp = " group by s.id order by count(m.id)";
            //String subtable = ", (select s.id as sid, s.name, count(m.id) as cnt from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id group by s.id;) as cnts";
            //String subwhere = " and cnts.sid = s.id";
            String qwithcnt = "SELECT m.id, m.title, m.year, m.director, r.rating, s.name as starname, s.id as sid,  g.name as genrename, cnts.cnt from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r, (select s.id as sid, s.name, count(m.id) as cnt from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id group by s.id) as cnts where m.id = sim.movieId and sim.starId = s.id and cnts.sid = s.id and m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId and m.id = ? group by s.id, m.id,r.rating, g.name order by cnts.cnt desc, s.name asc;";


            //String q = "SELECT m.id, m.title, m.year, m.director, r.rating, s.name as starname, s.id as sid,  g.name as genrename, count(m.id) as cnt from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r" + subtable+ " where m.id = sim.movieId and sim.starId = s.id"+subwhere+" and m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId group by s.id, m.id,r.rating, g.name order by count(m.id);";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(qwithcnt);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            System.out.println("70");
            Map<String, Map<String,String>> moviemap = new HashMap<>();
            // Iterate through each row of rs
            while (rs.next()) {
                System.out.println("73");
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_star = rs.getString("starname");
                String movie_genre = rs.getString("genrename");
                String star_id = rs.getString("sid");
                // Create a JsonObject based on the data we retrieve from rs
                if (moviemap.containsKey(movie_id) == false) {
                    System.out.println("87");
                    Map<String, String> info = new HashMap<>();
                    info.put("movie_title", movie_title);
                    info.put("movie_year", movie_year);
                    info.put("movie_director", movie_director);
                    info.put("movie_rating", movie_rating);
                    info.put("movie_stars", movie_star);
                    info.put("movie_genres", movie_genre);
                    info.put("star_ids", star_id);

                    moviemap.put(movie_id, info);
                    System.out.println("95");
                } else {
                    Map<String, String> info_dict = moviemap.get(movie_id);
                    String stars = info_dict.get("movie_stars");
                    if (!stars.contains(movie_star)) {

                        stars += "," + movie_star;
                    }
                    info_dict.put("movie_stars", stars);
                    String starids = info_dict.get("star_ids");
                    if (!starids.contains(star_id)) {

                        starids += "," + star_id;
                    }
                    info_dict.put("star_ids", starids);
                    String genres = info_dict.get("movie_genres");
                    if (!genres.contains(movie_genre)) {

                        genres += "," + movie_genre;
                    }
                    info_dict.put("movie_genres", genres);

                }
            }
            JsonObject jsonObject = new JsonObject();
            Map<String,String> info_dict = moviemap.get(id);
            System.out.println(info_dict.get("movie_stars"));
            jsonObject.addProperty("movie_id", id);
            jsonObject.addProperty("movie_title", info_dict.get("movie_title"));
            jsonObject.addProperty("movie_year", info_dict.get("movie_year"));
            jsonObject.addProperty("movie_director", info_dict.get("movie_director"));
            jsonObject.addProperty("movie_rating", info_dict.get("movie_rating"));
            jsonObject.addProperty("movie_stars", info_dict.get("movie_stars"));
            jsonObject.addProperty("movie_genres", info_dict.get("movie_genres"));
            jsonObject.addProperty("star_ids", info_dict.get("star_ids"));



            jsonArray.add(jsonObject);

            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}