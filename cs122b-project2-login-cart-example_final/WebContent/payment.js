let payment_form = $("#payment_form");
let payment_total = jQuery("#payment_total");
/**
 * Handle the data returned by PaymentServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle payment response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If Payment succeeds, it will redirect the user to confirmation.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        window.alert(resultDataJson["message"]);
        //$("#payment_error_message").text(resultDataJson["message"]);
    }
}

function handlePaymentData(resultData) {

    let payHTML = "Payment total: $";
    //console.log(resultData.length);
    //let datajson = JSON.parse(resultData);
    console.log(resultData["total"]);
    payHTML = payHTML+resultData["total"];
    payment_total.append(payHTML);


}

jQuery.ajax( {
    url:"api/payment",
    dataType: "json",
    method: "GET",
    success: (resultData) => handlePaymentData(resultData)
});

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

// Bind the submit action of the form to a handler function
payment_form.submit(submitPaymentForm);

