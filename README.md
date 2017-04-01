execute maven task with valid credentials:

mvn clean test "-Duser.login=\<loginName\>" "-Duser.password=\<loginPassword\>"

Chrome is used as default browser. If you want to run in Firefox, just add to command line:

-DbrowserClient=ff

Note:
There are only 1 positive test. In case of providing invalid credentials test will fail. For testing negative scenario there should be another test method
