let cartTableBodyElement = jQuery("#cart_table_body");
let rowHTML = "";
let shoptotal = 0;

function Payment(){
    let url = "payment.html";
    window.location.href = url;
}
function decrease(movie_title){
    $.ajax("api/cart", {
        method: "POST",
        data: "title=" + movie_title + "&mode=decrease",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log("The current dict length is: ", resultDataJson["dictlength"]);
            let url = "cart.html"
            window.location.href = url;
        }
    });
}

function increase(movie_title){
    $.ajax("api/cart", {
        method: "POST",
        data: "title=" + movie_title + "&mode=increase",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log("The current dict length is: ", resultDataJson["dictlength"]);
            let url = "cart.html"
            window.location.href = url;
        }
    });
}

function del(movie_title){
    $.ajax("api/cart", {
        method: "POST",
        data: "title=" + movie_title + "&mode=delete",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log("The current dict length is: ", resultDataJson["dictlength"]);
            let url = "cart.html"
            window.location.href = url;
        }
    });
}





function handleCartResult(resultData){
    rowHTML = "";
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + '<button type = "button" onclick=\"decrease(\'' + resultData[i]["title"] + '\')\">-</button>'
        + resultData[i]["quantity"].toString()
        + '<button type = "button" onclick=\"increase(\'' + resultData[i]["title"] + '\')\">+</button>'
        + "</th>";
        rowHTML += "<th>" + '<button type = "button" onclick=\"del(\'' + resultData[i]["title"] + '\')\">Delete</button>' + "</th>";

        rowHTML += "<th>" + "$5" + "</th>";
        let total = 5 * resultData[i]["quantity"];
        shoptotal += total;
        rowHTML += "<th>" + "$" + total.toString()+ "</th>";
        rowHTML += "</tr>";


    }
    rowHTML += "<tr><th></th><th></th><th></th><th></th><th>$" + shoptotal + "</th></tr>";

    cartTableBodyElement.append(rowHTML);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    // url: window.location.href,
    url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java

    //success: (resultData) => handleMovieResult(num,sortmode, page, resultData)
    // Setting callback function to handle data returned successfully by the StarsServlet
    success: resultData => {
        // console.log("This is the fking movie price: ", resultData[0]["price_dict"]);
        handleCartResult(resultData)
    }
});