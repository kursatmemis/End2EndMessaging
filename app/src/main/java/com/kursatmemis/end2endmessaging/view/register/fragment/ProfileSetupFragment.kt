package com.kursatmemis.end2endmessaging.view.register.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.kursatmemis.end2endmessaging.databinding.FragmentProfileSetupBinding
import com.kursatmemis.end2endmessaging.util.ImagePickerFromGallery
import com.kursatmemis.end2endmessaging.util.areParamsEmpty
import com.kursatmemis.end2endmessaging.util.userfeedback.AlertDialogButton
import com.kursatmemis.end2endmessaging.util.userfeedback.AlertDialogNegativeButton
import com.kursatmemis.end2endmessaging.util.userfeedback.AlertDialogPositiveButton
import com.kursatmemis.end2endmessaging.util.userfeedback.showAlertDialog
import com.kursatmemis.end2endmessaging.util.userfeedback.showToastMessage
import com.kursatmemis.end2endmessaging.model.database_model.UserData
import com.kursatmemis.end2endmessaging.util.closeKeyboard
import com.kursatmemis.end2endmessaging.util.userfeedback.closeProgressBar
import com.kursatmemis.end2endmessaging.util.userfeedback.showProgressBar
import com.kursatmemis.end2endmessaging.view.BaseFragment
import com.kursatmemis.end2endmessaging.view.main.activity.MainActivity
import com.kursatmemis.end2endmessaging.viewmodel.authentication.ProfileSetupViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@AndroidEntryPoint
class ProfileSetupFragment : BaseFragment<FragmentProfileSetupBinding>() {


    private val profileSetupViewModel: ProfileSetupViewModel by viewModels()
    private lateinit var imagePicker: ImagePickerFromGallery
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var phoneNumber: String
    private var imageUri: Uri? = null
    private lateinit var keyPair: KeyPair

    // Back tuşuna basıldığında tetiklenen işlemi yöneten bir geri çağırma
    private lateinit var backPressHandler: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: ProfileSetupFragmentArgs by navArgs()
        phoneNumber = bundle.phoneNumber
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        backPressHandler = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val userData = getUserData()
                profileSetupViewModel.saveUserDataToFirebaseStore(userData)
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun createBindingObject(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileSetupBinding? {
        return FragmentProfileSetupBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagePicker = ImagePickerFromGallery(
            requireContext(),
            requireActivity(),
            binding.root,
            activityResultLauncher,
            permissionLauncher
        )
    }

    override fun setupUI() {

        // Profil Resmi Seçme
        binding.profileImage.setOnClickListener {
            selectImage()
        }

        // About Alanını Belirleme
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = binding.root.findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton.text.toString()
            binding.aboutEditText.setText(selectedText)
        }

        binding.saveButton.setOnClickListener {
            showProgressBar(binding.progressBar4)
            closeKeyboard(requireActivity(), requireContext())
            val name = binding.nameEditText.text.toString()
            val about = binding.aboutEditText.text.toString()
            val isEmpty = areParamsEmpty(name, about)

            if (isEmpty) {
                Log.w("mKm-text-x", "geldi")
                val title = "Empty Fields"
                val message =
                    "There are empty fields on your profile. Do you still want to continue?"
                val positiveButton = AlertDialogPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    val userData = getUserData()
                    saveUserDataAndProfilePictureToFirebase(userData)
                }
                val negativeButton = AlertDialogNegativeButton(
                    "No"
                ) { dialog, which ->
                    val infoMessage = "It's cancelled!"
                    showToastMessage(requireContext(), infoMessage)
                }
                val alertDialogButton = AlertDialogButton(positiveButton, negativeButton)
                showAlertDialog(requireContext(), title, message, alertDialogButton)
                closeProgressBar(binding.progressBar4)
            } else {
                // FirebaseStora'a kaydet.
                val userData = getUserData()
                saveUserDataAndProfilePictureToFirebase(userData)
            }
        }

        observeLiveData()
    }

    private fun observeLiveData() {

        profileSetupViewModel.uploadImageResult.observe(viewLifecycleOwner) {
            val isUploadSuccessful = it.isSuccessful
            if (!isUploadSuccessful) {
                val errorMessage = it.errorMessage ?: "Unknown Error"
                showToastMessage(requireContext(), errorMessage)
            } else if (profileSetupViewModel.registractionUserDataResult.value != null && profileSetupViewModel.registractionUserDataResult.value!!.isSuccessful) {
                goToMainActivityAndFinishIt(requireContext(), requireActivity())
            }
            closeProgressBar(binding.progressBar4)
        }

        profileSetupViewModel.registractionUserDataResult.observe(viewLifecycleOwner) {
            val isSaveUserSuccess = it.isSuccessful
            if (!isSaveUserSuccess) {
                val errorMessage = it.errorMessage ?: "Unknown Error"
                showToastMessage(requireContext(), errorMessage)
            } else if (profileSetupViewModel.uploadImageResult.value != null && profileSetupViewModel.uploadImageResult.value!!.isSuccessful) {
                goToMainActivityAndFinishIt(requireContext(), requireActivity())
            }
        }

    }

    private fun saveUserDataAndProfilePictureToFirebase(userData: UserData) {
        try {
            profileSetupViewModel.saveProfilePictureToFirebaseStorage(imageUri, phoneNumber)
            profileSetupViewModel.saveUserDataToFirebaseStore(userData)
        } catch (e: Exception) {
            Log.w("mKm - krst", e.toString())
        }
    }

    private fun getUserData(): UserData {

        // Anahtar çiftini oluştur
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.genKeyPair()

        val publicKeyString = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
        val privateKeyString = Base64.encodeToString(keyPair.private.encoded, Base64.NO_WRAP)



        val id = phoneNumber
        val phoneNumber = phoneNumber
        val createdAt = Timestamp.now()
        val displayName = binding.nameEditText.text.toString()
        val description = binding.aboutEditText.text.toString()
        val lastSeen = Timestamp.now()
        val status = "online"
        val photoUrl = imageUri.toString()
        val token = "token"

        return UserData(
            id,
            phoneNumber,
            createdAt,
            displayName,
            description,
            lastSeen,
            status,
            photoUrl,
            publicKeyString,
            token,
            privateKeyString
        )

    }

    private fun selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Sürümü 33'den büyük olan cihazlarda resim seç.
            imagePicker.selectImageForSDK33AndAbove()
        } else {
            // Sürümü 33'den küçük olan cihazlarda resim seç.
            imagePicker.selectImageForPreSDK33()
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    imageUri = intentFromResult.data
                    binding.profileImage.setImageURI(imageUri)
                }
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                // Izin verilmiş.
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                // Izin verilmemiş.
                showToastMessage(requireContext(), "You have to give a permission.")
            }
        }

    }

    private fun goToMainActivityAndFinishIt(context: Context, activity: Activity) {
        activity.finish()
        val intentToMainActivity = Intent(context, MainActivity::class.java)
        context.startActivity(intentToMainActivity)
    }

}