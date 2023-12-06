package com.kursatmemis.end2endmessaging.di.module

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.kursatmemis.end2endmessaging.view.adapter.MessageListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey

@Module
@InstallIn(ActivityComponent::class)
class AdapterInActivityModule {

    fun generateKeyPair(): KeyPair {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore"
            )
            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    "myKeyAlias",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setKeySize(2048)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build()
            )
            return keyPairGenerator.generateKeyPair()
        } else {
            throw RuntimeException("Bu örnek sadece Android P ve sonrası sürümlerde çalışır.")
        }
    }

    @Provides
    @ActivityScoped
    fun provideMessageListAdapter(
        @ActivityContext context: Context
    ) : MessageListAdapter {
        val keyPair: KeyPair = generateKeyPair()

        val publicKey: PublicKey = keyPair.public
        val privateKey: PrivateKey = keyPair.private

        return MessageListAdapter(context, arrayListOf())
    }

}