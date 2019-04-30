package com.ondeline.InstaurantCliente;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateUI {
    static void updateUI(FirebaseUser firebaseUser, Context context) {
        if (firebaseUser == null) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
