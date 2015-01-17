<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['type']) && isset($_POST['upvotes']) && isset($_POST['downvotes'])
    && isset($_POST['longitude']) && isset($_POST['latitude'])) {

    $type = $_POST['type'];
    $upvotes = $_POST['upvotes'];
    $downvotes = $_POST['downvotes'];
    $longitude = $_POST['longitude'];
    $latitude = $_POST['latitude'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO markers(latitude, longitude, type, upvotes, downvotes)
                          VALUES('$latitude', '$longitude', '$type', '$upvotes', '$downvotes')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Operation ADD MARKER: success";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Operation ADD MARKER: failure (SQL insert failed)";

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