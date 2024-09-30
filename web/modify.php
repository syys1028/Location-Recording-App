<?php
    session_start();
    if (!$_SESSION["id"]){
        echo "<script> alert('로그인 필요합니다.');
            location.replace('login.php'); </script>";
    }
    $conn = mysqli_connect('localhost', 'root', '5159', 'android')
               or die("데이터베이스 연결 오류");

    $query = "select * from phonebooktable where no = ".$_GET["no"];
    //echo $query."<br>";

    $result = mysqli_query($conn, $query);
    $row = mysqli_fetch_array($result);
?>

<!DOCTYPE html>
<html>

<head>
<title> 전화번호 수정 </title>
<script type = "text/javascript">
    function del_fun() {
        //alert("함수 동작");
        yn = confirm("삭제할까요?");
        if (yn == true) {
            location.replace('delete_ok.php?no=<?php echo $row["no"]; ?>');
        }
    }
</script>
</head>

<body>
<h1> 전화번호 수정 </h1>

<form name="modify" method="get" action="modify_ok.php">
<input type="hidden" name="no" value="<?php echo $row["no"]; ?>">
이름 : <input type = "text" name = "name" value="<?php echo $row["name"]; ?>"> <br> 
전화번호 : <input type = "text" name = "tel" value="<?php echo $row["tel"]; ?>"> <br> 
<input type = "submit" value = "수정">
<input type = "button" value = "삭제" onclick="del_fun();"> 
<input type = "button" value = "취소" onclick="history.back();"> 
</form>
</body>
</html>
