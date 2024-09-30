<?php
    session_start();
    if (!$_SESSION["id"]){
        echo "<script> alert('로그인 필요합니다.');
            location.replace('login.php'); </script>";
    }
    else {
        echo "로그인 사용자 : ".$_SESSION["id"]."<a href='logout.php'><br>로그아웃</a><br>";
    }

    $conn = mysqli_connect('localhost', 'root', '5159', 'android')
               or die("데이터베이스 연결 오류");
    if (isset($_GET["search"])) {
        if ($_GET["search"] == "") $where = "";
        else $where = "where name = '".$_GET["search"]."' or tel = '".$_GET["search"]."' ";
    }
    $query = "select * from phonebooktable ".$where;
    //echo $query."<br>";

    $result = mysqli_query($conn, $query);
?>
<!DOCTYPE html>
<html>
<head>
<title> 전화번호부 </title>
</head>
<body>
<h1> 전화번호부 </h1>
<a href="insert.php">추가</a><br>
<form name="search" method="get" action="phonebook.php">
<input type="text" name="search">
<input type="submit" value="검색">
</form>

<table border="1">
<tr><th>no</th><th>name</th><th>tel</th></tr>
<?php
    while ($row = mysqli_fetch_array($result)) {
        echo "<tr><td><a href='modify.php?no=".$row["no"]."'>".$row["no"]."</a></td>";
        echo "<td>".$row["name"]."</td>";
        echo "<td>".$row["tel"]."</td></tr>";
    }
?>
</table>
</body>
</html>

<?php
    mysqli_close($conn);
?>
