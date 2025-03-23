package site.easy.to.build.crm.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String convertDateFormat(LocalDate inputDate) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return inputDate.format(outputFormatter);
    }

    public static ZonedDateTime getCurrentDateTimeInMadagascar() {
        ZoneId madagascarZone = ZoneId.of("Indian/Antananarivo");
        return ZonedDateTime.now(madagascarZone);
    }
}
