# Backend PHP 8.3 API — ICBT Bank (TDTU)

Thu muc nay chua ma nguon Database MySQL (`database.sql`) va he thong REST API bang PHP 8.3 (PDO) phuc vu ung dung Android ICBT Bank.

## 1. Cau truc thu muc

```text
backend/
├── database.sql                      # Schema 7 bang InnoDB (co user_sessions) + du lieu mau
└── api/
    ├── .htaccess                     # Giu Authorization: Bearer <token> tren Apache
    ├── config/
    │   ├── .htaccess                 # Chan truy cap truc tiep thu muc config/
    │   ├── config.example.php        # File mau cau hinh (an toan khi push GitHub)
    │   ├── config.php                # (Ignored boi .gitignore) Chua mat khau DB that
    │   └── db.php                    # PDO connection, JSON helpers, requireAuth & requireOfficer
    ├── health.php                    # GET  /api/health.php (cong khai)
    ├── auth/
    │   ├── login.php                 # POST /api/auth/login.php (tra ve Bearer token)
    │   └── logout.php                # POST /api/auth/logout.php (thu hoi token)
    ├── accounts/
    │   └── list.php                  # GET  /api/accounts/list.php (Bearer)
    ├── transactions/
    │   ├── history.php               # GET  /api/transactions/history.php (Bearer)
    │   ├── transfer.php              # POST /api/transactions/transfer.php (Bearer + OTP 2FA)
    │   └── deposit_withdraw.php      # POST /api/transactions/deposit_withdraw.php (Bearer)
    ├── utilities/
    │   ├── bills.php                 # GET  /api/utilities/bills.php (Bearer)
    │   └── pay.php                   # POST /api/utilities/pay.php (Bearer)
    ├── officer/
    │   ├── customers.php             # GET/POST /api/officer/customers.php (Bearer + OFFICER)
    │   └── update_policy.php         # POST /api/officer/update_policy.php (Bearer + OFFICER)
    └── branches/
        └── list.php                  # GET  /api/branches/list.php (Bearer)
```

## 2. Huong dan cai dat tren Hosting (DirectAdmin)

1. Tao Database trong **DirectAdmin -> Databases**, sau do mo **phpMyAdmin** va Import file `backend/database.sql`.
2. Copy `backend/api/config/config.example.php` thanh `backend/api/config/config.php` va dien `DB_NAME`, `DB_USER`, `DB_PASS`.
3. Upload toan bo noi dung thu muc `backend/api/` len `public_html/api/` tren hosting.
4. Truy cap `https://phatg.id.vn/api/health.php` de xac nhan `"success": true`.

> **Bao mat GitHub:** File `backend/api/config/config.php` da duoc them vao `.gitignore` o thu muc goc de khong bao gio bi lo thong tin ket noi database hay API key khi `git push`.
