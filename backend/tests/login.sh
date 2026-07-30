curl -s -X POST 'http://localhost:8081/api/auth/login' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'identifier=john@example.com' \
-d 'password=Password123!' | jq .
# # ------------------------------------->
# curl -s -X POST 'http://localhost:8081/api/auth/login' \
# -H 'Content-Type: application/x-www-form-urlencoded' \
# -d 'identifier=john01@example.com' \
# -d 'password=Password1234!' | jq .