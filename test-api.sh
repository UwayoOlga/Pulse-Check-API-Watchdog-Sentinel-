#!/bin/bash

echo "=== Pulse Check API Test Script ==="
echo ""

BASE_URL="http://localhost:8080"

echo "1. Registering monitor for device-123 with 60s timeout..."
curl -X POST $BASE_URL/monitors \
  -H "Content-Type: application/json" \
  -d '{"id":"device-123","timeout":60,"alert_email":"admin@critmon.com"}'
echo -e "\n"

sleep 2

echo "2. Sending heartbeat for device-123..."
curl -X POST $BASE_URL/monitors/device-123/heartbeat
echo -e "\n"

sleep 2

echo "3. Getting monitor status..."
curl $BASE_URL/monitors/device-123
echo -e "\n"

sleep 2

echo "4. Pausing monitor..."
curl -X POST $BASE_URL/monitors/device-123/pause
echo -e "\n"

sleep 2

echo "5. Checking status after pause..."
curl $BASE_URL/monitors/device-123
echo -e "\n"

sleep 2

echo "6. Resuming with heartbeat..."
curl -X POST $BASE_URL/monitors/device-123/heartbeat
echo -e "\n"

sleep 2

echo "7. Testing 404 for non-existent device..."
curl -X POST $BASE_URL/monitors/non-existent/heartbeat
echo -e "\n"

sleep 2

echo "8. Registering test device with 10s timeout (will trigger alert)..."
curl -X POST $BASE_URL/monitors \
  -H "Content-Type: application/json" \
  -d '{"id":"test-timeout","timeout":10,"alert_email":"test@example.com"}'
echo -e "\n"

echo ""
echo "=== Test Complete ==="
echo "Wait 10 seconds and check application logs for alert from test-timeout device"
