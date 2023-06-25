package com.bgmnt.rideshare;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import androidx.preference.PreferenceFragmentCompat;

import java.util.List;


public class SettingsActivity extends AppCompatActivity {


    public static String SelectedItem;

    LanguageManager languageManager = new LanguageManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        Preference preference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            preference = findPreference("app_version");
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            SelectedItem = "English";

            String key = preference.getKey();

            switch (key) {
                case "user_profile":
                    startActivity(new Intent(getActivity(), UserprofileActivity.class));
                    return true;

                case "change_password":
                    startActivity(new Intent(getActivity(), ForgetActivity.class));
                    return true;

                case "terms_and_conditions":
                    Uri uri = Uri.parse("https://divvy-upp.web.app/privacypolicy");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;

                case "Change_Language":
                    startActivity(new Intent(getActivity(), Language.class));
                    return true;

                case "ask_a_question":
                    showEmailDialog();
                    return true;

                default:
                    // Handle other preferences
                    break;
            }

            return super.onPreferenceTreeClick(preference);
        }

        private void showEmailDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Ask a Question");
            builder.setMessage("Do you want to send an email to ask a question?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendEmail();
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();
        }

        private void sendEmail() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"}); // Set the recipient email address
            intent.putExtra(Intent.EXTRA_SUBJECT, "Question"); // Set the email subject

            PackageManager packageManager = getActivity().getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            boolean isIntentSafe = activities.size() > 0;

            if (isIntentSafe) {
                startActivity(Intent.createChooser(intent, "Send email")); // Launch the email client
            } else {
                Toast.makeText(getActivity(), "No email client found", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onStart() {
        languageManager.updateLanguageConfiguration();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        languageManager.updateLanguageConfiguration();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        languageManager.updateLanguageConfiguration();
        super.onStop();
    }
}
