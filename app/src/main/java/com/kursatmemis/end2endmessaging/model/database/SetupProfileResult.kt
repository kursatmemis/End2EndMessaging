package com.kursatmemis.end2endmessaging.model.database

import androidx.lifecycle.MutableLiveData

data class SetupProfileResult(
    val uploadImageResult: MutableLiveData<UploadImageResult>,
    val saveUserDataResult: MutableLiveData<SaveUserDataResult>
)