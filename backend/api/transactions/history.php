<?php
/**
 * GET /api/transactions/history.php?limit=20
 * Yeu cau Bearer Token. Chi tra ve lich su giao dich cua chinh user dang dang nhap.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('GET');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);
$userId = (int)$authUser['id'];

$accountId = isset($_GET['account_id']) ? (int)$_GET['account_id'] : 0;
$limit = isset($_GET['limit']) ? max(1, min(100, (int)$_GET['limit'])) : 20;

if ($accountId > 0) {
    $sql = 'SELECT t.*, a.account_number AS from_account_number, a.account_type AS from_account_type
            FROM transactions t
            INNER JOIN accounts a ON t.from_account_id = a.id
            WHERE t.from_account_id = :account_id AND a.user_id = :user_id
            ORDER BY t.created_at DESC, t.id DESC
            LIMIT :lim';
    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':account_id', $accountId, PDO::PARAM_INT);
    $stmt->bindValue(':user_id', $userId, PDO::PARAM_INT);
    $stmt->bindValue(':lim', $limit, PDO::PARAM_INT);
} else {
    $sql = 'SELECT t.*, a.account_number AS from_account_number, a.account_type AS from_account_type
            FROM transactions t
            INNER JOIN accounts a ON t.from_account_id = a.id
            WHERE a.user_id = :user_id
            ORDER BY t.created_at DESC, t.id DESC
            LIMIT :lim';
    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':user_id', $userId, PDO::PARAM_INT);
    $stmt->bindValue(':lim', $limit, PDO::PARAM_INT);
}

$stmt->execute();
$rows = $stmt->fetchAll();

foreach ($rows as &$row) {
    $row['id'] = (int)$row['id'];
    $row['from_account_id'] = (int)$row['from_account_id'];
    $row['amount'] = (float)$row['amount'];
    $row['required_ekyc'] = (bool)$row['required_ekyc'];
    unset($row['otp_code']);
}

sendJson(true, 'Lay lich su giao dich thanh cong.', ['transactions' => $rows]);
