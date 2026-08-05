curl -X POST http://localhost:8080/api/users/register \
  -F "firstName=Bennacer" \
  -F "lastName=Douirat" \
  -F "username=BenDoe" \
  -F "email=bendoe@example.com" \
  -F "password=Password123!" \
  -F "role=CLIENT" \
  -F "avatar=@test.png"
