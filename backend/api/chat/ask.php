<?php
/**
 * POST /api/chat/ask.php
 * Yeu cau Bearer Token. Tro ly AI Nova: doc so du & giao dich thang tu MySQL,
 * goi Gemini API (neu da cau hinh GEMINI_API_KEY) hoac fallback thong minh,
 * tra ve { reply, action } de app dieu huong nhanh.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);
$userId = (int)$authUser['id'];

$input = getRequestInput();
$question = trim((string)($input['message'] ?? ''));

if ($question === '') {
    sendJson(false, 'Vui long nhap noi dung cau hoi.', null, 400);
}

// Lay thong tin so du va 5 giao dich gan nhat de lam context cho AI
$accStmt = $pdo->prepare(
    'SELECT account_type, balance FROM accounts WHERE user_id = :uid'
);
$accStmt->execute([':uid' => $userId]);
$accounts = $accStmt->fetchAll();

$checkingBalance = 0.0;
$savingBalance = 0.0;
foreach ($accounts as $acc) {
    if ($acc['account_type'] === 'CHECKING') {
        $checkingBalance = (float)$acc['balance'];
    } elseif ($acc['account_type'] === 'SAVING') {
        $savingBalance = (float)$acc['balance'];
    }
}

$txStmt = $pdo->prepare(
    'SELECT t.tx_type, t.amount, t.description, t.created_at
     FROM transactions t
     INNER JOIN accounts a ON t.from_account_id = a.id
     WHERE a.user_id = :uid
     ORDER BY t.created_at DESC
     LIMIT 5'
);
$txStmt->execute([':uid' => $userId]);
$recentTx = $txStmt->fetchAll();

/**
 * Phan tich y dinh de gan action dieu huong cho App.
 */
function detectAction(string $text): string
{
    $lower = mb_strtolower($text, 'UTF-8');
    if (str_contains($lower, 'điện thoại') || str_contains($lower, 'nạp thẻ') || str_contains($lower, 'topup')) {
        return 'PAY_PHONE';
    }
    if (str_contains($lower, 'điện') || str_contains($lower, 'nước') || str_contains($lower, 'hóa đơn')) {
        return 'PAY_BILL';
    }
    if (str_contains($lower, 'giao dịch') || str_contains($lower, 'lịch sử') || str_contains($lower, 'chi tiêu')) {
        return 'VIEW_TRANSACTIONS';
    }
    if (str_contains($lower, 'chi nhánh') || str_contains($lower, 'bản đồ') || str_contains($lower, 'địa chỉ') || str_contains($lower, 'gần nhất')) {
        return 'FIND_BRANCH';
    }
    return 'NONE';
}

$action = detectAction($question);
$reply = null;

// Goi Gemini API neu da cau hinh GEMINI_API_KEY va co cURL
if (defined('GEMINI_API_KEY')
    && GEMINI_API_KEY !== ''
    && GEMINI_API_KEY !== 'your_gemini_api_key_here'
    && function_exists('curl_init')
) {
    $txSummary = '';
    foreach ($recentTx as $t) {
        $txSummary .= "- {$t['tx_type']}: " . number_format((float)$t['amount']) . " VND ({$t['description']})\n";
    }

    $prompt = "Bạn là Nova AI - trợ lý ngân hàng ICBT (TDTU). Hãy trả lời ngắn gọn, lịch sự bằng tiếng Việt (tối đa 3 câu).\n"
        . "Thông tin khách hàng {$authUser['full_name']}:\n"
        . "- Số dư thanh toán (CHECKING): " . number_format($checkingBalance) . " VND\n"
        . "- Số dư tiết kiệm (SAVING): " . number_format($savingBalance) . " VND\n"
        . "5 giao dịch gần nhất:\n" . ($txSummary !== '' ? $txSummary : "- Chưa có giao dịch\n")
        . "\nCâu hỏi của khách: " . $question;

    $payload = json_encode([
        'contents' => [
            ['parts' => [['text' => $prompt]]],
        ],
    ], JSON_UNESCAPED_UNICODE);

    $url = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=' . urlencode(GEMINI_API_KEY);
    $ch = curl_init($url);
    curl_setopt_array($ch, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POST => true,
        CURLOPT_HTTPHEADER => ['Content-Type: application/json'],
        CURLOPT_POSTFIELDS => $payload,
        CURLOPT_TIMEOUT => 15,
    ]);
    $raw = curl_exec($ch);
    $code = (int)curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($raw !== false && $code >= 200 && $code < 300) {
        $gemini = json_decode($raw, true);
        $text = $gemini['candidates'][0]['content']['parts'][0]['text'] ?? null;
        if (is_string($text) && trim($text) !== '') {
            $reply = trim($text);
        }
    }
}

// Fallback thong minh neu chua cau hinh GEMINI_API_KEY
if ($reply === null) {
    $balText = number_format($checkingBalance, 0, ',', '.') . ' VND';
    $reply = match ($action) {
        'PAY_PHONE' => "Số dư khả dụng của bạn hiện là $balText. Bạn có thể bấm nút bên dưới để mở ngay màn hình Nạp tiền điện thoại (ưu đãi 2%).",
        'PAY_BILL' => "Số dư khả dụng của bạn hiện là $balText. Bạn có thể bấm nút bên dưới để vào mục Tiện ích thanh toán hóa đơn Điện / Nước.",
        'VIEW_TRANSACTIONS' => "Hiện bạn có " . count($recentTx) . " giao dịch gần nhất trên hệ thống. Bấm nút bên dưới để xem chi tiết Lịch sử giao dịch.",
        'FIND_BRANCH' => "ICBT Bank hiện có 5 chi nhánh quanh khu vực ĐH Tôn Đức Thắng (Quận 7). Bấm nút bên dưới để xem bản đồ và đường đi bộ ngắn nhất.",
        default => "Xin chào {$authUser['full_name']}! Số dư thanh toán của bạn là $balText. Bạn có thể hỏi tôi về số dư, nạp điện thoại, thanh toán điện nước, lịch sử giao dịch hoặc tìm chi nhánh gần nhất.",
    };
}

sendJson(true, 'Phan hoi thanh cong.', [
    'reply' => $reply,
    'action' => $action,
]);
