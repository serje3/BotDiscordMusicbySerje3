package org.serje3.rest.base;

import com.google.gson.Gson;

import java.net.http.HttpRequest;

// Базовый класс для представления тела запроса
public class RequestBody {
    // Метод, преобразующий тело запроса в формат, поддерживаемый HTTP
    public HttpRequest.BodyPublisher toBodyPublisher(){
        Gson gson = new Gson();
        String json = gson.toJson(this); // Преобразование объекта в JSON
        return HttpRequest.BodyPublishers.ofString(json);
    };
}
