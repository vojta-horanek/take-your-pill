package eu.vojtechh.takeyourpill.depinjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.vojtechh.takeyourpill.database.PillDatabase
import eu.vojtechh.takeyourpill.klass.Constants
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationScope {

    @Singleton
    @Provides
    fun providePillDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        PillDatabase::class.java,
        Constants.PILL_DATABASE_NAME
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun providePillDao(db: PillDatabase) = db.getPillDao()
}