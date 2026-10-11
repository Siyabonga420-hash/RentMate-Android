package com.example.rentmate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


// =========================================================
// DATA MODELS
// =========================================================

/*
 * These classes represent information received
 * from the RentMate backend.
 *
 * No demonstration data is stored here.
 */


data class Household(
    val name: String = "",
    val memberCount: Int = 0,
    val role: String = ""
)


data class DashboardMetric(
    val title: String,
    val value: String,
    val subtitle: String = ""
)


data class QuickAction(
    val title: String,
    val subtitle: String = ""
)


data class Activity(
    val title: String,
    val subtitle: String = ""
)


// =========================================================
// GREETING SECTION
// =========================================================

@Composable
fun GreetingSection(
    userName: String,
    household: Household
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = userName.ifBlank { "Tenant" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Household"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = household.name.ifBlank {
                            "Awaiting property assignment"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (household.name.isNotBlank()) {

                        Text(
                            text = "${household.memberCount} members • ${household.role}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (household.name.isNotBlank()) {

                    AssistChip(
                        onClick = {},
                        label = {
                            Text("Assigned")
                        }
                    )
                }
            }
        }
    }
}


// =========================================================
// DASHBOARD METRICS GRID
// =========================================================

@Composable
fun MetricGrid(
    metrics: List<DashboardMetric>
) {

    if (metrics.isEmpty()) {

        Text(
            text = "No household information available yet.",
            style = MaterialTheme.typography.bodyMedium
        )

        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        metrics.chunked(2).forEach { rowMetrics ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                rowMetrics.forEach { metric ->

                    MetricCard(
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowMetrics.size == 1) {

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


// =========================================================
// METRIC CARD
// =========================================================

@Composable
fun MetricCard(
    metric: DashboardMetric,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = metric.title,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = metric.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (metric.subtitle.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = metric.subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


// =========================================================
// QUICK ACTIONS SECTION
// =========================================================

@Composable
fun QuickActionsSection(
    actions: List<QuickAction>
) {

    SectionTitle(
        title = "Quick Actions"
    )

    if (actions.isEmpty()) {

        Text(
            text = "No actions are currently available.",
            style = MaterialTheme.typography.bodyMedium
        )

        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        actions.forEachIndexed { index, action ->

            val icon = when (index) {

                0 -> Icons.Default.Payments

                1 -> Icons.Default.Add

                2 -> Icons.Default.CheckCircle

                else -> Icons.Default.Campaign
            }

            ActionButton(
                action = action,
                icon = icon
            )
        }
    }
}


// =========================================================
// ACTION BUTTON
// =========================================================

@Composable
fun ActionButton(
    action: QuickAction,
    icon: ImageVector
) {

    Button(
        onClick = {
            // Navigation or API actions must be
            // connected by the parent screen.
        },
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(
                    text = action.title,
                    fontWeight = FontWeight.SemiBold
                )

                if (action.subtitle.isNotBlank()) {

                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


// =========================================================
// RECENT ACTIVITY SECTION
// =========================================================

@Composable
fun RecentActivitySection(
    activities: List<Activity>
) {

    SectionTitle(
        title = "Recent Activity"
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            if (activities.isEmpty()) {

                Text(
                    text = "No recent activity.",
                    style = MaterialTheme.typography.bodyMedium
                )

            } else {

                activities.forEach { activity ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),

                        verticalAlignment = Alignment.Top
                    ) {

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column {

                            Text(
                                text = activity.title,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (activity.subtitle.isNotBlank()) {

                                Text(
                                    text = activity.subtitle,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// =========================================================
// SECTION TITLE
// =========================================================

@Composable
fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,

        modifier = Modifier.padding(
            top = 8.dp,
            bottom = 4.dp
        )
    )
}