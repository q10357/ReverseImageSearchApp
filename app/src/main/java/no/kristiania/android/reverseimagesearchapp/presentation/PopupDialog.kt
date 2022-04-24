package no.kristiania.android.reverseimagesearchapp.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.databinding.SaveCollectionPopupBinding
import no.kristiania.android.reverseimagesearchapp.databinding.TryagainPopupBinding

enum class DialogTypeO {
    DIALOG_ERROR,
    DIALOG_DELETE,
    DIALOG_EDIT
}

sealed class Dialog {
    object CreateErrorDialog: Dialog()
    object DeleteMessageDialog: Dialog()
    object EditCollectionDialog: Dialog()
}
class PopupDialog(
    private val type: DialogType,
    private val f: () -> Unit
): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.tryagain_popup, container, false)

        val binding: TryagainPopupBinding = TryagainPopupBinding.bind(view)
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.messageId.text = type.title
        binding.positiveButton.text = type.posText
        binding.positiveButton.setOnClickListener {
            Log.i("TAGTAG", "THIS IS FUCKING CLICKED ALREADY")
            f()
            dismiss()
        }

        return view
    }
}

enum class DialogType(val title: String, val posText: String) {
    ERROR("Something Happened", "Try Again"),
    INSERT("A title for your collection", "Save"),
    DELETE("Are you sure you want to delete?", "Yes")
}