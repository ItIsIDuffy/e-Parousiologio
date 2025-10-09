package gr.ihu.eparousiologio.util;

import androidx.annotation.MainThread;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.Consumer;

/**
 * Κεντρικός manager για authentication.
 * Διαχειρίζεται αυτόματα τη σύνδεση του καθηγητή.
 */
public final class AuthManager {

    private static final AuthManager I = new AuthManager();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final String TEACHER_EMAIL = "parousiologiodipae@gmail.com";
    private static final String TEACHER_PASS = "parousiologiodipae";

    private AuthManager() {
    }

    public static AuthManager get() {
        return I;
    }

    public FirebaseUser current() {
        return auth.getCurrentUser();
    }

    @MainThread
    public void ensureTeacherSignedIn(Runnable onOk) {
        ensureTeacherSignedIn(onOk, e -> {});
    }

    @MainThread
    public void ensureTeacherSignedIn(Runnable onOk, Consumer<Exception> onErr) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            onOk.run();
            return;
        }

        auth.signInWithEmailAndPassword(TEACHER_EMAIL, TEACHER_PASS)
                .addOnSuccessListener(r -> onOk.run())
                .addOnFailureListener(onErr::accept);
    }

    public void signOut() {
        auth.signOut();
    }
}
