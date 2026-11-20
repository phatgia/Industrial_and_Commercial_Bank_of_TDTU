<?php
/**
 * POST /api/ekyc/ocr.php
 * Yeu cau Bearer Token. Dung Gemini Vision doc thong tin mat truoc CCCD:
 * { "id_front_base64": "..." } -> { cccd_number, full_name, dob, address }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
requireAuth($pdo);

if (!defined('GEMINI_API_KEY')
    || GEMINI_API_KEY === ''
    || GEMINI_API_KEY === 'your_gemini_api_key_here'
    || !function_exists('curl_init')
) {
    sendJson(false, 'Chua cau hinh GEMINI_API_KEY de doc OCR CCCD.', null, 400);
}

$input = getRequestInput();
$base64 = trim((string)($input['id_front_base64'] ?? ''));

if ($base64 === '') {
    sendJson(false, 'Thieu anh mat truoc CCCD (id_front_base64).', null, 400);
}
if (str_contains($base64, ',')) {
    $parts = explode(',', $base64, 2);
    $base64 = $parts[1];
}

$payload = json_encode([
    'contents' => [
        [
            'parts' => [
                [
                    'text' => 'Hãy trích xuất thông tin từ ảnh mặt trước Căn cước công dân (CCCD) Việt Nam dưới dạng JSON với đúng 4 key: '
                        . '"cccd_number" (12 số), "full_name" (Họ và tên viết hoa), "dob" (DD/MM/YYYY), "address" (Nơi thường trú). '
                        . 'Nếu trường nào không đọc được thì để chuỗi rỗng "".',
                ],
                [
                    'inline_data' => [
                        'mime_type' => 'image/jpeg',
                        'data' => $base64,
                    ],
                ],
            ],
        ],
    ],
    'generationConfig' => [
        'responseMimeType' => 'application/json',
        'temperature' => 0.1,
    ],
], JSON_UNESCAPED_UNICODE);

$url = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=' . urlencode(GEMINI_API_KEY);
$ch = curl_init($url);
curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST => true,
    CURLOPT_HTTPHEADER => ['Content-Type: application/json'],
    CURLOPT_POSTFIELDS => $payload,
    CURLOPT_TIMEOUT => 20,
]);
$raw = curl_exec($ch);
$code = (int)curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($raw === false || $code < 200 || $code >= 300) {
    sendJson(false, 'Khong doc duoc thong tin tu anh CCCD.', null, 502);
}

$gemini = json_decode($raw, true);
$text = $gemini['candidates'][0]['content']['parts'][0]['text'] ?? '';
$parsed = json_decode((string)$text, true);

if (!is_array($parsed)) {
    sendJson(false, 'Anh CCCD chua ro net, vui long chup lai hoac nhap tay.', null, 422);
}

sendJson(true, 'Doc thong tin CCCD thanh cong.', [
    'cccd_number' => trim((string)($parsed['cccd_number'] ?? '')),
    'full_name' => trim((string)($parsed['full_name'] ?? '')),
    'dob' => trim((string)($parsed['dob'] ?? '')),
    'address' => trim((string)($parsed['address'] ?? '')),
]);
