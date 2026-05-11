
DROP TABLE IF EXISTS monitors;
 
CREATE TABLE monitors (
    id SERIAL PRIMARY KEY,
    timeout_seconds INTEGER NOT NULL,
    alert_email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    last_heartbeat TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);
 
CREATE INDEX idx_monitors_status ON monitors(status);
  