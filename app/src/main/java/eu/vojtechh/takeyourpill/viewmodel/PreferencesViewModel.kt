package eu.vojtechh.takeyourpill.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.vojtechh.takeyourpill.klass.addDay
import eu.vojtechh.takeyourpill.model.*
import eu.vojtechh.takeyourpill.reminder.NotificationManager
import eu.vojtechh.takeyourpill.repository.HistoryRepository
import eu.vojtechh.takeyourpill.repository.PillRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
        private val pillRepository: PillRepository,
        private val historyRepository: HistoryRepository,
) : ViewModel() {
    fun addTestData(context: Context) = viewModelScope.launch {
        val pills = listOf(
            Pill(
                PillEntity(
                    name = "Xyzal",
                    description = "Na alergii, ve skříni",
                    photo = null,
                    color = PillColor.default(),
                    deleted = false,
                    options = ReminderOptions.indefinite()
                ),
                listOf(
                    Reminder.create(pillId = -1),
                    Reminder.create(hour = 9, pillId = -1),
                    Reminder.create(hour = 10, pillId = -1),
                )
            ),
            Pill(
                PillEntity(
                    name = "Nasivin",
                    description = "Na rýmu, na poličce",
                    photo = null,
                    color = PillColor.teal(),
                    deleted = false,
                    options = ReminderOptions.indefinite(),
                ),
                listOf(
                    Reminder.create(pillId = -1),
                    Reminder.create(hour = 12, pillId = -1),
                    Reminder.create(hour = 22, pillId = -1),
                )
            ),
            Pill(
                PillEntity(
                    name = "Vitamín C",
                    description = null,
                    photo = null,
                    color = PillColor.red(),
                    deleted = false,
                    options = ReminderOptions.finite(7),
                ),
                listOf(
                    Reminder.create(hour = 7, pillId = -1),
                )
            )
        )

        val pillIds = mutableListOf<Long>()
        pills.forEach { pill ->
            val id = pillRepository.insertPill(pill)
            pillIds.add(id)
            NotificationManager.createNotificationChannel(context, id.toString(), pill.name)
        }

        val histories = listOf(
            listOf(
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-1),
                    confirmed = Reminder.create(minute = 10, pillId = -1)
                        .getTodayCalendar().addDay(-1),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 9, pillId = -1).getTodayCalendar().addDay(-1),
                    confirmed = null,
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 10, pillId = -1).getTodayCalendar()
                        .addDay(-1),
                    confirmed = Reminder.create(hour = 11, minute = 5, pillId = -1)
                        .getTodayCalendar().addDay(-1),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-2),
                    confirmed = Reminder.create(minute = 8, pillId = -1).getTodayCalendar()
                        .addDay(-2),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 9, pillId = -1).getTodayCalendar()
                        .addDay(-2),
                    confirmed = Reminder.create(hour = 9, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-2),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 10, pillId = -1).getTodayCalendar()
                        .addDay(-2),
                    confirmed = null,
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-3),
                    confirmed = Reminder.create(minute = 8, pillId = -1).getTodayCalendar()
                        .addDay(-3),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 9, pillId = -1).getTodayCalendar()
                        .addDay(-3),
                    confirmed = Reminder.create(hour = 11, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-3),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 10, pillId = -1).getTodayCalendar()
                        .addDay(-3),
                    confirmed = null,
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-4),
                    confirmed = Reminder.create(minute = 8, pillId = -1).getTodayCalendar()
                        .addDay(-4),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 9, pillId = -1).getTodayCalendar()
                        .addDay(-4),
                    confirmed = Reminder.create(hour = 1, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-4),
                    pillId = pillIds[0]
                ),
                History(
                    reminded = Reminder.create(hour = 10, pillId = -1).getTodayCalendar()
                        .addDay(-4),
                    confirmed = null,
                    pillId = pillIds[0]
                ),
            ), listOf(
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-1),
                    confirmed = Reminder.create(minute = 10, pillId = -1)
                        .getTodayCalendar().addDay(-1),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 12, pillId = -1).getTodayCalendar()
                        .addDay(-1),
                    confirmed = Reminder.create(hour = 12, minute = 12, pillId = -1)
                        .getTodayCalendar().addDay(-1),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 22, pillId = -1).getTodayCalendar()
                        .addDay(-1),
                    confirmed = Reminder.create(hour = 22, minute = 5, pillId = -1)
                        .getTodayCalendar().addDay(-1),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-2),
                    confirmed = Reminder.create(minute = 8, pillId = -1).getTodayCalendar()
                        .addDay(-2),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 12, pillId = -1).getTodayCalendar()
                        .addDay(-2),
                    confirmed = Reminder.create(hour = 12, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-2),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 22, pillId = -1).getTodayCalendar()
                        .addDay(-2),
                    confirmed = Reminder.create(hour = 22, minute = 7, pillId = -1)
                        .getTodayCalendar().addDay(-2),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-3),
                    confirmed = Reminder.create(minute = 8, pillId = -1).getTodayCalendar()
                        .addDay(-3),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 12, pillId = -1).getTodayCalendar()
                        .addDay(-3),
                    confirmed = Reminder.create(hour = 12, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-3),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 22, pillId = -1).getTodayCalendar()
                        .addDay(-3),
                    confirmed = Reminder.create(hour = 22, minute = 7, pillId = -1)
                        .getTodayCalendar().addDay(-3),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(pillId = -1).getTodayCalendar().addDay(-4),
                    confirmed = Reminder.create(minute = 8, pillId = -1).getTodayCalendar()
                        .addDay(-4),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 12, pillId = -1).getTodayCalendar()
                        .addDay(-4),
                    confirmed = Reminder.create(hour = 12, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-4),
                    pillId = pillIds[1]
                ),
                History(
                    reminded = Reminder.create(hour = 22, pillId = -1).getTodayCalendar()
                        .addDay(-4),
                    confirmed = Reminder.create(hour = 22, minute = 7, pillId = -1)
                        .getTodayCalendar().addDay(-4),
                    pillId = pillIds[1]
                ),
            ), listOf(
                History(
                    reminded = Reminder.create(hour = 7, pillId = -1).getTodayCalendar().addDay(-1),
                    confirmed = Reminder.create(hour = 7, minute = 12, pillId = -1)
                        .getTodayCalendar().addDay(-1),
                    pillId = pillIds[2]
                ), History(
                    reminded = Reminder.create(hour = 7, pillId = -1).getTodayCalendar()
                        .addDay(-2),
                    confirmed = Reminder.create(hour = 7, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-2),
                    pillId = pillIds[2]
                ), History(
                    reminded = Reminder.create(hour = 7, pillId = -1).getTodayCalendar()
                        .addDay(-3),
                    confirmed = null,
                    pillId = pillIds[2]
                ), History(
                    reminded = Reminder.create(hour = 7, pillId = -1).getTodayCalendar()
                        .addDay(-4),
                    confirmed = Reminder.create(hour = 8, minute = 2, pillId = -1)
                        .getTodayCalendar().addDay(-4),
                    pillId = pillIds[2]
                )
            )
        )

        histories.forEach {
            historyRepository.insertHistories(it)
        }
    }
}