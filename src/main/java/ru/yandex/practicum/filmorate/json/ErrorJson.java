package ru.yandex.practicum.filmorate.json;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class ErrorJson {
    private String errorMessage;
    private static Gson gson = new Gson();

    public static String Response(String errorMessage) {
        ErrorJson json = new ErrorJson();
        json.setErrorMessage(errorMessage);
        return gson.toJson(json);
    }
}
