# RentMate Android — Neon + Render edition

1. Open `RentMateApp-master` in Android Studio.
2. Edit `app/src/main/java/com/example/rentmate/RentMateApiConfig.kt` and replace `https://YOUR-RENTMATE-SERVICE.onrender.com` with your **deployed Render HTTPS URL** (no trailing slash).
3. Build/Run on your Samsung device. Internet permission is already in AndroidManifest.xml.
4. Register a **new test account** in the Android app, then log in. Neon accounts are separate from Supabase accounts.
5. Before manager assignment, dashboard rent area shows `Awaiting property assignment`.
6. Create a manager through backend `create_staff.py`, then use the API `/docs` manager routes to create property, unit, assign tenant, rent and payment.
7. Log out/in or reopen dashboard to reload rent summary.

The original Compose layout/navigation are preserved. The original dashboard has many demo/local widgets: the rent summary is live; other widgets need UI wiring to repository endpoints. The dashboard ViewModel now supports live fields, but the original DashboardScreen uses its own state. Verification documents are **only selected locally**, not uploaded. Do not test with real identity documents.

Backend ZIP contains SQL, all API endpoints and setup instructions. No Neon DB URL, API signing secret or Supabase key is embedded in Android.
