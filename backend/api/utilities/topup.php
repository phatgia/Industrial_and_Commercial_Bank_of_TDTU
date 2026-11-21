<?php
/**
 * POST /api/utilities/topup.php
 * Yeu cau Bearer Token. Nap tien dien thoai voi menh gia hop le do server tinh chiet khau (2%).
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);
$userId = (int)$authUser['id'];

$input = getRequestInput();
$phone = trim((string)($input['phone'] ?? ''));
$denomination = (int)($input['denomination'] ?? 0);

if (!preg_match('/^0[0-9]{8,10}$/', $phone)) {
    sendJson(false, 'So dien thoai khong hop le.', null, 400);
}

$allowedDenominations = [10000, 20000, 50000, 100000, 200000, 500000];
if (!in_array($denomination, $allowedDenominations, true)) {
    sendJson(false, 'Menh gia nap tien khong hop le.', null, 400);
}

$amount = round($denomination * 0.98, 2);

try {
    $pdo->beginTransaction();

    $accStmt = $pdo->prepare(
        'SELECT id, account_number, balance FROM accounts WHERE user_id = :uid AND account_type = "CHECKING" LIMIT 1 FOR UPDATE'
    );
    $accStmt->execute([':uid' => $userId]);
    $acc = $accStmt->fetch();

    if (!$acc) {
        $pdo->rollBack();
        sendJson(false, 'Khong tim thay tai khoan thanh toan.', null, 404);
    }

    if ((float)$acc['balance'] < $amount) {
        $pdo->rollBack();
        sendJson(false, 'So du tai khoan khong du de nap dien thoai.', null, 400);
    }

    $pdo->prepare('UPDATE accounts SET balance = balance - :amt WHERE id = :id')
        ->execute([':amt' => $amount, ':id' => $acc['id']]);

    $desc = 'Nạp tiền điện thoại ' . $phone . ' (Mệnh giá ' . number_format($denomination) . ' đ)';
    $ins = $pdo->prepare(
        'INSERT INTO transactions (from_account_id, to_account_number, to_bank_name, tx_type, amount, status, required_ekyc, description)
         VALUES (:acc_id, :phone, "Phone Top-Up", "BILL_PAYMENT", :amt, "COMPLETED", 0, :desc)'
    );
    $ins->execute([
        ':acc_id' => $acc['id'],
        ':phone' => $phone,
        ':amt' => $amount,
        ':desc' => $desc,
    ]);

    $txId = (int)$pdo->lastInsertId();
    $newBalance = round((float)$acc['balance'] - $amount, 2);
    $pdo->commit();

    sendJson(true, 'Nap tien dien thoai thanh cong.', [
        'tx_id' => $txId,
        'new_balance' => $newBalance,
        'charged_amount' => $amount,
    ]);
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendJson(false, 'Loi he thong khi nap tien dien thoai.', null, 500);
}
