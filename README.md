# Restaurant Management System
### Android Native (Java) + ERPNext URYPOS Integration

## Project Structure

```
RestaurantPOS/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/restaurant/pos/
│   │   ├── RestaurantApp.java          ← Application class
│   │   ├── activities/
│   │   │   ├── SplashActivity.java     ← App entry point
│   │   │   ├── LoginActivity.java      ← ERPNext connection setup
│   │   │   ├── MainActivity.java       ← Bottom nav host
│   │   │   ├── POSActivity.java        ← Order taking (landscape)
│   │   │   ├── KitchenDisplayActivity  ← KDS (landscape, 3-column)
│   │   │   ├── PaymentActivity.java    ← Cash/Card/QR payment
│   │   │   └── SettingsActivity.java   ← ERPNext config
│   │   ├── fragments/
│   │   │   ├── DashboardFragment.java  ← Stats & quick actions
│   │   │   ├── TablesFragment.java     ← Table map grid
│   │   │   ├── OrdersFragment.java     ← Active POS Invoices
│   │   │   ├── KitchenFragment.java    ← KDS launcher
│   │   │   └── SettingsFragment.java   ← Settings launcher
│   │   ├── api/
│   │   │   ├── ApiCallback.java        ← Generic callback interface
│   │   │   ├── ERPNextClient.java      ← OkHttp3 client w/ auth interceptor
│   │   │   └── ERPNextService.java     ← All ERPNext REST API calls
│   │   ├── models/
│   │   │   ├── ERPConfig.java          ← Connection configuration
│   │   │   ├── MenuItem.java           ← ERPNext Item doctype
│   │   │   ├── RestaurantTable.java    ← Restaurant Table doctype
│   │   │   ├── Order.java              ← POS Invoice doctype
│   │   │   └── PaymentEntry.java       ← Payment data model
│   │   ├── adapters/
│   │   │   ├── MenuAdapter.java        ← Grid menu items
│   │   │   ├── CartAdapter.java        ← Order cart list
│   │   │   ├── TableAdapter.java       ← Tables grid
│   │   │   ├── OrderAdapter.java       ← Orders list
│   │   │   └── KitchenOrderAdapter.java← KDS columns
│   │   └── utils/
│   │       ├── SessionManager.java     ← SharedPreferences auth store
│   │       ├── CartManager.java        ← Singleton cart state
│   │       └── CurrencyUtils.java      ← Currency formatting
│   └── res/
│       ├── layout/                     ← All XML layouts
│       ├── values/colors.xml           ← Dark theme palette
│       ├── values/strings.xml
│       ├── values/themes.xml           ← Material3 dark theme
│       └── menu/bottom_nav_menu.xml
```

## ERPNext URYPOS API Endpoints Used

| Feature              | Method | Endpoint                                          |
|----------------------|--------|---------------------------------------------------|
| Test Connection      | GET    | `/api/method/frappe.auth.get_logged_user`         |
| Load Menu Items      | GET    | `/api/resource/Item`                              |
| Load Tables          | GET    | `/api/resource/Restaurant Table`                  |
| Update Table Status  | PUT    | `/api/resource/Restaurant Table/{id}`             |
| Create POS Invoice   | POST   | `/api/resource/POS Invoice`                       |
| Get Active Orders    | GET    | `/api/resource/POS Invoice`                       |
| Process Payment      | POST   | `/api/method/...make_posa_payment_entry`           |

## Setup Instructions

### 1. ERPNext Configuration
1. Install ERPNext with the **URYPOS** app (or standard POS module)
2. Go to **Settings → API Access** → Generate API Key & Secret for your user
3. Ensure the user has `POS Manager` or `Restaurant Manager` role
4. Create a **POS Profile** named "Restaurant POS" (or your custom name)
5. Create **Restaurant Tables** in ERPNext under the Restaurant module
6. Enable **Item** records with `is_sales_item = 1`

### 2. Android Setup
1. Open in Android Studio (Hedgehog or later)
2. Sync Gradle dependencies
3. Update `minSdk` in `app/build.gradle` if needed (default: 28)
4. Build & run on device/emulator (API 28+)

### 3. First Login
1. Enter your ERPNext URL (e.g., `https://your-restaurant.erpnext.com`)
2. Enter API Key and Secret
3. Enter your POS Profile name
4. Tap **Connect & Login** → verifies live connection

## Key Features

- **POS Screen** (landscape) — Split-panel: 3-col menu grid + live cart
- **Kitchen Display** (landscape) — 3-column Kanban: New → Cooking → Ready
- **Table Management** — Visual grid with real-time status from ERPNext
- **Payment** — Cash (with change calc), Card, QR/KHQR, posts to ERPNext
- **Offline Fallback** — Mock data shown when ERPNext unreachable
- **Auto Token Auth** — API Key:Secret injected on every request via OkHttp interceptor
- **Dark Theme** — Full Material 3 dark theme with orange accent

## Dependencies

| Library              | Version  | Purpose                    |
|----------------------|----------|----------------------------|
| OkHttp3              | 4.12.0   | HTTP client                |
| Gson                 | 2.10.1   | JSON serialization          |
| Glide                | 4.16.0   | Image loading               |
| Material Components  | 1.12.0   | UI components               |
| AndroidX Lifecycle   | 2.8.1    | ViewModel / LiveData        |
| Security Crypto      | 1.1.0    | Encrypted SharedPreferences |

## Cambodia Localization
- Currency displayed in USD and KHR (฿ 4,100 rate)
- KHQR support in payment screen
- ABA Bank QR payment option

---
*Built with ❤️ for Cambodian restaurant operators*
