package com.pettersonapps.wl.presentation.ui.main.users.add;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserAddPresenter extends BasePresenter<UserAddView> {

    private final User mUser = new User();
    private FirebaseAuth mAuth2;

    UserAddPresenter() {
    }

    public void onViewReady() {
    }

    public void onRegister(Context context, String email, String password, String name, String phone, String department) {
        getView().showProgress();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://paworkload01.firebaseio.com/")
                .setApiKey("AIzaSyD-WLO5002WFK0r1nlaBllaRteUr-Xum0U")
                .setApplicationId("paworkload01").build();
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(context, firebaseOptions, "Workload2");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("Workload2"));
        }
        if (mAuth2 != null) {
            mAuth2.createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> {
                        getView().hideProgress();
                        getView().showMessage(e.getMessage());
                    })
                    .addOnSuccessListener(aVoid -> {
                        mUser.setKey(mAuth2.getUid());
                        mUser.setEmail(email);
                        mUser.setName(name);
                        mUser.setPhone(phone);
                        mUser.setDepartment(department);
                        getDataManager().saveUser(mUser)
                                .addOnSuccessListener(aVoid1 -> {
                                    mAuth2.signOut();
                                    getView().close();
                                })
                                .addOnFailureListener(e -> {
                                    getView().hideProgress();
                                    getView().showMessage(e.getMessage());
                                });
                    });
        } else {
            getAuth().createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> getView().hideProgress())
                    .addOnSuccessListener(aVoid -> {
                        mUser.setKey(getCurrentUser().getUid());
                        mUser.setEmail(email);
                        mUser.setName(name);
                        mUser.setPhone(phone);
                        mUser.setDepartment(department);
                        getDataManager().saveUser(mUser)
                                .addOnSuccessListener(aVoid1 -> getView().afterSuccessfullySaving())
                                .addOnFailureListener(e -> {
                                    getView().hideProgress();
                                    getView().showMessage(e.getMessage());
                                });
                    });
        }
    }

    public User getUser() {
        return mUser;
    }
}
