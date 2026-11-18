<?php
/**
 * GET /api/branches/list.php
 * Tra ve danh sach chi nhanh ngan hang cho tinh nang Ban do.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('GET');

$pdo = getDbConnection();
$stmt = $pdo->query('SELECT id, branch_name, address, latitude, longitude, opening_hours FROM bank_branches ORDER BY id ASC');
$branches = $stmt->fetchAll();

foreach ($branches as &$branch) {
    $branch['id'] = (int)$branch['id'];
    $branch['latitude'] = (float)$branch['latitude'];
    $branch['longitude'] = (float)$branch['longitude'];
}

sendJson(true, 'Lay danh sach chi nhanh thanh cong.', ['branches' => $branches]);
