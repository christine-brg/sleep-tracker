INSERT INTO users (
    username,
    email,
    password,
    phone_number,
    enabled,
    role,
    created_at
)
SELECT
    'user' || gs.id                                  AS username,
    'user' || gs.id || '@example.com'                AS email,
    '$2a$12$4Uj.ZDwRvzsJqiXabcqYA.rp2BXJJS/.uyEiQo8Y7QNSZKUUz0ote' AS password,
    '+3736' || LPAD((FLOOR(random() * 10000000))::text, 7, '0') AS phone_number,
    (random() > 0.3)                                 AS enabled,       -- ~70% true
    'USER'                                           AS role,
    NOW() - (random() * INTERVAL '1 year')           AS created_at
FROM generate_series(1, 100) AS gs(id);
