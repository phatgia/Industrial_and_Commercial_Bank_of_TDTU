<?php
/**
 * POST /api/utilities/pay.php
 * Thanh toan hoa don tien ich tu tai khoan CHECKING bang ACID Transaction.
 * Body: { "user_id": 1, "bill_id": 1 } hoac { "user_id": 1, "bill_code": "EVN-TDTU-1126" }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$input = getRequestInput();
$userId = (int)($input['user_id'] ?? 0);
$billId = (int)($input['bill_id'] ?? 0);
$billCode = trim((string)($input['bill_code'] ?? ''));

if ($userId <= 0 || ($billId <= 0 && $billCode === '')) {
    sendJson(false, 'Thieu user_id va bill_id hoac bill_code.', null, 400);
}

$pdo = getDbConnection();

try {
    $pdo->beginTransaction();

    if ($billId > 0) {
        $billStmt = $pdo->prepare('SELECT * FROM utility_bills WHERE id = :id LIMIT 1 FOR UPDATE');
        $billStmt->execute([':id' => $billId]);
    } else {
        $billStmt = $pdo->prepare('SELECT * FROM utility_bills WHERE bill_code = :code LIMIT 1 FOR UPDATE');
        $billStmt->execute([':code' => $billCode]);
    }
    $bill = $billStmt->fetch();

    if (!$bill) {
        $pdo->rollBack();
        sendJson(false, 'Khong tim thay hoa don.', null, 404);
    }
    if ((bool)$bill['is_paid']) {
        $pdo->rollBack();
        sendJson(false, 'Hoa don nay da duoc thanh toan truoc do.', null, 400);
    }

    $accStmt = $pdo->prepare(
        'SELECT id, account_number, balance FROM accounts WHERE user_id = :uid AND account_type = "CHECKING" LIMIT 1 FOR UPDATE'
    );
    $accStmt->execute([':uid' => $userId]);
    $acc = $accStmt->fetch();

    if (!$acc) {
        $pdo->rollBack();
        sendJson(false, 'Khong tim thay tai khoan thanh toan.', null, 404);
    }

    $amount = (float)$bill['amount'];
    if ((float)$acc['balance'] < $amount) {
        $pdo->rollBack();
        sendJson(false, 'So du tai khoan khong du de thanh toan hoa don.', null, 400);
    }

    // Tru tien tai khoan & gach no hoa don
    $pdo->prepare('UPDATE accounts SET balance = balance - :amt WHERE id = :acc_id')
        ->execute([':amt' => $amount, ':acc_id' => $acc['id']]);
    $pdo->prepare('UPDATE utility_bills SET is_paid = 1 WHERE id = :bid')
        ->execute([':bid' => $bill['id']]);

    $desc = 'Thanh toán ' . $bill['provider_name'] . ' (' . $bill['bill_code'] . ')';
    $ins = $pdo->prepare(
        'INSERT INTO transactions (from_account_id, to_account_number, to_bank_name, tx_type, amount, status, required_ekyc, description)
         VALUES (:acc_id, :bcode, :provider, "BILL_PAYMENT", :amt, "COMPLETED", 0, :desc)'
    );
    $ins->execute([
        ':acc_id' => $acc['id'],
        ':bcode' => $bill['bill_code'],
        ':provider' => $bill['provider_name'],
        ':amt' => $amount,
        ':desc' => $desc,
    ]);

    $pdo->commit();

    sendJson(true, 'Thanh toan hoa don thanh cong.', [
        'tx_id' => (int)$pdo->lastInsertId(),
        'bill_code' => $bill['bill_code'],
        'amount' => $amount,
        'new_balance' => round((float)$acc['balance'] - $amount, 2),
    ]);
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendJson(false, 'Loi he thong khi thanh toan hoa don.', null, 500);
}
