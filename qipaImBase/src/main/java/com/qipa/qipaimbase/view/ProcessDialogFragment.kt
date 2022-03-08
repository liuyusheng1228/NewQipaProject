package com.qipa.qipaimbase.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.qipa.qipaimbase.R

class ProcessDialogFragment : DialogFragment() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.dialog_process, container)
        getDialog()?.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        return view
    }

    companion object {
        val instance: ProcessDialogFragment
            get() = ProcessDialogFragment()
    }
}
