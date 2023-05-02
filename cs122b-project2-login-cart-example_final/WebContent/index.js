let search_form = $("#search_form");
let browse_title = jQuery("#browse_title");
let browse_genre = jQuery("#browse_genre");




let indexchar = 0;
let rowHTML = "";
function Checkout(){

    window.location.href = "cart.html";
}

function handleGenreData(resultData) {

    console.log("handleStarResult: populating star table from resultData");

    let genreHTML = "";
    console.log(resultData.length);
    for (let i = 0; i < resultData.length; i++)
    {
        console.log("123");
        genreHTML = genreHTML + '<a href="movies.html?genre=' + resultData[i]["genre_name"] + '">' + resultData[i]["genre_name"] + '</a> | ';
    }
    genreHTML = genreHTML.substring(0,genreHTML.length-1);
    browse_genre.append(genreHTML);


}
//browse title
for(indexchar = 48; indexchar < 58; indexchar++)
{
    rowHTML = rowHTML + '<a href="movies.html?titleinit=' + String.fromCharCode(indexchar) + '">' + String.fromCharCode(indexchar) + '</a> | ';
}
for(indexchar = 97; indexchar < 123; indexchar++)
{
    rowHTML = rowHTML + '<a href="movies.html?titleinit=' + String.fromCharCode(indexchar) + '">' + String.fromCharCode(indexchar) + '</a> | ';
}
rowHTML = rowHTML + '<a href="movies.html?titleinit=*">*</a>';

browse_title.append(rowHTML);
//browse genre

jQuery.ajax( {
    url:"api/index",
    dataType: "json",
    method: "GET",
    success: (resultData) => handleGenreData(resultData)
});



/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information 
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/index", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

/*
$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});

 */


//new code



function handleSearch(searchEvent) {
    console.log("submit search form");
    let url = "movies.html?";
    searchEvent.preventDefault();
    /*
    let tit = document.getElementById("title").innerText;
    if(tit) {url = url + "title=%" + tit + "%&"}
    let year = document.getElementById("year").innerText;
    if(year) {url = url + "year=" + year + "&"}
    let dir = document.getElementById("director").innerText;
    if(dir) {url = url + "director=" + dir + "&"}
    let star = document.getElementById("star_name").innerText;
    if(star) {url = url + "starName=" + star + "&"}

     */
    //let que = search_form.serialize();
    url = url + search_form.serialize();
    //url = url.substring(0,url.length-1);

    console.log(url);


    window.location.href = url;
    /*
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        // url: window.location.href,
        url: "api/movies"+window.location.search, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
    */


}

// Bind the submit action of the form to a event handler function
search_form.submit(handleSearch);
