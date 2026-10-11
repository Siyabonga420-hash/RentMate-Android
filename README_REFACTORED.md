# RentMate - Refactored Tenant Dashboard

This version fixes the crash caused by the placeholder `TODO("Not yet implemented")` viewModel function.

Changes made:
- Removed the custom placeholder `viewModel()` call from `DashboardScreen.kt`.
- Removed dashboard dependency on `NavController` for now, because no NavHost/routes are configured yet.
- Added fake dashboard data so the UI can run immediately.
- Improved the tenant dashboard layout with Material 3 cards, metrics, quick actions, and recent activity.
- Simplified `MainActivity.kt` so it uses only one `setContent {}` block.

Open this project in Android Studio, sync Gradle, then run the app.
