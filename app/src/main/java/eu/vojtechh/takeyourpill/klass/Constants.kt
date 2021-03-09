package eu.vojtechh.takeyourpill.klass

class Constants {
    companion object {
        const val INTENT_EXTRA_REMINDER_ID = "INTENT_EXTRA_REMINDER_ID"
        const val INTENT_EXTRA_REMINDED_TIME = "INTENT_EXTRA_REMINDED_TIME"
        const val INTENT_EXTRA_TIME_DELAY = "INTENT_EXTRA_TIME_DELAY"
        const val INTENT_EXTRA_PILL_ID = "INTENT_EXTRA_PILL_ID"
        const val INTENT_EXTRA_LAUNCHED_FROM_NOTIFICATION =
            "INTENT_EXTRA_LAUNCHED_FROM_NOTIFICATION"
        const val INTENT_CHECK_COUNT = "INTENT_CHECK_COUNT"
        const val PILL_DATABASE_NAME = "db_pill"
        const val TAG_TIME_PICKER_HISTORY_VIEW = "TIME_PICKER_HISTORY_VIEW"
        const val TAG_REMINDER_DIALOG = "TAG_REMINDER_DIALOG"
        const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1
        const val IMAGE_MAX_WIDTH = 1920
        const val IMAGE_MAX_HEIGHT = 1080
        const val IMAGE_MAX_SIZE = 1024
        const val MAX_CHECK_COUNT = 5
    }
}