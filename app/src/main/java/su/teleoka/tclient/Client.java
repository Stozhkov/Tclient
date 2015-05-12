package su.teleoka.tclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client {
    private static final String URL_SERVER = "http://abonent.teleoka.su/stat_test.php";
    private String username;
    private String password;
    private String statusString = "";
    private String[] statusArray;

    public boolean errorFromLogin   = false;
    public boolean errorFromNetwork = false;

    public Client(String username, String password) throws IOException, NoSuchAlgorithmException {
        this.username = username;
        this.password = MD5(password);

        getStatusString();

        if (statusString.equals("error")) {
            errorFromLogin = true;
        } else {
            parseString();
        }
    }

    private void getStatusString() throws IOException {

        URL url;
        URLConnection conn = null;
        OutputStreamWriter writer;
        BufferedReader reader;

        url = new URL(URL_SERVER);
        try {
            conn = url.openConnection();
            conn.setDoOutput(true);
        } catch (ConnectException e) {
            System.out.println("Соединения с сетью Интернет скорее всего нет. Проверьте сеть!");
            statusString = "error connection";
            errorFromLogin = true;
            return;
        }


        try {
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write("userlogin="+this.username+"&userpasshs="+this.password);
            writer.flush();
            writer.close();
        } catch (UnknownHostException e){
            System.out.println("Соединения с сетью Интернет скорее всего нет. Проверьте сеть!");
            statusString = "error connection";
            errorFromLogin = true;
            return;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            statusString = reader.readLine();
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Сервер статистики не доступен. Попробуйте позже.");
            statusString = "error connection";
            errorFromLogin = true;
            return;
        }


    }

    private void parseString() {
        statusArray = statusString.split(";");
    }

    private String MD5(String input) throws NoSuchAlgorithmException{
        String res = "";
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(input.getBytes());
        byte[] md5 = algorithm.digest();
        String tmp;
        for (int i = 0; i < md5.length; i++) {
            tmp = (Integer.toHexString(0xFF & md5[i]));
            if (tmp.length() == 1) {
                res += "0" + tmp;
            } else {
                res += tmp;
            }
        }
        return res;
    }

    public String getNumberContract() {

        String numberContract;

        try {

            numberContract = statusArray[5];

        } catch (NullPointerException e) {

            numberContract = "n/a";

        }

        return numberContract;

    }

    public String getAddress() {

        String address;

        try {

            address = statusArray[1];

        } catch (NullPointerException e) {

            address = "n/a";

        }

        return address;

    }

    public String getTariffPlan() {

        String tariffPlan;

        try {

            tariffPlan = statusArray[3];

        } catch (NullPointerException e) {

            tariffPlan = "n/a";

        }

        return tariffPlan;

    }

    public String getBalance() {

        String balance;

        try {

            balance = statusArray[2];

        } catch (NullPointerException e) {

            balance = "n/a";

        }

        return balance;

    }

    public String getBonusBalance() {

        String bonusBalance;

        try {

            bonusBalance = statusArray[7];

        } catch (NullPointerException e) {

            bonusBalance = "n/a";

        }

        return bonusBalance;

    }

    public String getResultBalance() {

        Double resultBalance;

        String balance = getBalance();
        String bonusBalance = getBonusBalance();

        if (!balance.equals("n/a") || !bonusBalance.equals("n/a")) {

            resultBalance = Double.parseDouble(balance) + Double.parseDouble(bonusBalance);

            return resultBalance.toString();
        } else {

            return "n/a";

        }
    }

    public String getDaysBeforeDisable() {

        String daysBeforeDisable;

        try {

            daysBeforeDisable = statusArray[8];

        } catch (NullPointerException e) {

            daysBeforeDisable = "n/a";

        }

        return daysBeforeDisable;

    }

    public String getDateOfDisable() {

        String dateOfDisable;

        try {

            dateOfDisable = statusArray[9];

        } catch (NullPointerException e) {

            dateOfDisable = "n/a";

        }

        return dateOfDisable;

    }
}