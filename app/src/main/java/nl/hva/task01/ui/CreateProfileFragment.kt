package nl.hva.task01.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import nl.hva.task01.R
import nl.hva.task01.databinding.FragmentCreateProfileBinding
import nl.hva.task01.viewmodel.ProfileViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CreateProfileFragment : Fragment() {

    private var _binding: FragmentCreateProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by activityViewModels()

    // Define variables that register for result of selected retrieval of image from gallery.
    private var profileImageUri: Uri? = null
    private val getProfileImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            profileImageUri = uri
            binding.ivProfileImage.setImageURI(profileImageUri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGallery.setOnClickListener { onGalleryClick() }
        binding.btnConfirm.setOnClickListener { onConfirmClick() }
    }

    private fun onGalleryClick() {
        // Create an Intent with action as ACTION_PICK
        val galleryIntent = Intent(Intent.ACTION_PICK)

        // Sets the type as image/*. This ensures only components of type image are selected
        galleryIntent.type = "image/*"

        // Start the (background) activity to retrieve a selected image using the gallery intent
        getProfileImage.launch("image/*")
    }

    private fun CharSequence?.ifNullOrEmpty(default: String) =
        if (this.isNullOrEmpty()) default else this.toString()

    private fun Uri?.ifNullOrEmpty() = this?.toString() ?: ""

    private fun onConfirmClick() {
        viewModel.createProfile(
            binding.etFirstName.text.ifNullOrEmpty(""),
            binding.etLastName.text.ifNullOrEmpty(""),
            binding.etProfileDescription.text.ifNullOrEmpty(""),
            profileImageUri.ifNullOrEmpty()
        )

        observeProfileCreation()
    }

    private fun observeProfileCreation() {
        viewModel.createSuccess.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(activity, R.string.successfully_created_profile, Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
                findNavController().navigate(R.id.profileFragment)
            }
        }

        viewModel.errorText.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}