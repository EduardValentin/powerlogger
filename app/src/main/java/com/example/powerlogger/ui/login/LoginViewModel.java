package com.example.powerlogger.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.powerlogger.R;
import com.example.powerlogger.dto.user.GoogleUserAuthenticationDTO;
import com.example.powerlogger.dto.user.UserAuthenticationResponseDTO;
import com.example.powerlogger.dto.user.UserDTO;
import com.example.powerlogger.dto.user.UserSettingsDTO;
import com.example.powerlogger.repositories.UserRepository;

import java.io.IOException;
import java.util.function.Consumer;

public class LoginViewModel extends ViewModel implements LifecycleOwner {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    private UserRepository userRepository;

    private String userBirthDate;

    LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }


    public void login(String username, String password, Consumer<Object> callback) {
        // can be launched in a separate asynchronous job
        userRepository.login(username, password, callback);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public void saveUserSettings(UserSettingsDTO settingsDTO) {
        userRepository.saveUserSettings(settingsDTO);
    }

    void validateToken(String token, String username, Consumer<Void> onSuccess, Consumer<Throwable> onFail) {
        userRepository.validateToken(token, username, onSuccess, onFail);
    }

    void validateAndCreateIfNotExistsGoogleAccount(String token, Consumer<GoogleUserAuthenticationDTO> onSuccess, Consumer<Throwable> onFail) {
        userRepository.validateAndCreateIfNotExistsGoogleAccount(token, onSuccess, onFail);
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private void onGoogleValidationResponse(Object res) {
        UserAuthenticationResponseDTO authDetails = (UserAuthenticationResponseDTO) res;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    public void setUserBirthDate(String userBirthDate) {
        this.userBirthDate = userBirthDate;
    }
}