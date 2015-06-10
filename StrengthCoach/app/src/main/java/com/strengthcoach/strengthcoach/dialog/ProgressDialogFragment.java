package com.strengthcoach.strengthcoach.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Neeraja on 6/9/2015.
 */

public class ProgressDialogFragment extends DialogFragment {
    public static ProgressDialogFragment newInstance(int msgId) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();

        Bundle args = new Bundle();
        args.putInt("msgId", msgId);

        fragment.setArguments(args);

        return fragment;
    }

    public ProgressDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int msgId = getArguments().getInt("msgId");
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getActivity().getResources().getString(msgId));
        return dialog;
    }
}
