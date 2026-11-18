<?php
/**
 * GET /api/utilities/bills.php?service_type=ELECTRICITY&bill_code=EVN-TDTU-1126
 * Yeu cau Bearer Token. Chi tra cuu hoa don thuoc ve user dang dang nhap.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('GET');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);
$userId = (int)$authUser['id'];

$serviceType = strtoupper(trim((string)($_GET['service_type'] ?? '')));
$billCode = trim((string)($_GET['bill_code'] ?? ''));

$conditions = ['user_id = :uid'];
$params = [':uid' => $userId];

if ($serviceType !== '') {
    $conditions[] = 'service_type = :stype';
    $params[':stype'] = $serviceType;
}
if ($billCode !== '') {
    $conditions[] = 'bill_code = :bcode';
    $params[':bcode'] = $billCode;
}

$sql = 'SELECT id, user_id, service_type, provider_name, bill_code, amount, is_paid, due_date, created_at
        FROM utility_bills
        WHERE ' . implode(' AND ', $conditions) . '
        ORDER BY is_paid ASC, due_date ASC';

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
