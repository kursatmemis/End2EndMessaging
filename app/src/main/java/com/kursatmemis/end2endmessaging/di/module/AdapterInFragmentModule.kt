package com.kursatmemis.end2endmessaging.di.module

import android.content.Context
import com.kursatmemis.end2endmessaging.view.adapter.ContactListAdapter
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import com.kursatmemis.end2endmessaging.view.adapter.ChatAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class AdapterInFragmentModule {

    @Provides
    @FragmentScoped
    fun providePeopleListAdapter(
        @ActivityContext context: Context,
        firebaseStorageRepository: FirebaseStorageRepository
    ): ContactListAdapter {
        return ContactListAdapter(context, arrayListOf(), firebaseStorageRepository)
    }

    @Provides
    @FragmentScoped
    fun provideChatAdapter(
        @ActivityContext context: Context,
        firebaseStorageRepository: FirebaseStorageRepository
    ) : ChatAdapter {
        return ChatAdapter(context, arrayListOf(), firebaseStorageRepository)
    }

}