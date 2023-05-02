function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
function Checkout(){

    window.location.href = "cart.html";
}
function addmovie(movie_title){
    $.ajax("api/cart", {
        method: "POST",
        data: "title=" + movie_title + "&mode=increase",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log("The current dict length is: ", resultDataJson["dictlength"]);
            //Popup.classList.add("show");
        }
    });
    window.alert("successfully added to cart");
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");
    let homepage = jQuery("#home_page1")
    homepage.append("<p>" + "Fabflix&nbsp;&nbsp;&nbsp;&nbsp;" + '<a href="index.html">Home</a>'+"&nbsp;&nbsp;&nbsp;&nbsp;Top20");
    // append two html <p> created to the h3 body, which will refresh the page

    movieInfoElement.append("<p>" + resultData[0]["movie_title"] + " (" + resultData[0]["movie_year"] + ")" + "</p>");

    console.log("handleResult: populating movie table2 from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body2");
    const Moviestars = resultData[0]["movie_stars"].split(",");
    const starids = resultData[0]["star_ids"].split(",");
    const Moviegenres = resultData[0]["movie_genres"].split(",");
    // Concatenate the html tags with resultData jsonObject to create table rows

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";
    //rowHTML += "<th>" + resultData[0]["movie_genres"] + "</th>";
    let stars_string = "<th>";
    let index = 0;
    Moviegenres.sort();
    for(index = 0; index < Moviegenres.length; index++){
        stars_string += '<a href="movies.html?genre=' + Moviegenres[index] + '">'
            + Moviegenres[index] +     // display star_name for the link text
            '</a>';
        stars_string += ",";
    }
    stars_string = stars_string.substring(0, stars_string.length-1);

    stars_string += "</th> <th>";
    for(index = 0; index < Moviestars.length; index++){
        stars_string += '<a href="single-star.html?id=' + starids[index] + '">'
            + Moviestars[index] +     // display star_name for the link text
            '</a>';
        stars_string += ",";
    }
    stars_string = stars_string.substring(0, stars_string.length-1);

    stars_string += "</th>";
    rowHTML += stars_string;
    rowHTML += "<th>" + resultData[0]["movie_rating"] + "</th>";
    rowHTML += "<th>" + '<button type = "button" onclick=\"addmovie(\'' + resultData[0]["movie_title"] + '\')\">Add</button>' + "</th>";
    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});
