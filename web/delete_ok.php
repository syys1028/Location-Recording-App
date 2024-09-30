<?php
    session_start();
    if (!$_SESSION["id"]){
        echo "<script> alert('로그인 필요합니다.');
            location.replace('login.php'); </script>";
    }
    $conn = mysqli_connect('localhost', 'root', '5159', 'android')
               or die("데이터베이스 연결 오류");
    $query = "delete from phonebooktable where no='".$_GET["no"]."'";
    //echo $query;
    $result = mysqli_query($conn, $query);
    mysqli_close($conn);
    
    echo "<script type='text/javascript'> location.replace('phonebook.php'); </script>";
?>