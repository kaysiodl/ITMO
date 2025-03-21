package lab5.itmo.client.io.utility;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.*;

public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    @Override
    public JsonElement serialize(ZonedDateTime zonedDateTime, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        JsonObject dateTime = new JsonObject();
        JsonObject date = new JsonObject();
        date.addProperty("year", zonedDateTime.getYear());
        date.addProperty("month", zonedDateTime.getMonthValue());
        date.addProperty("day", zonedDateTime.getDayOfMonth());

        JsonObject time = new JsonObject();
        time.addProperty("hour", zonedDateTime.getHour());
        time.addProperty("minute", zonedDateTime.getMinute());
        time.addProperty("second", zonedDateTime.getSecond());
        time.addProperty("nano", zonedDateTime.getNano());

        dateTime.add("date", date);
        dateTime.add("time", time);

        JsonObject offset = new JsonObject();
        offset.addProperty("totalSeconds", zonedDateTime.getOffset().getTotalSeconds());

        JsonObject zone = new JsonObject();
        zone.addProperty("id", zonedDateTime.getZone().getId());

        jsonObject.add("dateTime", dateTime);
        jsonObject.add("offset", offset);
        jsonObject.add("zone", zone);

        return jsonObject;
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonObject dateTime = jsonObject.getAsJsonObject("dateTime");
        JsonObject date = dateTime.getAsJsonObject("date");
        JsonObject time = dateTime.getAsJsonObject("time");

        int year = date.get("year").getAsInt();
        int month = date.get("month").getAsInt();
        int day = date.get("day").getAsInt();
        int hour = time.get("hour").getAsInt();
        int minute = time.get("minute").getAsInt();
        int second = time.get("second").getAsInt();
        int nano = time.get("nano").getAsInt();

        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second, nano);

        JsonObject offset = jsonObject.getAsJsonObject("offset");
        int totalSeconds = offset.get("totalSeconds").getAsInt();
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(totalSeconds);

        JsonObject zone = jsonObject.getAsJsonObject("zone");
        String zoneId = zone.get("id").getAsString();
        ZoneId zoneIdObj = ZoneId.of(zoneId);

        return ZonedDateTime.of(localDateTime, zoneOffset).withZoneSameInstant(zoneIdObj);
    }
}