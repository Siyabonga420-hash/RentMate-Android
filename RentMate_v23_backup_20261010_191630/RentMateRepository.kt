package com.example.rentmate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.time.OffsetDateTime
import java.util.Locale

class RentMateRepository {

    // =====================================================
    // API REQUEST HANDLER
    // =====================================================

    private fun request(
        path: String,
        method: String = "GET",
        body: JSONObject? = null,
        authenticated: Boolean = true
    ): String {

        val base = RentMateApiConfig.BASE_URL.trimEnd('/')

        require(
            base.startsWith("https://") &&
                    !base.contains("YOUR-")
        ) {
            "Configure your Render API URL."
        }

        val connection = URL(base + path)
            .openConnection() as HttpURLConnection

        try {

            connection.requestMethod = method

            connection.connectTimeout = 45000
            connection.readTimeout = 90000

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            // Attach login token to protected requests
            if (authenticated) {

                val token = RentMateSession.accessToken
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Please sign in first."
                    )

                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $token"
                )
            }

            // Send JSON data when required
            if (body != null) {

                connection.doOutput = true

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                connection.outputStream.use { output ->

                    output.write(
                        body.toString().toByteArray(
                            Charsets.UTF_8
                        )
                    )
                }
            }

            val code = connection.responseCode

            val stream = if (code in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val response = stream
                ?.bufferedReader()
                ?.use { it.readText() }
                ?: ""

            // =====================================================
            // ERROR HANDLING
            // =====================================================

            if (code !in 200..299) {

                val detail = runCatching {

                    val json = JSONObject(response)

                    json.optString(
                        "detail",
                        json.optString(
                            "error",
                            json.optString(
                                "message",
                                "Request failed"
                            )
                        )
                    )

                }.getOrDefault("Request failed")

                val message = when {

                    code == 401 && path == "/auth/login" ->
                        "Invalid email or password."

                    code == 401 ->
                        "Your session has expired. Please sign in again."

                    code == 403 ->
                        "You do not have permission to perform this action."

                    code == 404 ->
                        "Not found: $detail"

                    code == 409 ->
                        detail

                    code == 400 ->
                        detail

                    code >= 500 ->
                        "Server error ($code): $detail"

                    else ->
                        detail
                }

                throw IllegalStateException(message)
            }

            return response

        } catch (e: SocketTimeoutException) {

            throw IllegalStateException(
                "The server took too long to respond. " +
                        "Please check your connection and try again."
            )

        } finally {

            connection.disconnect()
        }
    }

    // =====================================================
    // USER PROFILE
    // =====================================================

    suspend fun getProfile(): JSONObject =
        withContext(Dispatchers.IO) {

            JSONObject(
                request("/me")
            )
        }

    suspend fun updateProfile(
        name: String,
        phone: String
    ): JSONObject = withContext(Dispatchers.IO) {

        val payload = JSONObject()
            .put("full_name", name.trim())
            .put("phone", phone.trim())

        JSONObject(
            request(
                path = "/me",
                method = "PATCH",
                body = payload
            )
        )
    }

    suspend fun changePassword(
        current: String,
        new: String
    ): Unit = withContext(Dispatchers.IO) {

        val payload = JSONObject()
            .put("current_password", current)
            .put("new_password", new)

        request(
            path = "/me/password",
            method = "POST",
            body = payload
        )

        Unit
    }

    // =====================================================
    // REGISTRATION
    // =====================================================

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Boolean = withContext(Dispatchers.IO) {

        val payload = JSONObject()
            .put("email", email.trim())
            .put("password", password)
            .put("full_name", fullName.trim())
            .put("phone", phone.trim())

        request(
            path = "/auth/register",
            method = "POST",
            body = payload,
            authenticated = false
        )

        true
    }

    // =====================================================
    // LOGIN
    // =====================================================

    suspend fun signIn(
        email: String,
        password: String
    ) = withContext(Dispatchers.IO) {

        val payload = JSONObject()
            .put("email", email.trim())
            .put("password", password)

        val result = JSONObject(
            request(
                path = "/auth/login",
                method = "POST",
                body = payload,
                authenticated = false
            )
        )

        RentMateSession.accessToken =
            result.getString("access_token")

        RentMateSession.userId =
            result.getJSONObject("user")
                .getString("id")
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    suspend fun getDashboardJson(): JSONObject =
        withContext(Dispatchers.IO) {

            JSONObject(
                request("/dashboard")
            )
        }

    // =====================================================
    // INDIVIDUAL TENANT RENT SUMMARY
    // =====================================================

    suspend fun getRentSummary(): RentSummary =
        withContext(Dispatchers.IO) {

            val data = JSONObject(
                request("/dashboard")
            )

            // Tenant has not yet been assigned a household
            if (!data.optBoolean("assigned", false)) {

                return@withContext RentSummary(
                    message = data.optString(
                        "message",
                        "Awaiting property assignment"
                    )
                )
            }

            val due = data.optDouble(
                "rent_due",
                0.0
            )

            val paid = data.optDouble(
                "amount_paid",
                0.0
            )

            RentSummary(
                amountDue = due,
                amountPaid = paid,
                outstanding = data.optDouble(
                    "outstanding",
                    (due - paid).coerceAtLeast(0.0)
                ),
                dueDate = data.optString(
                    "due_date",
                    ""
                ),
                status = data.optString(
                    "payment_status",
                    "unpaid"
                )
            )
        }

    // =====================================================
    // INDIVIDUAL RENT HISTORY
    // =====================================================

    suspend fun getRentHistory(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/rent")
            )
        }

    // =====================================================
    // INDIVIDUAL PAYMENT HISTORY
    // =====================================================

    suspend fun getPayments(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/payments")
            )
        }

    // =====================================================
    // HOUSEMATES
    // =====================================================

    suspend fun getHousemates(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/housemates")
            )
        }

    // =====================================================
    // CHORES
    // =====================================================

    suspend fun getChores(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/chores")
            )
        }

    suspend fun createChore(
        title: String
    ): Unit = withContext(Dispatchers.IO) {

        require(title.isNotBlank()) {
            "Please enter a chore title."
        }

        request(
            path = "/my/chores",
            method = "POST",
            body = JSONObject()
                .put("title", title.trim())
        )

        Unit
    }

    suspend fun completeChore(
        id: String
    ): JSONObject = withContext(Dispatchers.IO) {

        JSONObject(
            request(
                path = "/my/chores/$id/complete",
                method = "PATCH"
            )
        )
    }

    // =====================================================
    // SHARED GROCERIES
    // =====================================================

    suspend fun getGroceries(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/groceries")
            )
        }

    suspend fun addGrocery(
        title: String
    ): Unit = withContext(Dispatchers.IO) {

        require(title.isNotBlank()) {
            "Please enter a grocery item."
        }

        require(title.trim().length <= 150) {
            "The grocery item name is too long."
        }

        val payload = JSONObject()
            .put("title", title.trim())

        request(
            path = "/my/groceries",
            method = "POST",
            body = payload
        )

        Unit
    }

    suspend fun purchaseGrocery(
        id: String
    ): Unit = withContext(Dispatchers.IO) {

        require(id.isNotBlank()) {
            "Invalid grocery item."
        }

        request(
            path = "/my/groceries/$id/purchased",
            method = "PATCH"
        )

        Unit
    }

    // =====================================================
    // KITCHEN & BRAAI BOOKINGS
    // =====================================================

    suspend fun getBookings(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/bookings")
            )
        }

    suspend fun addBooking(
        title: String,
        start: String,
        end: String
    ): Unit = withContext(Dispatchers.IO) {

        require(title.isNotBlank()) {
            "Please enter a booking title."
        }

        val startDate = try {
            OffsetDateTime.parse(start)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Please enter a valid start date and time."
            )
        }

        val endDate = try {
            OffsetDateTime.parse(end)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Please enter a valid end date and time."
            )
        }

        require(endDate.toInstant().isAfter(startDate.toInstant())) {
            "The end time must be after the start time."
        }

        val payload = JSONObject()
            .put("title", title.trim())
            .put("starts_at", startDate.toString())
            .put("ends_at", endDate.toString())

        request(
            path = "/my/bookings",
            method = "POST",
            body = payload
        )

        Unit
    }

    // =====================================================
    // ANNOUNCEMENTS
    // =====================================================

    suspend fun getAnnouncements(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/announcements")
            )
        }

    // =====================================================
    // HOUSEHOLD EXPENSES
    // =====================================================

    suspend fun getExpenses(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/expenses")
            )
        }

    suspend fun getExpenseSplits(): JSONArray =
        withContext(Dispatchers.IO) {

            JSONArray(
                request("/my/expense-splits")
            )
        }

    suspend fun addExpense(
        householdId: String,
        title: String,
        amount: Double
    ): JSONObject = withContext(Dispatchers.IO) {

        require(title.isNotBlank()) {
            "Please enter an expense title."
        }

        require(amount.isFinite() && amount > 0) {
            "Please enter a valid amount."
        }

        val payload = JSONObject()
            .put("household_id", householdId)
            .put("title", title.trim())
            .put("amount", amount)

        JSONObject(
            request(
                path = "/households/$householdId/expenses",
                method = "POST",
                body = payload
            )
        )
    }
}

// =====================================================
// RENT SUMMARY MODEL
// =====================================================

data class RentSummary(

    val amountDue: Double = 0.0,

    val amountPaid: Double = 0.0,

    val outstanding: Double = 0.0,

    val dueDate: String = "",

    val status: String = "",

    val message: String = ""

) {

    fun money(amount: Double): String {

        return "R" + String.format(
            Locale.US,
            "%,.2f",
            amount
        )
    }
}