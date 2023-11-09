package com.kursatmemis.end2endmessaging.di.module

import android.content.Context
import com.kursatmemis.end2endmessaging.view.adapter.ContactListAdapter
import com.kursatmemis.end2endmessaging.repository.firebase.FirebaseStorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class AdapterModule {

    @Provides
    @FragmentScoped
    fun providePeopleListAdapter(
        @ActivityContext context: Context,
        firebaseStorageRepository: FirebaseStorageRepository
    ): ContactListAdapter {
        return ContactListAdapter(context, arrayListOf(), firebaseStorageRepository)
    }

}