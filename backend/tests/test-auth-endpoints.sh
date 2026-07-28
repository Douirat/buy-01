#!/usr/bin/env bash
#
# test-auth-endpoints.sh
# Smoke tests for user-service: /register and /login
#
# NOTE: UserController currently maps to "/api/api/" (@RequestMapping("/api/api/")),
# so the effective register path is /api/api/register instead of /api/register.
# This script targets it as-is. Fix the mapping to "/api" and update BASE_PATH below
# once corrected.
#
# Both DTOs use @ModelAttribute, so bodies must be sent as
# application/x-www-form-urlencoded, not JSON.

set -uo pipefail

HOST="${HOST:-http://localhost:8081}"
BASE_PATH="${BASE_PATH:-/api/api}"     # change to /api once the double-mapping bug is fixed
AUTH_PATH="${AUTH_PATH:-/api/auth}"

PASS=0
FAIL=0

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Unique email per run so re-running the script doesn't collide with @Indexed(unique=true)
TS=$(date +%s)
TEST_NAME="Test User"
TEST_EMAIL="testuser+${TS}@example.com"
TEST_PASSWORD="Password123!"

pass() { echo -e "${GREEN}✓ PASS${NC} - $1"; PASS=$((PASS+1)); }
fail() { echo -e "${RED}✗ FAIL${NC} - $1"; echo "   $2"; FAIL=$((FAIL+1)); }

check_status() {
  local desc="$1" expected="$2" actual="$3" body="$4"
  if [[ "$actual" == "$expected" ]]; then
    pass "$desc (HTTP $actual)"
  else
    fail "$desc (expected HTTP $expected, got $actual)" "$body"
  fi
}

echo "=================================================="
echo " user-service auth smoke tests"
echo " Host: $HOST"
echo " Test email: $TEST_EMAIL"
echo "=================================================="
echo

# ---------------------------------------------------------------------------
# 1. Register - happy path
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Register: valid payload ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${BASE_PATH}/register" \
  --data-urlencode "name=${TEST_NAME}" \
  --data-urlencode "email=${TEST_EMAIL}" \
  --data-urlencode "password=${TEST_PASSWORD}")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
check_status "register valid user" "201" "$code" "$body"
echo

# ---------------------------------------------------------------------------
# 2. Register - duplicate email
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Register: duplicate email ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${BASE_PATH}/register" \
  --data-urlencode "name=${TEST_NAME}" \
  --data-urlencode "email=${TEST_EMAIL}" \
  --data-urlencode "password=${TEST_PASSWORD}")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
# Adjust expected code to match whatever your GlobalExceptionHandler returns for duplicates (409 is typical)
check_status "register duplicate email rejected" "409" "$code" "$body"
echo

# ---------------------------------------------------------------------------
# 3. Register - validation errors (blank name, bad email, short password)
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Register: validation failures ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${BASE_PATH}/register" \
  --data-urlencode "name=" \
  --data-urlencode "email=not-an-email" \
  --data-urlencode "password=short")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
check_status "register with invalid fields rejected" "400" "$code" "$body"
echo

# ---------------------------------------------------------------------------
# 4. Register - missing fields entirely
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Register: missing fields ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${BASE_PATH}/register")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
check_status "register with no body rejected" "400" "$code" "$body"
echo

# ---------------------------------------------------------------------------
# 5. Login - happy path
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Login: valid credentials ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${AUTH_PATH}/login" \
  --data-urlencode "identifier=${TEST_EMAIL}" \
  --data-urlencode "password=${TEST_PASSWORD}")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
check_status "login valid credentials" "200" "$code" "$body"

# Try to pull a token out of the response for reuse if you extend this script later
TOKEN=$(echo "$body" | grep -o '"token"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"token"[[:space:]]*:[[:space:]]*"//;s/"$//')
if [[ -n "${TOKEN:-}" ]]; then
  echo "   Token captured (${#TOKEN} chars)"
fi
echo

# ---------------------------------------------------------------------------
# 6. Login - wrong password
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Login: wrong password ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${AUTH_PATH}/login" \
  --data-urlencode "identifier=${TEST_EMAIL}" \
  --data-urlencode "password=WrongPassword999!")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
check_status "login wrong password rejected" "401" "$code" "$body"
echo

# ---------------------------------------------------------------------------
# 7. Login - unknown user
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Login: unknown identifier ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${AUTH_PATH}/login" \
  --data-urlencode "identifier=nobody-${TS}@example.com" \
  --data-urlencode "password=${TEST_PASSWORD}")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
check_status "login unknown identifier rejected" "401" "$code" "$body"
echo

# ---------------------------------------------------------------------------
# 8. Login - missing fields
# ---------------------------------------------------------------------------
echo -e "${YELLOW}--- Login: missing fields ---${NC}"
resp=$(curl -s -w "\n%{http_code}" -X POST "${HOST}${AUTH_PATH}/login")
body=$(echo "$resp" | sed '$d')
code=$(echo "$resp" | tail -n1)
echo "$body"
check_status "login with no body rejected" "400" "$code" "$body"
echo

# ---------------------------------------------------------------------------
echo "=================================================="
echo -e " Results: ${GREEN}${PASS} passed${NC}, ${RED}${FAIL} failed${NC}"
echo "=================================================="

[[ $FAIL -eq 0 ]] && exit 0 || exit 1
