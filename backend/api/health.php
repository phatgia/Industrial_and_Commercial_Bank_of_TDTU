<?php
/**
 * Endpoint kiem tra API va ket noi MySQL.
 * Mo trinh duyet truy cap: https://phatg.id.vn/api/health.php
 */

require_once __DIR__ . '/config/db.php';

requireMethod('GET');

$pdo = getDbConnection();
$pdo->query('SELECT 1')->fetchColumn();

sendJson(true, 'API va MySQL hoat dong binh thuong.', [
    'status' => 'ok',
    'server_time' => date('c'),
]);
