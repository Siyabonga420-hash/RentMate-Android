# RentMate Android — Live Render/Neon edition

Open this folder in Android Studio. API origin is configured in `RentMateApiConfig.kt` as `https://rentmate-system.onrender.com`.

## Changed
- Registration and login use Render API, with longer HTTP timeouts (45s connect, 90s read). A timeout can still mean the server committed the account: try logging in before re-registering.
- Wrong credentials (HTTP 401) display an alert dialog, **Invalid email or password**.
- Dashboard and profile no longer display demonstration people, balances, fake chores, or false verification badges.
- After sign-in, dashboard requests `/dashboard`; until a manager assigns the tenant to a unit, the app shows **Awaiting property assignment** and hides household functions.
- After assignment, the dashboard reads rent and household summary from `/dashboard`, and section buttons fetch chores, rent, payments, announcements, expenses and expense splits from the API. Chores can be marked complete.
- Profile reads `/me`; editing, password changes, verification and document uploads are explicitly unavailable rather than pretending to save data.

## Known limitations / important
- The original signup UI selects ID and registration documents, but these are **not uploaded**. ID, student and emergency contact details are not stored by the current backend.
- The existing API does not expose a housemate roster, grocery list, meal schedule, or verification state. These features are not fabricated in the app.
- The API requires an assigned household for tenant household data. A manager must assign the tenant in the manager portal.
- Authentication token is held in memory; restarting the app requires signing in again.
- This ZIP was not built in Android Studio or tested against the deployed service. Please run **Build > Rebuild Project** and test on a non-production account.
- The Render backend is **not changed** by installing this Android ZIP.
- Never put Neon database credentials in the Android app.
