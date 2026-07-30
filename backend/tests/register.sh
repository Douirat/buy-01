curl -X POST http://localhost:8081/api/users/register \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'firstName=John' \
-d 'lastName=Doe' \
-d 'username=johndoe' \
-d 'email=john@example.com' \
-d 'password=Password123!' \
-d 'role=CLIENT' \
-d 'avatar=default-avatar.png'

# ------------------------------------->
# curl -X POST http://localhost:8081/api/users/register \
# -H 'Content-Type: application/x-www-form-urlencoded' \
# -d 'firstName=John01' \
# -d 'lastName=Doe' \
# -d 'username=jdoe01' \
# -d 'email=john01@example.com' \
# -d 'password=Password1234!' \
# -d 'avatar=default-avatar.png'