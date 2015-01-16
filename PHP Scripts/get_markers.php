<?php

/*
 * Following code will list all the products
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all products from products table
$result = mysql_query("SELECT *FROM markers") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["markers"] = array();

    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $marker = array();
        $marker["type"] = $row["type"];
        $marker["upvotes"] = $row["upvotes"];
        $marker["downvotes"] = $row["downvotes"];
        $marker["longitude"] = $row["longitude"];
        $marker["latitude"] = $row["latitude"];

        // push single product into final response array
        array_push($response["markers"], $marker);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No markers found";

    // echo no users JSON
    echo json_encode($response);
}
?>