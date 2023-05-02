let confirmTableBodyElement = jQuery("#confirm_table_body");
let rowHTML = "";
let shoptotal = 0;







function handleCartResult(resultData){
    rowHTML = "";
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["saleid"] + "</th>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"]+ "</th>";

        rowHTML += "<th>" + "$5" + "</th>";
        let total = 5 * resultData[i]["quantity"];
        shoptotal += total;
        rowHTML += "<th>" + "$" + total.toString()+ "</th>";
        rowHTML += "</tr>";


    }
    rowHTML += "<tr><th></th><th></th><th></th><th></th><th>$" + shoptotal + "</th></tr>";

    confirmTableBodyElement.append(rowHTML);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    // url: window.location.href,
    url: "api/confirmation", // Setting request url, which is mapped by StarsServlet in Stars.java

    //success: (resultData) => handleMovieResult(num,sortmode, page, resultData)
    // Setting callback function to handle data returned successfully by the StarsServlet
    success: resultData => {
        // console.log("This is the fking movie price: ", resultData[0]["price_dict"]);
        handleCartResult(resultData)
    }
});