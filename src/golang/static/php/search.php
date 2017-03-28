<?php
  
  $indexes = '';
  $host = $_SERVER['HTTP_HOST'];
  $searchTerms = $_POST['search'];


  $commandString = 'sudo ssh namenode "/usr/local/hadoop/bin/hadoop jar ~/TinyGoogle/TinyGoogleMR.jar TinyGoogleMapReduce /TinyGoogle/books /web1 2 \\"'.$searchTerms.'\\""';
  $results ='';
  exec($commandString,$results);
  $temp = '';
  foreach($results as $k=>$v){
    $temp = $temp.$v;
  }
  die(exec('whoami');
  $commandString2 = 'sudo rm ../results/results.json';
  exec($commandString2);
  $commandString3 = 'sudo scp namenode:~/results.json ../results/results.json';
  exec($commandString3);
?>