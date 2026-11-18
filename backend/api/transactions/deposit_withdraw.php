<?php
/**
 * POST /api/transactions/deposit_withdraw.php
 * Nap tien (DEPOSIT) hoac Rut tien (WITHDRAW) tren tai khoan CHECKING.
 * Body: { "user_id": 1, "type": "DEPOSIT" | "WITHDRAW", "amount": 500000, "description": "..." }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$input = getRequestInput();
$userId = (int)($input['user_id'] ?? 0);
$type = strtoupper(trim((string)($input['type'] ?? '')));
$amount = (float)($input['amount'] ?? 0);
$description = trim((string)($input['description'] ?? ''));

if ($userId <= 0 || !in_array($type, ['DEPOSIT', 'WITHDRAW'], true) || $amount <= 0) {
    sendJson(false, 'Tham so khong hop le (can user_id, type DEPOSIT/WITHDRAW, amount > 0).', null, 400);
}

$pdo = getDbConnection();

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

    if ($type === 'WITHDRAW' && (float)$acc['balance'] < $amount) {
        $pdo->rollBack();
        sendJson(false, 'So du khong du de rut tien.', null, 400);
    }

    $sql = ($type === 'DEPOSIT')
        ? 'UPDATE accounts SET balance = balance + :amt WHERE id = :id'
        : 'UPDATE accounts SET balance = balance - :amt WHERE id = :id';
    $pdo->prepare($sql)->execute([':amt' => $amount, ':id' => $acc['id']]);

    if ($description === '') {
        $description = ($type === 'DEPOSIT') ? 'Nạp tiền vào tài khoản' : 'Rút tiền từ tài khoản';
    }

    $ins = $pdo->prepare(
        'INSERT INTO transactions (from_account_id, to_account_number, to_bank_name, tx_type, amount, status, required_ekyc, description)
         VALUES (:acc_id, :to_acc, "ICBT Bank", :tx_type, :amt, "COMPLETED", 0, :desc)'
    );
    $ins->execute([
        ':acc_id' => $acc['id'],
        ':to_acc' => $acc['account_number'],
        ':tx_type' => $type,
        ':amt' => $amount,
        ':desc' => $description,
    ]);

    $newBalance = ($type === 'DEPOSIT')
        ? ((float)$acc['balance'] + $amount)
        : ((float)$acc['balance'] - $amount);

    $pdo->commit();

    sendJson(true, ($type === 'DEPOSIT' ? 'Nap tien' : 'Rut tien') . ' thanh cong.', [
        'tx_id' => (int)$pdo->lastInsertId(),
        'new_balance' => round($newBalance, 2),
    ]);
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendJson(false, 'Loi he thong khi cap nhat so du.', null, 500);
}
