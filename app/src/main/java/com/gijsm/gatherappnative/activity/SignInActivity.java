package com.gijsm.gatherappnative.activity;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gijsm.gatherappnative.DatabaseUtils;
import com.gijsm.gatherappnative.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.TAG, "Trying to in");
        setContentView(R.layout.activity_sign_in);
        handleSignIn();
    }

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 895;

    private void handleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signIn();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                String IdToken = result.getSignInAccount().getIdToken();
                DatabaseUtils.googleId = result.getSignInAccount().getId();
                AuthCredential credential = GoogleAuthProvider.getCredential(IdToken, null);
                DatabaseUtils.auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(MainActivity.TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Log.w(MainActivity.TAG, "signInWithCredential", task.getException());
                                } else {
                                    DatabaseUtils.userId = DatabaseUtils.auth.getCurrentUser().getUid();
                                    MainActivity.initLocationServiceHelper();
                                    SignInActivity.this.finish();
                                }
                            }
                        });
            } else {
                signIn();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
