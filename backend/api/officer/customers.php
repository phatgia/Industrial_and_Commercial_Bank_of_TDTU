<?php
/**
 * GET  /api/officer/customers.php             -> Danh sach khach hang
 * POST /api/officer/customers.php             -> Tao khach hang moi + tai khoan CHECKING/SAVING
 */

require_once __DIR__ . '/../config/db.php';

$pdo = getDbConnection();
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    $stmt = $pdo->query(
        'SELECT u.id, u.full_name, u.email, u.phone, u.ekyc_verified, u.created_at,
                COALESCE(SUM(CASE WHEN a.account_type = "CHECKING" THEN a.balance ELSE 0 END), 0) AS checking_balance
         FROM users u
         LEFT JOIN accounts a ON u.id = a.user_id
         WHERE u.role = "CUSTOMER"
         GROUP BY u.id
         ORDER BY u.id DESC'
    );
    $customers = $stmt->fetchAll();

    foreach ($customers as &$c) {
        $c['id'] = (int)$c['id'];
        $c['ekyc_verified'] = (bool)$c['ekyc_verified'];
        $c['checking_balance'] = (float)$c['checking_balance'];
    }

    sendJson(true, 'Lay danh sach khach hang thanh cong.', ['customers' => $customers]);
}

if ($method === 'POST') {
    $input = getRequestInput();
    $officerId = (int)($input['officer_id'] ?? 0);
    $fullName = trim((string)($input['full_name'] ?? ''));
    $email = trim((string)($input['email'] ?? ''));
    $phone = trim((string)($input['phone'] ?? ''));
    $password = (string)($input['password'] ?? '123456');
    $initialBalance = max(0.0, (float)($input['initial_balance'] ?? 0));

    if ($officerId <= 0 || $fullName === '' || $email === '' || $phone === '') {
        sendJson(false, 'Thieu thong tin tao khach hang.', null, 400);
    }

    $roleCheck = $pdo->prepare('SELECT role FROM users WHERE id = :id LIMIT 1');
    $roleCheck->execute([':id' => $officerId]);
    $officer = $roleCheck->fetch();
    if (!$officer || $officer['role'] !== 'OFFICER') {
        sendJson(false, 'Chi nhan vien ngan hang (OFFICER) moi co quyen tao tai khoan.', null, 403);
    }

    try {
        $pdo->beginTransaction();

        $hash = password_hash($password, PASSWORD_BCRYPT);
        $insUser = $pdo->prepare(
            'INSERT INTO users (full_name, email, phone, password_hash, role, ekyc_verified)
             VALUES (:name, :email, :phone, :hash, "CUSTOMER", 0)'
        );
        $insUser->execute([
            ':name' => $fullName,
            ':email' => $email,
            ':phone' => $phone,
            ':hash' => $hash,
        ]);
        $newUserId = (int)$pdo->lastInsertId();

        $policy = $pdo->query('SELECT saving_interest_rate FROM bank_policies ORDER BY id ASC LIMIT 1')->fetch();
        $savingRate = $policy ? (float)$policy['saving_interest_rate'] : 5.50;

        $checkingNo = '19001' . str_pad((string)$newUserId, 5, '0', STR_PAD_LEFT);
        $savingNo = '19002' . str_pad((string)$newUserId, 5, '0', STR_PAD_LEFT);

        $insAcc = $pdo->prepare(
            'INSERT INTO accounts (account_number, user_id, account_type, balance, interest_rate)
             VALUES (:acc_no, :uid, :type, :bal, :rate)'
        );
        $insAcc->execute([
            ':acc_no' => $checkingNo,
            ':uid' => $newUserId,
            ':type' => 'CHECKING',
            ':bal' => $initialBalance,
            ':rate' => 0.20,
        ]);
        $insAcc->execute([
            ':acc_no' => $savingNo,
            ':uid' => $newUserId,
            ':type' => 'SAVING',
            ':bal' => 0.00,
            ':rate' => $savingRate,
        ]);

        $pdo->commit();

        sendJson(true, 'Tao tai khoan khach hang thanh cong.', [
            'customer_id' => $newUserId,
            'checking_account' => $checkingNo,
            'saving_account' => $savingNo,
        ], 201);
    } catch (PDOException $e) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }
        sendJson(false, 'Email hoac so dien thoai da ton tai.', null, 409);
    }
}

sendJson(false, 'Method not allowed.', null, 405);
