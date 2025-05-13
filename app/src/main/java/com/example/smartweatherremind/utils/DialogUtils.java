package com.example.smartweatherremind.utils;

import android.app.Activity;
import android.app.AlertDialog;

public class DialogUtils {
    public static void showExitDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("Voulez-vous quitter l'application ?")
                .setTitle("Confirmation")
                .setPositiveButton("Quitter", (dialog, which) -> {
                    dialog.dismiss();
                    activity.finish();
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
