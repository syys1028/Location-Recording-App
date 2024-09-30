<?php
    session_start();
    if (!$_SESSION["id"]){
        echo "<script> alert('로그인 필요합니다.');
            location.replace('login.php'); </script>";
    }
    echo "insert_ok"."<br>";
    echo "name = ".$_GET["name"]."<br>";
    echo "tel = ".$_GET["tel"]."<br>";

    $conn = mysqli_connect('localhost', 'root', '5159', 'android')
    //$conn = mysqli_connect('localhost', 'syys10280', 'Asdf1234!', 'syys10280')
               or die("데이터베이스 연결 오류");
    $query = "insert into phonebooktable (name, tel) values ('".$_GET["name"]."', '".$_GET["tel"]."')";
    //echo $query;
    $result = mysqli_query($conn, $query);
    mysqli_close($conn);
    
    echo "<script type='text/javascript'> location.replace('phonebook.php'); </script>";
?>