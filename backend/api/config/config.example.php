<?php
/**
 * File mau cau hinh ket noi Database & API Keys cho Backend ICBT Bank.
 *
 * HUONG DAN SU DUNG:
 * 1. Copy file nay thanh "config.php" trong cung thu muc backend/api/config/.
 * 2. Dien thong tin Database ban da tao trong DirectAdmin -> Databases.
 * 3. File config.php da duoc dua vao .gitignore de khong bi lo mat khau khi push GitHub.
 */

define('DB_HOST', 'localhost');
define('DB_NAME', 'YOUR_DATABASE_NAME');
define('DB_USER', 'YOUR_DATABASE_USER');
define('DB_PASS', 'YOUR_DATABASE_PASSWORD');

// Tuy chon cho cac tinh nang mo rong (Stripe Test Mode & Gemini AI Chatbot)
define('STRIPE_SECRET_KEY', 'sk_test_your_key_here');
define('GEMINI_API_KEY', 'your_gemini_api_key_here');

// Cau hinh bao mat URL, CORS & OTP
define('APP_BASE_URL', 'https://phatg.id.vn/api');
define('CORS_ALLOWED_ORIGIN', ''); // De trong cho Android app; chi dien domain cu the neu co Web FE
define('DEMO_RETURN_OTP', false); // Dat false tren production khi da tich hop SMS/Email OTP
