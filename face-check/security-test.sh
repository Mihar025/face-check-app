#!/bin/bash

echo "🛡️ FINAL SECURITY VALIDATION 🛡️"
echo "=================================="
echo ""

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Сначала проверяем headers (ДО rate limiting тестов!)
echo -e "${BLUE}1. SECURITY HEADERS CHECK${NC}"
echo "-------------------------"
headers=$(curl -s -I -X GET http://localhost:8088/api/v1/actuator/health 2>/dev/null)

check_header() {
    if echo "$headers" | grep -q "$1"; then
        echo -e "${GREEN}✓${NC} $2"
        return 0
    else
        echo -e "${RED}✗${NC} $2"
        return 1
    fi
}

HEADERS_OK=0
check_header "X-Content-Type-Options: nosniff" "X-Content-Type-Options present" && ((HEADERS_OK++))
check_header "X-Frame-Options: DENY" "X-Frame-Options present" && ((HEADERS_OK++))
check_header "X-XSS-Protection: 1; mode=block" "X-XSS-Protection present" && ((HEADERS_OK++))
check_header "Referrer-Policy" "Referrer-Policy present" && ((HEADERS_OK++))
check_header "Content-Security-Policy" "CSP present" && ((HEADERS_OK++))

echo "Headers Score: $HEADERS_OK/5"
echo ""

echo -e "${BLUE}2. RATE LIMITING${NC}"
echo "----------------"
RATE_LIMIT_OK=0
for i in {1..10}; do
    response=$(curl -s -w "HTTP_STATUS:%{http_code}" -X POST http://localhost:8088/api/v1/auth/authenticate \
        -H "Content-Type: application/json" \
        -H "X-Forwarded-For: 192.168.1.$i" \
        -d '{"email":"test@test.com","password":"test"}' 2>/dev/null)

    http_status="${response##*HTTP_STATUS:}"

    if [ $i -le 6 ]; then
        [ "$http_status" = "401" ] && ((RATE_LIMIT_OK++))
    else
        [ "$http_status" = "429" ] && ((RATE_LIMIT_OK++))
    fi
done
echo "Rate Limit Score: $RATE_LIMIT_OK/10"
echo ""

echo -e "${BLUE}3. SQL INJECTION PROTECTION${NC}"
echo "---------------------------"
SQL_OK=0
sql_tests=(
    "' OR 1=1--"
    "'; DROP TABLE users--"
    "' UNION SELECT * FROM users--"
)

for sql in "${sql_tests[@]}"; do
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST http://localhost:8088/api/v1/auth/authenticate \
        -H "Content-Type: application/json" \
        -H "X-Forwarded-For: 10.0.0.1" \
        -d "{\"email\":\"$sql\",\"password\":\"test\"}" 2>/dev/null)

    http_status="${response##*HTTP_STATUS:}"
    [ "$http_status" = "400" ] && ((SQL_OK++))
done
echo "SQL Protection Score: $SQL_OK/3"
echo ""

echo -e "${BLUE}4. XSS PROTECTION${NC}"
echo "-----------------"
XSS_OK=0
xss_tests=(
    "<script>alert('XSS')</script>"
    "javascript:alert(1)"
    "<img onerror=alert(1)>"
)

for xss in "${xss_tests[@]}"; do
    response=$(curl -s -X POST http://localhost:8088/api/v1/auth/authenticate \
        -H "Content-Type: application/json" \
        -H "X-Forwarded-For: 10.0.0.2" \
        -d "{\"email\":\"test@test.com\",\"password\":\"$xss\"}" 2>/dev/null)

    if [[ ! "$response" =~ "<script>" ]] && [[ ! "$response" =~ "javascript:" ]]; then
        ((XSS_OK++))
    fi
done
echo "XSS Protection Score: $XSS_OK/3"
echo ""

