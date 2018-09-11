package com.przemolab.oknotifier

class Constants {

    class BundleKeys {
        companion object {
            const val SortOrder = "sortOrderKey"
            const val ContestId = "contestIdKey"
            const val Contestants = "contestantsKey"
            const val ContestantsListFragment = "contestantsListFragmentKey"
        }
    }

    class SharedPreferences {
        companion object {
            const val Name = "contestWidgetSharedPreferences"
            const val BestContestant = "bestContestantKey"
            const val NotificationsSwitch = "pref_notifications_switch"
            const val ContestSwitches = "pref_contest_switches"
        }
    }
}
