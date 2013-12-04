package com.sickboots.sickvideos.youtube;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.sickboots.sickvideos.misc.ApplicationHub;
import com.sickboots.sickvideos.misc.PreferenceCache;

import java.util.Arrays;
import java.util.List;

public class GoogleAccount {
  public interface GoogleAccountDelegate {
    public Activity getActivity();

    public void credentialIsReady();
  }

  private GoogleAccountCredential credential;
  private List<String> scopes;
  private final int REQUEST_ACCOUNT_PICKER = 33008;
  private final String ACCOUNT_FRAGMENT_NAME = "AccountFragment";
  private GoogleAccountDelegate delegate;

  // helper to create a YouTube credential
  public static GoogleAccount newYouTube(GoogleAccountDelegate d) {
    GoogleAccount result = new GoogleAccount();

    result.scopes = Arrays.asList(YouTubeScopes.YOUTUBE);
    result.delegate = d;

    return result;
  }

  public GoogleAccountCredential credential(boolean askUser) {
    if (credential == null) {
      setupCredential();
    }

    // return null if no name set
    if (credential.getSelectedAccountName() != null) {
      return credential;
    }

    if (askUser) {
      chooseAccount(delegate.getActivity());
    }

    return null;
  }

  private void setupCredential() {
    Activity activity = delegate.getActivity();

    credential = GoogleAccountCredential.usingOAuth2(activity, scopes);

    // example code had this, no idea if needed
    credential.setBackOff(new ExponentialBackOff());

    loadAccount();
  }

  private void loadAccount() {
    String accountName = ApplicationHub.preferences().getString(PreferenceCache.GOOGLE_ACCOUNT_PREF, null);

    if (accountName != null) {
      credential.setSelectedAccountName(accountName);
    }
  }

  private void saveAccount() {
    ApplicationHub.preferences().setString(PreferenceCache.GOOGLE_ACCOUNT_PREF, credential.getSelectedAccountName());
  }

  private void chooseAccount(Activity activity) {

    // create temporary fragment so we can get the result
    FragmentManager fragmentManager = activity.getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    Fragment fragment = new AccountFragment();
    fragmentTransaction.add(fragment, ACCOUNT_FRAGMENT_NAME);
    fragmentTransaction.commit();
  }

  public void chooseAccountResult(String result) {
    credential.setSelectedAccountName(result);
    saveAccount();

    delegate.credentialIsReady();

    Activity a = delegate.getActivity();

    // tell the activity that the credential is ready to rock
    FragmentManager fragmentManager = a.getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.remove(a.getFragmentManager().findFragmentByTag(ACCOUNT_FRAGMENT_NAME));
    fragmentTransaction.commit();
  }

  // fragment just used to get onActivityResult
  public class AccountFragment extends Fragment {

    // do not delete this, or we crash on startup
    public AccountFragment() {
      super();
    }

    @Override
    public void onAttach(Activity activity) {
      super.onAttach(activity);

      startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
        case REQUEST_ACCOUNT_PICKER:
          String accountName = null;

          if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
          }

          // must be called for OK and Cancel
          chooseAccountResult(accountName);

          break;
      }
    }

  }

}
