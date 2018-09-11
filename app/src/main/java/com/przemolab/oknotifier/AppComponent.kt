package com.przemolab.oknotifier

import com.przemolab.oknotifier.fragments.ContestantsListFragment
import com.przemolab.oknotifier.fragments.ContestsListFragment
import com.przemolab.oknotifier.modules.NotifierRepositoryModule
import com.przemolab.oknotifier.modules.OpenKattisServiceModule

import javax.inject.Singleton
import dagger.Component

@Singleton
@Component(modules = [OpenKattisServiceModule::class, NotifierRepositoryModule::class])
interface AppComponent {

    fun inject(app: NotifierApp)

    fun inject(contestsListFragment: ContestsListFragment)

    fun inject(contestantsListFragment: ContestantsListFragment)
}
