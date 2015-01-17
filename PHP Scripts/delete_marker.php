<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['longitude']) && isset($_POST['latitude'])) {

    $longitude = $_POST['longitude'];
    $latitude = $_POST['latitude'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    $result = mysql_query("DELETE FROM markers WHERE latitude=$latitude AND longitude=$longitude");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Operation DELETE MARKER: success.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to delete
        $response["success"] = 0;
        $response["message"] = "Operation DELETE MARKER: failure.";

        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>