package ru.yandex.tonychem.interpalsviewbooster.util;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;
import ru.yandex.tonychem.interpalsviewbooster.InterpalsBoosterApplication;
import ru.yandex.tonychem.interpalsviewbooster.engine.model.Country;
import ru.yandex.tonychem.interpalsviewbooster.search.SearchUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppUtils {

    public static final String CONSOLE_POISON_PILL = "POISON_PILL";
    public static final String BAD_CREDENTIALS_FLAG = "If you have an account, please sign in below";
    public static final String DENIAL_SERVER_RESPONSE =
            "You have been visiting too many profiles in a short period of time.";

    public static final String DENIAL_CLIENT_RESPONSE = "Account has been put on hold " +
            "due to too many requests. Please wait 30 s before next attempt";

    public static final int DENIAL_WAIT = 30_000;

    public static void closeWindow(Event event) {
        Stage currentStage = (Stage) (((Node) (event.getSource())).getScene().getWindow());
        currentStage.close();
    }

    public static List<Country> readCountryList() {
        List<Country> countries = new ArrayList<>(251);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        SearchUI.class.getResourceAsStream("countrylist.datafile")
                ))) {
            while (reader.ready()) {
                String[] inputString = reader.readLine().split(">");
                countries.add(new Country(inputString[1], inputString[0]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error opening countrylist datafile");
        }

        Collections.sort(countries.subList(1, countries.size()));
        return countries;
    }

    public static List<String> readUserAgents() {
        List<String> userAgentList = new ArrayList<>(247);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        InterpalsBoosterApplication.class.getResourceAsStream("user-agent.datafile")
                ))) {
            while (reader.ready()) {
                userAgentList.add(reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error opening user-agent datafile");
        }

        return userAgentList;
    }
}
