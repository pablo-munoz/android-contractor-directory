package munoz.pablo.directorio.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import munoz.pablo.directorio.fragments.AccountDetail;
import munoz.pablo.directorio.fragments.CategoryContractors;
import munoz.pablo.directorio.fragments.ChatConversationSelector;
import munoz.pablo.directorio.fragments.ContractorCategoryMenu;
import munoz.pablo.directorio.fragments.Favorites;
import munoz.pablo.directorio.fragments.Login;
import munoz.pablo.directorio.fragments.RegistrationFragment;
import munoz.pablo.directorio.R;
import munoz.pablo.directorio.models.Account;
import munoz.pablo.directorio.models.Contractor;
import munoz.pablo.directorio.models.ContractorCategory;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.utils.AndroidContractorDirectoryApp;
import munoz.pablo.directorio.utils.Constants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String contentFragmentTag = "content";

    // The account of the currently logged in user or an Anonymous account.
    private Account userAccount;

    private NavigationView navigationView;
    private FragmentManager fragmentManager;

    private TextView drawerEmailTv;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (userAccount == null) {
            userAccount = Account.getAnonymous();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        updateNavigation();

        mSocket = ((AndroidContractorDirectoryApp) getApplication()).getSocket();

        fragmentManager = getFragmentManager();

        // Insert the first fragment directly, instead of using changeContentFragment method
        // to enable history and backward navigation
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_activity_content, new ContractorCategoryMenu(), contentFragmentTag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            changeContentFragment(new Login());
        } else if (id == R.id.nav_register) {
            changeContentFragment(new RegistrationFragment());
        } else if (id == R.id.nav_logout) {
            userAccount = Account.getAnonymous();
            ((AndroidContractorDirectoryApp) getApplication()).setUserAccount(userAccount);
            updateNavigation();
            changeContentFragment(new ContractorCategoryMenu());
        } else if (id == R.id.nav_favorites) {
            if (userAccount.isAnonymous()) {
                changeContentFragment(new Login());
            } else {
                changeContentFragment(new Favorites());
            }
        } else if (id == R.id.nav_chat) {
            changeContentFragment(new ChatConversationSelector());
        } else if (id == R.id.nav_account) {
            changeContentFragment(new AccountDetail());
        }
        else if (id == R.id.search_contractor) {
            changeContentFragment(new ContractorCategoryMenu());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeContentFragment(Fragment newFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (fragmentManager.findFragmentByTag(contentFragmentTag) != null) {
            transaction.remove(fragmentManager.findFragmentByTag(contentFragmentTag));
        }


        transaction.add(R.id.main_activity_content, newFragment, contentFragmentTag);

        if (!(newFragment instanceof Login || newFragment instanceof RegistrationFragment)) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public Account getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Account account) {
        userAccount = account;
    }

    public boolean attemptLogin(String email, String password) {
        boolean wasLoginSuccesful = false;
        final String endpoint = Constants.API_URL + "/" + Constants.API_VERSION + "/auth/login";

        APIRequest loginRequest = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                try {
                    String accountId = json.getString("id");
                    String email = json.getString("email");
                    boolean isContractor = false;
                    String token = json.getString("token");

                    if (json.has("contractor")) {
                        isContractor = true;
                    }

                    userAccount = new Account(accountId, email, isContractor, token);
                    ((AndroidContractorDirectoryApp) getApplication()).setUserAccount(userAccount);

                    JSONObject contractorData = null;

                    if (json.has("contractor") && !json.getString("contractor").equals("null")) {
                        contractorData = json.getJSONObject("contractor");
                        userAccount.setContractor(new Contractor(
                                contractorData.getString("id"),
                                contractorData.getString("first_name"),
                                contractorData.getString("middle_name"),
                                contractorData.getString("last_names"),
                                contractorData.getString("email"),
                                contractorData.getString("phone"),
                                contractorData.getString("website"),
                                contractorData.getString("portrait"),
                                contractorData.getDouble("avg_rating"),
                                contractorData.getString("account_id"),
                                new JSONArray(),
                                contractorData.getString("address")
                        ));
                    }

                    updateNavigation();
                    changeContentFragment(new ContractorCategoryMenu());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("MainActivity", "Errors encountered when parsing authentication response.");
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.d("MainActivity", "Login request failed with code: " + code);
                Log.d("MainActivity", "Endpoint: " + endpoint);
                Log.d("MainActivity", errorMessage);
            }
        });

        String payload = String.format("{ \"email\": \"%s\", \"password\": \"%s\" }", email, password);
        loginRequest.execute("POST", endpoint, null, payload);

        return wasLoginSuccesful;
    }

    private void updateNavigation() {
        Menu menu = navigationView.getMenu();
        MenuItem loginMenuItem = menu.findItem(R.id.nav_login);
        MenuItem registrationMenuItem = menu.findItem(R.id.nav_register);
        MenuItem accountMenuItem = menu.findItem(R.id.nav_account);
        MenuItem logoutMenuItem = menu.findItem(R.id.nav_logout);
        MenuItem favoritesMenuItem = menu.findItem(R.id.nav_favorites);
        MenuItem messagesMenuItem = menu.findItem(R.id.nav_chat);


        if (!userAccount.isAnonymous()) {
            loginMenuItem.setVisible(false);
            registrationMenuItem.setVisible(false);
            accountMenuItem.setVisible(true);
            logoutMenuItem.setVisible(true);
            favoritesMenuItem.setVisible(true);
            messagesMenuItem.setVisible(true);
        } else {
            loginMenuItem.setVisible(true);
            registrationMenuItem.setVisible(true);
            accountMenuItem.setVisible(false);
            logoutMenuItem.setVisible(false);
            favoritesMenuItem.setVisible(false);
            messagesMenuItem.setVisible(false);

        }
    }
}
