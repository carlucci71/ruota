package it.ddlsolution.ruota.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;


@Component
@Slf4j
public class Utility {
    @Autowired
    Environment environment;

    @Autowired
    RestTemplate restTemplate;

    public final static SimpleDateFormat dateFormatLite = new SimpleDateFormat("yyyyMMdd");
    public final static String dateFormatLiteWithTime = "yyyyMMddHHmm";
    public final static String dateFormatReadable = "dd/MM/yyyy HH:mm";
    public final static String dateFormatLiteWithTimeAndSeconds = "yyyyMMddHHmmss";
    ObjectMapper mapper = null;

    public StopWatch startStopWatch(String nome) {
        StopWatch stopWatch = new StopWatch(nome);
        stopWatch.start();
        return stopWatch;
    }

    public void stopStopWatch(StopWatch stopWatch) {
        stopStopWatch(stopWatch, true);
    }

    public void stopStopWatch(StopWatch stopWatch, boolean logga) {
        stopWatch.stop();
        if (logga) {
            log.info("process {} executed in {} ms", stopWatch.getId(), stopWatch.getTotalTimeMillis());
        }
    }

    public  String getInSeconds(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ofPattern(dateFormatLiteWithTimeAndSeconds));

    }

    public  LocalDateTime toLocalDateTimeItaly(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatLiteWithTime).withLocale(Locale.ITALY);
        return LocalDateTime.parse(dateString, formatter);
    }

    public  LocalDateTime toLocalDateTimeItalyReadable(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatReadable).withLocale(Locale.ITALY);
        return LocalDateTime.parse(dateString, formatter);
    }

    public  String toLocalDateTimeItalyReadable(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatReadable).withLocale(Locale.ITALY);
        return localDateTime.format(formatter);
    }


    public <T> T callUrl(String url, Class<T> clazz) {
        log.info("++++++++++++++++++++++++++++++ {}",url);
        try {
            ResponseEntity<T> forEntity = restTemplate.getForEntity(url, clazz);
            return forEntity.getBody();
        } catch (Exception e){
            log.error("Errore in {} {}", url,e);
            throw e;
        }
    }

    private ObjectMapper getMapper() {
        if (mapper == null) {
            mapperGetInstance();
        }
        return mapper;
    }

    private synchronized void mapperGetInstance() {
        if (mapper == null) {
            mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
    }

    public String toJson(Object o) {
        try {
            byte[] data = getMapper().writeValueAsBytes(o);
            return new String(data, StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson(Object o, Charset charset) {
        try {
            byte[] data = getMapper().writeValueAsBytes(o);
            return new String(data, charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return getMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Calendar datasimpleToCalendar(String dataRiferimento) {
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"));
            calendar.setTime(dateFormatLite.parse(dataRiferimento));
            return calendar;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String calendarToDatasimple(Calendar dataRiferimento) {
        return dateFormatLite.format(dataRiferimento.getTime());
    }


    public String calendarToData(Calendar dataRiferimento, String pattern) {
        return new SimpleDateFormat(pattern).format(dataRiferimento.getTime());
    }


    public  BigDecimal bdFromDouble(Double d) {
        return new BigDecimal(String.valueOf(Double.toString(d)));
    }

    public  BigDecimal bdFromFloat(Float d) {
        return new BigDecimal(String.valueOf(Float.toString(d)));
    }

    public  long diffDateFromFormatLite(String primaData, String secondaData) {


        LocalDate primaDataL = LocalDate.of(Integer.parseInt(primaData.substring(0, 4)), Integer.parseInt(primaData.substring(4, 6)), Integer.parseInt(primaData.substring(6, 8)));
        LocalDate secondaDataL = LocalDate.of(Integer.parseInt(secondaData.substring(0, 4)), Integer.parseInt(secondaData.substring(4, 6)), Integer.parseInt(secondaData.substring(6, 8)));

        // Calcola la differenza tra le due date
        long differenzaGiorni = ChronoUnit.DAYS.between(secondaDataL, primaDataL);
        return differenzaGiorni;
    }

    public  BigDecimal roundBigDecimal(BigDecimal value, int newScale) {
        if (value == null) return BigDecimal.ZERO.stripTrailingZeros();
        return value.setScale(newScale, RoundingMode.HALF_UP);
    }



        private  final Random RANDOM = new Random();

        public  int randomUntil(int max) {
            if (max < 1) {
                throw new IllegalArgumentException("max deve essere >= 1");
            }
            return RANDOM.nextInt(max) + 1;
        }

}
