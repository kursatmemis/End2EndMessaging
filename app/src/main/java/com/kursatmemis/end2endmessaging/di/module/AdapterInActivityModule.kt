package com.kursatmemis.end2endmessaging.di.module

import android.content.Context
import com.kursatmemis.end2endmessaging.view.adapter.MessageListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class AdapterInActivityModule {

    @Provides
    @ActivityScoped
    fun provideMessageListAdapter(
        @ActivityContext context: Context
    ) : MessageListAdapter {
        return MessageListAdapter(context, arrayListOf())
    }

}