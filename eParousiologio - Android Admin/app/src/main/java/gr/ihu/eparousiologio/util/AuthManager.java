package gr.ihu.eparousiologio.util;

import androidx.annotation.MainThread;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.Consumer;

public final class AuthManager {
    private static final AuthManager I = new AuthManager();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private AuthManager() {
    }

    public static AuthManager get() {
        return I;
    }

    public FirebaseUser current() {
        return auth.getCurrentUser();
    }

    @MainThread
    public void signInTeacher(String email, String password,
                              Runnable onOk,
                              Consumer<Exception> onErr) {
        FirebaseUser u = auth.getCurrentUser();
        if (u != null) {
            onOk.run();
            return;
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(r -> onOk.run())
                .addOnFailureListener(onErr::accept);
    }
}