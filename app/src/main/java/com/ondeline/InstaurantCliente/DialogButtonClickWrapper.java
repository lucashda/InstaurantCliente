package com.ondeline.InstaurantCliente;

import android.app.AlertDialog;
import android.view.View;

public abstract class DialogButtonClickWrapper implements View.OnClickListener {

    private AlertDialog dialog;

    public DialogButtonClickWrapper(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onClick(View v) {
        if(onClicked()){
            dialog.dismiss();
        }
    }

    protected abstract boolean onClicked();
}