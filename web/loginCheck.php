<?php
    $conn = mysqli_connect('localhost', 'root', '5159', 'android')
               or die("데이터베이스 연결 오류");

    $query = "select pw from membertable where id = '".$_GET["id"]."'";
    //echo $query."<br>";

    $result = mysqli_query($conn, $query);
    $row = mysqli_fetch_array($result);
    //echo "password = ".$row["pw"]."<br>";

    if (!session_id()) {
        session_start();
        if ($_GET["pw"] == $row["pw"]) {
            $_SESSION["id"] = $_GET["id"];
            echo "<script> alert('로그인 성공');
                location.replace('phonebook.php'); </script>";
        } 
        else {
            session_destroy();
            echo "<script> alert('로그인 실패');
                location.replace('login.php'); </script>";
        }
    }
?>