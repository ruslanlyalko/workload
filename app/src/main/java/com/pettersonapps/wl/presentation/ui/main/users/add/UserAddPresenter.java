package com.pettersonapps.wl.presentation.ui.main.users.add;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import static com.pettersonapps.wl.data.Config.API_KEY;
import static com.pettersonapps.wl.data.Config.APP_ID;
import static com.pettersonapps.wl.data.Config.APP_NAME;
import static com.pettersonapps.wl.data.Config.DATABASE_URL;

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

    public void onRegister(Context context, String email, String password, String name, String phone,
                           String skype, String department, final boolean registerAndLogin) {
        getView().showProgress();
        mUser.setEmail(email);
        mUser.setName(name);
        mUser.setPhone(phone);
        mUser.setSkype(skype);
        mUser.setDepartment(department);
        if(registerAndLogin) {
            getAuth().createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> getView().hideProgress())
                    .addOnSuccessListener(aVoid -> {
                        mUser.setKey(getAuth().getUid());
                        mUser.setIsAllowEditPastReports(true);
                        getDataManager().saveUser(mUser)
                                .addOnSuccessListener(aVoid1 -> {
                                    if(getView() == null) return;
                                    getDataManager().clearCache();
                                    getView().afterSuccessfullySaving();
                                })
                                .addOnFailureListener(e -> {
                                    if(getView() == null) return;
                                    getView().hideProgress();
                                    getView().showError(e.getMessage());
                                });
                    });
            return;
        }
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl(DATABASE_URL)
                .setApiKey(API_KEY)
                .setApplicationId(APP_ID).build();
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(context, firebaseOptions, APP_NAME);
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance(APP_NAME));
        }
        if(mAuth2 != null) {
            mAuth2.createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> {
                        if(getView() == null) return;
                        getView().hideProgress();
                        getView().showError(e.getMessage());
                    })
                    .addOnSuccessListener(aVoid -> {
                        mUser.setKey(mAuth2.getUid());
                        getDataManager().saveUser(mUser)
                                .addOnSuccessListener(aVoid1 -> {
                                    mAuth2.signOut();
                                    if(getView() == null) return;
                                    getView().close();
                                })
                                .addOnFailureListener(e -> {
                                    if(getView() == null) return;
                                    getView().hideProgress();
                                    getView().showError(e.getMessage());
                                });
                    });
        } else {
            getAuth().createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener(e -> getView().hideProgress())
                    .addOnSuccessListener(aVoid -> {
                        mUser.setKey(getAuth().getUid());
                        getDataManager().saveUser(mUser)
                                .addOnSuccessListener(aVoid1 -> {
                                    if(getView() == null) return;
                                    getDataManager().clearCache();
                                    getView().afterSuccessfullySaving();
                                })
                                .addOnFailureListener(e -> {
                                    if(getView() == null) return;
                                    getView().hideProgress();
                                    getView().showError(e.getMessage());
                                });
                    });
        }
    }

    public User getUser() {
        return mUser;
    }
}
