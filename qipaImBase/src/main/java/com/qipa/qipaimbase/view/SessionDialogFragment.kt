package com.qipa.qipaimbase.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.R2

class SessionDialogFragment : DialogFragment() {
//    @BindView(R2.id.tvDelete)
    lateinit var tvDelete: TextView
    private var onHandleListener: OnHandleListener? = null
    @Nullable
   override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.dialog_session, container)
        getDialog()?.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        tvDelete = view.findViewById(R.id.tvDelete)
        tvDelete.setOnClickListener {
            onDeleteClick()
        }
        return view
    }

//    @OnClick(R2.id.tvDelete)
    fun onDeleteClick() {
        if (onHandleListener != null) {
            onHandleListener!!.onDelete()
        }
    }

//    @OnClick(R2.id.tvDeleteContent)
    fun oClearContentClick() {
        if (onHandleListener != null) {
            onHandleListener!!.onClearContent()
        }
    }

    interface OnHandleListener {
        fun onDelete()
        fun onClearContent()
    }

    companion object {
        fun getInstance(onHandleListener: OnHandleListener?): SessionDialogFragment {
            val sessionDialogFragment = SessionDialogFragment()
            sessionDialogFragment.onHandleListener = onHandleListener
            return sessionDialogFragment
        }
    }
}