echo -e "${BLUE}5. JWT PROTECTION${NC}"
echo "-----------------"
JWT_OK=0
# Without token
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET http://localhost:8088/api/v1/user/profile 2>/dev/null)
http_status="${response##*HTTP_STATUS:}"
[ "$http_status" = "403" ] || [ "$http_status" = "401" ] && ((JWT_OK++))

# With invalid token
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET http://localhost:8088/api/v1/user/profile \
    -H "Authorization: Bearer invalid.token" 2>/dev/null)
http_status="${response##*HTTP_STATUS:}"
[ "$http_status" = "403" ] || [ "$http_status" = "401" ] && ((JWT_OK++))

echo "JWT Protection Score: $JWT_OK/2"
echo ""

echo -e "${BLUE}6. ERROR HANDLING${NC}"
echo "-----------------"
ERROR_OK=0
response=$(curl -s -X GET http://localhost:8088/api/v1/nonexistent 2>/dev/null)
[[ "$response" =~ "{" ]] && [[ ! "$response" =~ "<html>" ]] && ((ERROR_OK++))
echo "Error Handling Score: $ERROR_OK/1"
echo ""

echo -e "${BLUE}7. LARGE PAYLOAD${NC}"
echo "-----------------"
PAYLOAD_OK=0
LARGE=$(printf 'A%.0s' {1..1000000})
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST http://localhost:8088/api/v1/auth/authenticate \
    -H "Content-Type: application/json" \
    -H "X-Forwarded-For: 10.0.0.3" \
    -d "{\"email\":\"test@test.com\",\"password\":\"$LARGE\"}" \
    --max-time 2 2>/dev/null)

http_status="${response##*HTTP_STATUS:}"
if [ "$http_status" = "413" ] || [ "$http_status" = "400" ] || [ "$http_status" = "500" ]; then
    ((PAYLOAD_OK++))
fi
echo "Payload Limit Score: $PAYLOAD_OK/1"
echo ""

# ИТОГОВЫЙ ПОДСЧЕТ
echo "=================================="
echo -e "${YELLOW}FINAL SECURITY SCORECARD${NC}"
echo "=================================="

TOTAL_SCORE=$((HEADERS_OK + RATE_LIMIT_OK + SQL_OK + XSS_OK + JWT_OK + ERROR_OK + PAYLOAD_OK))
MAX_SCORE=25
PERCENTAGE=$((TOTAL_SCORE * 100 / MAX_SCORE))

echo "Security Headers:    $HEADERS_OK/5"
echo "Rate Limiting:       $RATE_LIMIT_OK/10"
echo "SQL Protection:      $SQL_OK/3"
echo "XSS Protection:      $XSS_OK/3"
echo "JWT Authentication:  $JWT_OK/2"
echo "Error Handling:      $ERROR_OK/1"
echo "Payload Limits:      $PAYLOAD_OK/1"
echo "----------------------------------"
echo "TOTAL:              $TOTAL_SCORE/$MAX_SCORE ($PERCENTAGE%)"
echo ""

if [ $PERCENTAGE -ge 90 ]; then
    echo -e "${GREEN}🏆 GRADE: A+ - EXCELLENT SECURITY!${NC}"
    echo "Your application is production-ready!"
elif [ $PERCENTAGE -ge 80 ]; then
    echo -e "${GREEN}✅ GRADE: A - VERY GOOD SECURITY${NC}"
    echo "Minor improvements recommended."
elif [ $PERCENTAGE -ge 70 ]; then
    echo -e "${YELLOW}⚠️ GRADE: B - GOOD SECURITY${NC}"
    echo "Some vulnerabilities need attention."
elif [ $PERCENTAGE -ge 60 ]; then
    echo -e "${YELLOW}⚠️ GRADE: C - ADEQUATE SECURITY${NC}"
    echo "Several security issues to address."
else
    echo -e "${RED}❌ GRADE: F - POOR SECURITY${NC}"
    echo "Critical security improvements needed!"
fi

echo ""
echo "=================================="
echo "     TEST COMPLETED"
echo "=================================="