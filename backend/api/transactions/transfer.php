<?php
/**
 * POST /api/transactions/transfer.php
 * Xu ly chuyen tien noi bo / lien ngan hang qua 2 buoc (OTP 2FA + ACID Transaction).
 *
 * Buoc 1 (action = "request_otp"):
 *   Body: { "action": "request_otp", "user_id": 1, "to_account_number": "1900100002", "to_bank_name": "ICBT Bank", "amount": 500000, "description": "..." }
 * Buoc 2 (action = "confirm"):
 *   Body: { "action": "confirm", "user_id": 1, "tx_id": 10, "otp_code": "123456" }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$input = getRequestInput();
$action = trim((string)($input['action'] ?? 'request_otp'));
$userId = (int)($input['user_id'] ?? 0);

if ($userId <= 0) {
    sendJson(false, 'Thieu user_id hop le.', null, 400);
}

$pdo = getDbConnection();

if ($action === 'request_otp') {
    $toAccountNumber = trim((string)($input['to_account_number'] ?? ''));
    $toBankName = trim((string)($input['to_bank_name'] ?? 'ICBT Bank'));
    $amount = (float)($input['amount'] ?? 0);
    $description = trim((string)($input['description'] ?? 'Chuyển tiền'));

    if ($toAccountNumber === '' || $amount <= 0) {
        sendJson(false, 'So tai khoan nhan va so tien phai hop le (> 0).', null, 400);
    }

    $accStmt = $pdo->prepare(
        'SELECT id, account_number, balance FROM accounts WHERE user_id = :uid AND account_type = "CHECKING" LIMIT 1'
    );
    $accStmt->execute([':uid' => $userId]);
    $fromAcc = $accStmt->fetch();

    if (!$fromAcc) {
        sendJson(false, 'Khong tim thay tai khoan thanh toan.', null, 404);
    }
    if ($fromAcc['account_number'] === $toAccountNumber && strcasecmp($toBankName, 'ICBT Bank') === 0) {
        sendJson(false, 'Khong the chuyen tien cho chinh tai khoan nay.', null, 400);
    }
    if ((float)$fromAcc['balance'] < $amount) {
        sendJson(false, 'So du tai khoan khong du de thuc hien giao dich.', null, 400);
    }

    $policy = $pdo->query('SELECT high_value_threshold FROM bank_policies ORDER BY id ASC LIMIT 1')->fetch();
    $threshold = $policy ? (float)$policy['high_value_threshold'] : 10000000.0;
    $requiredEkyc = ($amount >= $threshold) ? 1 : 0;

    if ($requiredEkyc === 1) {
        $userStmt = $pdo->prepare('SELECT ekyc_verified FROM users WHERE id = :uid LIMIT 1');
        $userStmt->execute([':uid' => $userId]);
        $u = $userStmt->fetch();
        if (!$u || !(bool)$u['ekyc_verified']) {
            sendJson(false, 'Giao dich gia tri cao (>= ' . number_format($threshold) . ' VND) yeu cau xac thuc eKYC truoc.', null, 403);
        }
    }

    $otpCode = str_pad((string)random_int(0, 999999), 6, '0', STR_PAD_LEFT);

    $ins = $pdo->prepare(
        'INSERT INTO transactions (from_account_id, to_account_number, to_bank_name, tx_type, amount, otp_code, status, required_ekyc, description)
         VALUES (:from_id, :to_acc, :to_bank, "TRANSFER", :amount, :otp, "PENDING_OTP", :ekyc, :desc)'
    );
    $ins->execute([
        ':from_id' => $fromAcc['id'],
        ':to_acc' => $toAccountNumber,
        ':to_bank' => $toBankName,
        ':amount' => $amount,
        ':otp' => $otpCode,
        ':ekyc' => $requiredEkyc,
        ':desc' => $description,
    ]);

    sendJson(true, 'Da tao ma OTP xac thuc 2FA.', [
        'tx_id' => (int)$pdo->lastInsertId(),
        'otp_code' => $otpCode, // Tra ve phuc vu demo thong bao OTP tren app
        'required_ekyc' => (bool)$requiredEkyc,
    ]);
}

if ($action === 'confirm') {
    $txId = (int)($input['tx_id'] ?? 0);
    $otpInput = trim((string)($input['otp_code'] ?? ''));

    if ($txId <= 0 || $otpInput === '') {
        sendJson(false, 'Thieu tx_id hoac ma OTP.', null, 400);
    }

    try {
        $pdo->beginTransaction();

        $txStmt = $pdo->prepare(
            'SELECT t.*, a.user_id, a.balance
             FROM transactions t
             INNER JOIN accounts a ON t.from_account_id = a.id
             WHERE t.id = :tx_id AND a.user_id = :uid
             LIMIT 1 FOR UPDATE'
        );
        $txStmt->execute([':tx_id' => $txId, ':uid' => $userId]);
        $tx = $txStmt->fetch();

        if (!$tx) {
            $pdo->rollBack();
            sendJson(false, 'Khong tim thay giao dich cho xac thuc.', null, 404);
        }
        if ($tx['status'] !== 'PENDING_OTP') {
            $pdo->rollBack();
            sendJson(false, 'Giao dich nay da duoc xu ly truoc do.', null, 400);
        }
        if (!hash_equals((string)$tx['otp_code'], $otpInput)) {
            $pdo->rollBack();
            sendJson(false, 'Ma OTP khong chinh xac.', null, 401);
        }

        $amount = (float)$tx['amount'];
        if ((float)$tx['balance'] < $amount) {
            $pdo->prepare('UPDATE transactions SET status = "FAILED" WHERE id = :id')->execute([':id' => $txId]);
            $pdo->commit();
            sendJson(false, 'So du khong du tai thoi diem xac nhan.', null, 400);
        }

        // Tru tien tai khoan nguon
        $pdo->prepare('UPDATE accounts SET balance = balance - :amt WHERE id = :acc_id')
            ->execute([':amt' => $amount, ':acc_id' => $tx['from_account_id']]);

        // Neu chuyen noi bo ICBT Bank thi cong tien cho tai khoan dich
        if (strcasecmp((string)$tx['to_bank_name'], 'ICBT Bank') === 0) {
            $pdo->prepare('UPDATE accounts SET balance = balance + :amt WHERE account_number = :to_acc')
                ->execute([':amt' => $amount, ':to_acc' => $tx['to_account_number']]);
        }

        $pdo->prepare('UPDATE transactions SET status = "COMPLETED" WHERE id = :id')
            ->execute([':id' => $txId]);

        $pdo->commit();

        sendJson(true, 'Chuyen tien thanh cong.', [
            'tx_id' => $txId,
            'amount' => $amount,
            'status' => 'COMPLETED',
        ]);
    } catch (Throwable $e) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }
        sendJson(false, 'Loi he thong khi xu ly giao dich.', null, 500);
    }
}

sendJson(false, 'Hanh dong khong hop le.', null, 400);
