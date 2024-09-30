<?php
    session_start();
    if (!$_SESSION["id"]){
        echo "<script> alert('로그인 필요합니다.');
            location.replace('login.php'); </script>";
    }
?>

<!DOCTYPE html>
<html>

<head>
<title> 전화번호 추가 </title>
</head>

<body>
<h1> 전화번호 추가 </h1>

<form name="insert" method="get" action="insert_ok.php">
이름 : <input type = "text" name = "name"> <br> 
전화번호 : <input type = "text" name = "tel"> <br> 
<input type = "submit" value = "전송">
<input type = "reset" value = "취소" onclick="history.back();"> 
</form>
</body>
</html>
