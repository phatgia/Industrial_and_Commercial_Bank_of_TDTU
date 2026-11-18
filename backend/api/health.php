<?php
/**
 * Endpoint kiem tra API va ket noi MySQL.
 * Mo trinh duyet truy cap: https://phatg.id.vn/api/health.php
 */

require_once __DIR__ . '/config/db.php';

requireMethod('GET');

$pdo = getDbConnection();
$version = $pdo->query('SELECT VERSION()')->fetchColumn();

sendJson(true, 'API va MySQL hoat dong binh thuong.', [
    'php_version' => PHP_VERSION,
    'mysql_version' => $version,
    'server_time' => date('c'),
]);
