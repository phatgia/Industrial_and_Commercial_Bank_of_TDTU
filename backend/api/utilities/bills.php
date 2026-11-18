<?php
/**
 * GET /api/utilities/bills.php?user_id=1&service_type=ELECTRICITY&bill_code=EVN-TDTU-1126
 * Tra cuu hoa don tien ich (dien, nuoc, nap DT, ve...).
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('GET');

$userId = isset($_GET['user_id']) ? (int)$_GET['user_id'] : 0;
$serviceType = strtoupper(trim((string)($_GET['service_type'] ?? '')));
$billCode = trim((string)($_GET['bill_code'] ?? ''));

$pdo = getDbConnection();

$conditions = [];
$params = [];

if ($userId > 0) {
    $conditions[] = 'user_id = :uid';
    $params[':uid'] = $userId;
}
if ($serviceType !== '') {
    $conditions[] = 'service_type = :stype';
    $params[':stype'] = $serviceType;
}
if ($billCode !== '') {
    $conditions[] = 'bill_code = :bcode';
    $params[':bcode'] = $billCode;
}

$sql = 'SELECT id, user_id, service_type, provider_name, bill_code, amount, is_paid, due_date, created_at
        FROM utility_bills';
if (!empty($conditions)) {
    $sql .= ' WHERE ' . implode(' AND ', $conditions);
}
$sql .= ' ORDER BY is_paid ASC, due_date ASC';

$stmt = $pdo->prepare($sql);
$stmt->execute($params);
$bills = $stmt->fetchAll();

foreach ($bills as &$bill) {
    $bill['id'] = (int)$bill['id'];
    $bill['user_id'] = (int)$bill['user_id'];
    $bill['amount'] = (float)$bill['amount'];
    $bill['is_paid'] = (bool)$bill['is_paid'];
}

sendJson(true, 'Tra cuu hoa don thanh cong.', ['bills' => $bills]);
