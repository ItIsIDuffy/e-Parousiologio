package gr.ihu.eparousiologio.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import gr.ihu.eparousiologio.R;
import gr.ihu.eparousiologio.util.AuthManager;
import gr.ihu.eparousiologio.util.CustomToast;
import gr.ihu.eparousiologio.view.fragment.MainMenuFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        AuthManager
                .get()
                .signInTeacher("parousiologiodipae@gmail.com", "parousiologiodipae", () -> {
                            //προχωράμε όλα οκ
                        },
                        e -> CustomToast.showError(MainActivity.this,
                                "Αποτυχία επαλήθευσης καθηγητή, παρακαλούμε επανεκκινήστε την εφαρμογή"));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainerView, new MainMenuFragment())
                    .commit();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    finish();
                }
            }
        });
    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.mainFragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void resetToMainMenu() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fm.beginTransaction()
                .replace(R.id.mainFragmentContainerView, new MainMenuFragment())
                .commit();
    }
}
