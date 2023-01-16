package ru.yandex.practicum.tasktracker.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        long durationLong = duration.toMinutes();
        jsonWriter.value(durationLong);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        long duration = jsonReader.nextLong();
        return Duration.ofMinutes(duration);
    }
}