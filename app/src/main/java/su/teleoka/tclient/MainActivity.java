package su.teleoka.tclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends Activity {

    Client client;

    String username;
    String password;

    final String CONTRACT_NUMBER        = "contract_number";
    final String ADDRESS                = "address";
    final String TARIFF_PLAN            = "tariff_plan";
    final String BALANCE                = "balance";
    final String BONUS_BALANCE          = "bonus_balance";
    final String RESULT_BALANCE         = "result_balance";
    final String DAYS_BEFORE_DISABLE    = "days_before_disable";
    final String DATE_OF_DISABLE        = "date_of_disable";
//    final String LAST_CHECK             = "last_check";

    public static final String PREFS_NAME = "settings";

    TextView txtNumberContract;
    TextView txtAddress;
    TextView txtTariffPlan;
    TextView txtBalance;
    TextView txtBonusBalance;
    TextView txtResultBalance;
    TextView txtDaysBeforeDisable;
    TextView txtDataOfDisable;
    SharedPreferences settings;
    SharedPreferences userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userKey = getSharedPreferences("su.teleoka.tclient_preferences", 0);
        username = userKey.getString("username", "");
        password = userKey.getString("password", "");

        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
        txtNumberContract       = (TextView)findViewById(R.id.txtNumberContract);
        txtAddress              = (TextView)findViewById(R.id.txtAddress);
        txtTariffPlan           = (TextView)findViewById(R.id.txtTariffPlan);
        txtBalance              = (TextView)findViewById(R.id.txtBalance);
        txtBonusBalance         = (TextView)findViewById(R.id.txtBonusBalance);
        txtResultBalance        = (TextView)findViewById(R.id.txtResultBalance);
        txtDaysBeforeDisable    = (TextView)findViewById(R.id.txtDayBeforeDisable);
        txtDataOfDisable        = (TextView)findViewById(R.id.txtDataOfDisable);

        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Счет");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Справка");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);


        if (!username.equals("") && !password.equals("")) {

            if (internetAvailable()) {
                MyTask myTask = new MyTask();
                myTask.execute();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Нет активных сетевых подключений",
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            loadContractInfo();

        } else {

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Не заполнены имя пользователя или пароль",
                    Toast.LENGTH_SHORT);

            toast.show();
        }
    }

    protected void onResume() {

        loadContractInfo();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, "Настройки");
        mi.setIntent(new Intent(this, PrefActivity.class));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void loadContractInfo() {

        settings = getSharedPreferences(PREFS_NAME, 0);
        txtNumberContract.setText("Номер договора: "+settings.getString(CONTRACT_NUMBER, "-"));
        txtAddress.setText("Адрес: " + settings.getString(ADDRESS, "-"));
        txtTariffPlan.setText("Тарифный план: " + settings.getString(TARIFF_PLAN, "-"));
        txtResultBalance.setText("Общий остаток: " + settings.getString(RESULT_BALANCE, "-") + " р.");
        txtBalance.setText(" - основной счет: " + settings.getString(BALANCE, "-") + "р.");
        txtBonusBalance.setText(" - бонусный счет: " + settings.getString(BONUS_BALANCE, "-") + " р.");
        txtDaysBeforeDisable.setText("Дней до отключения: " + settings.getString(DAYS_BEFORE_DISABLE, "-"));
        txtDataOfDisable.setText("Дата отключения: " + settings.getString(DATE_OF_DISABLE, "-"));

    }

    public Boolean internetAvailable() {
        ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean internetAvailable = (connectManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        return internetAvailable;
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                client = new Client(username, password);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            Editor editor = settings.edit();
            editor.putString(CONTRACT_NUMBER, client.getNumberContract());
            editor.putString(ADDRESS,client.getAddress());
            editor.putString(TARIFF_PLAN, client.getTariffPlan());
            editor.putString(RESULT_BALANCE, client.getResultBalance());
            editor.putString(BALANCE, client.getBalance());
            editor.putString(BONUS_BALANCE, client.getBonusBalance());
            editor.putString(DAYS_BEFORE_DISABLE, client.getDaysBeforeDisable());
            editor.putString(DATE_OF_DISABLE, client.getDateOfDisable());
            editor.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadContractInfo();
        }
    }
}