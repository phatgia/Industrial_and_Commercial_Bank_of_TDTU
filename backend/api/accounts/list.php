<?php
/**
 * GET /api/accounts/list.php?user_id=1
 * Tra ve danh sach 3 loai tai khoan (CHECKING, SAVING, MORTGAGE) va chinh sach lai suat.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('GET');

$userId = isset($_GET['user_id']) ? (int)$_GET['user_id'] : 0;
if ($userId <= 0) {
    sendJson(false, 'Thieu tham so user_id hop le.', null, 400);
}

$pdo = getDbConnection();

$policyStmt = $pdo->query(
    'SELECT saving_interest_rate, high_value_threshold FROM bank_policies ORDER BY id ASC LIMIT 1'
);
$policy = $policyStmt->fetch() ?: [
    'saving_interest_rate' => 5.50,
    'high_value_threshold' => 10000000.00,
];

$accStmt = $pdo->prepare(
    'SELECT id, account_number, user_id, account_type, balance, interest_rate, periodic_payment, payment_cycle, updated_at
     FROM accounts
     WHERE user_id = :user_id
     ORDER BY FIELD(account_type, "CHECKING", "SAVING", "MORTGAGE")'
);
$accStmt->execute([':user_id' => $userId]);
$accounts = $accStmt->fetchAll();

foreach ($accounts as &$acc) {
    $acc['id'] = (int)$acc['id'];
    $acc['user_id'] = (int)$acc['user_id'];
    $acc['balance'] = (float)$acc['balance'];
    $acc['interest_rate'] = (float)$acc['interest_rate'];
    $acc['periodic_payment'] = (float)$acc['periodic_payment'];

    if ($acc['account_type'] === 'SAVING') {
        $rate = $acc['interest_rate'] > 0 ? $acc['interest_rate'] : (float)$policy['saving_interest_rate'];
        $acc['interest_rate'] = $rate;
        $acc['estimated_monthly_profit'] = round(($acc['balance'] * $rate) / (12 * 100), 2);
    } else {
        $acc['estimated_monthly_profit'] = 0.0;
    }
}

sendJson(true, 'Lay danh sach tai khoan thanh cong.', [
    'policy' => [
        'saving_interest_rate' => (float)$policy['saving_interest_rate'],
        'high_value_threshold' => (float)$policy['high_value_threshold'],
    ],
    'accounts' => $accounts,
]);
