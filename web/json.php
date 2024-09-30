<?php
    $conn = mysqli_connect('localhost', 'root', 'asdf1234', 'phonebookdb')
               or die("데이터베이스 연결 오류");
   
    $query = "select * from phonebooktable ";
    //echo $query."<br>";

    $result = mysqli_query($conn, $query);
    $phonebook = array();
    $i = 0;

    while ($row = mysqli_fetch_array($result)) {
        //echo $row["no"]."<br>";
        //echo $row["name"]."<br>";
        //echo $row["tel"]."<br>";
        $member = array();
        $member["no"] = intval($row["no"]);
        $member["name"] = $row["name"];
        $member["tel"] = $row["tel"];

        $phonebook[$i] = $member;
        $i = $i + 1;
    }

    $json["PhoneBook"] = $phonebook;
    $output = json_encode($json);
    echo $output;

    mysqli_close($conn);
?>
