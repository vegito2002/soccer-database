package com.soccerDatabase;

import javax.xml.stream.events.EndDocument;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class CSVReader {
    private static String CVS_FILE_NAME = "E0.CSV";
    private static String CVS_SEPARATER = ",";

    public List<EnglandMatch> readMatchData() {
        String line = "";
        List<EnglandMatch> matches = new ArrayList<EnglandMatch>();

        try (BufferedReader br = new BufferedReader(new FileReader(CVS_FILE_NAME))) {

            while ((line = br.readLine()) != null) {

                String[] entry = line.split(CVS_SEPARATER);

                if (entry[4].contains("F")) continue;

                int fthg = Integer.parseInt(entry[4]);
                int ftag = Integer.parseInt(entry[5]);
                int hthg = Integer.parseInt(entry[7]);
                int htag = Integer.parseInt(entry[8]);
                int hs = Integer.parseInt(entry[11]);
                int as = Integer.parseInt(entry[12]);
                int hst = Integer.parseInt(entry[13]);
                int ast = Integer.parseInt(entry[14]);
                int hf = Integer.parseInt(entry[15]);
                int af = Integer.parseInt(entry[16]);
                int hc = Integer.parseInt(entry[17]);
                int ac = Integer.parseInt(entry[18]);
                int hy = Integer.parseInt(entry[19]);
                int ay = Integer.parseInt(entry[20]);
                int hr = Integer.parseInt(entry[21]);
                int ar = Integer.parseInt(entry[22]);

                String dateString = entry[1];
                String[] date = dateString.split("/");
                int dayOfDate = Integer.parseInt(date[0]);
                int monthOfDate = Integer.parseInt(date[1]);
                int yearOfDate = Integer.parseInt(date[2]);

                String monthOfDateString = (monthOfDate<10) ? String.format("0%d", monthOfDate) : String.format("%d", monthOfDate);
                String dayOfDateString = (dayOfDate<10) ? String.format("0%d", dayOfDate) : String.format("%d", dayOfDate);

                String isoDate = String.format("20%d-%s-%sT00:00:00.000", yearOfDate, monthOfDateString, dayOfDateString);

                LocalDate dateLocal = LocalDate.parse(isoDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                int dateInt = 86400 * (int) dateLocal.toEpochDay();

                String homeTeam = entry[2];
                String awayTeam = entry[3];
                String ftr = entry[6];
                String htr = entry[9];
                String referee = entry[10];

                EnglandMatch newMatch = new EnglandMatch(dateString, dateInt, homeTeam, awayTeam, ftr, htr, referee, fthg, ftag, hthg, htag, hs, as, hst, ast, hf, af, hc, ac, hy, ay, hr, ar);

                matches.add(newMatch);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return matches;
    }

}