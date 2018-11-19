package com.usth.group10.githubclient.repository.icons;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AskForkFragment extends DialogFragment {

    public static final String REPO_URL = "repoURL";

    TransmitDataDialog transmitDataDialog;
    private String repoName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        transmitDataDialog = (TransmitDataDialog) getActivity();


        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Fork");
        dialog.setMessage("Fork this repository ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                transmitDataDialog.TransmitData(true);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                transmitDataDialog.TransmitData(false);
            }
        });

        Dialog askBox = dialog.create();

        return askBox;
    }

}
