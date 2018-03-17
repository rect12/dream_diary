package bobrovskaya.rect12.dreamdiary.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class GsonMethods {

    public static ArrayList<String> getListFromJson(String json) {
        Type targetClassType = new TypeToken<List<String>>() { }.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, targetClassType);
    }

    public static String getJsonFromList(List<String> list) {
        Type listOfTestObject = new TypeToken<List<String>>(){}.getType();
        Gson gson = new Gson();
        return gson.toJson(list, listOfTestObject);
    }
}