package com.przemolab.oknotifier.modules;

import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.utils.DateUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class OpenKattisService {

    private final String BaseKattisUrl = "https://open.kattis.com";

    public List<Contest> getOngoingContests() {
        try {
            List<Contest> contests = new ArrayList<>();

            String url = String.format("%s/contests", BaseKattisUrl);
            Document contestsPageDocument = Jsoup.connect(url).timeout(0).get();
            Elements elements = contestsPageDocument.select(".main-content table:first-of-type tbody tr");

            for (Element row : elements) {
                Contest contest = getContest(row);
                if (contest != null) {
                    contests.add(contest);
                }
            }

            return contests;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    public List<Contestant> getContestStandings(String contestId) {
        try {
            List<Contestant> contestants = new ArrayList<>();

            String url = String.format("%s/contests/%s", BaseKattisUrl, contestId);
            Document contestsPageDocument = Jsoup.connect(url).timeout(0).get();
            Elements elements = contestsPageDocument.select("#standings tbody tr");

            for (Element row : elements) {
                Contestant contestant = getContestant(row, contestId);
                if (contestant != null) {
                    contestants.add(contestant);
                }
            }

            return contestants;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    private Contest getContest(Element row) {
        try {
            Elements cells = row.select("td");

            String name = cells.get(0).select("a").text();
            String contestUrl = String.format("%s%s", BaseKattisUrl, cells.get(0).select("a").attr("href"));
            String[] parts = contestUrl.split("/");
            String id = parts[parts.length - 1];

            String startDateText = cells.get(3).text().replace(" CEST", "");

            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            if (startDateText.length() == 8) {
                dateTimeFormat = "HH:mm:ss";
            }
            Date startDate = DateUtils.getDate(startDateText, dateTimeFormat);

            String lengthText = cells.get(2).text();
            Date endDate = DateUtils.addTimeToDate(startDate, lengthText);

            Document standingsPageDocument = Jsoup.connect(contestUrl).timeout(0).get();
            int numberOfContestants = standingsPageDocument.select("#standings tbody tr").size() - 4;
            int numberOfProblems = standingsPageDocument.select("#standings thead th.problemcolheader-standings").size();

            return new Contest(id, name, startDate, endDate, numberOfContestants, numberOfProblems);
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    private Contestant getContestant(Element row, String contestId) {
        try {
            Elements cells = row.select("td");

            String rank = cells.get(0).select(".rank").text();

            if (!rank.isEmpty()) {

                String name = cells.get(1).select("a").text();
                if (name.isEmpty()) {
                    name = cells.get(1).select("div").text();
                }
                if (name.isEmpty()) {
                    name = cells.get(1).select("em").text();
                }

                int time = Integer.valueOf(cells.get(3).text());

                int solved = 0;
                int submitted = 0;
                int failed = 0;
                int notTried = 0;
                for (Element cell : cells) {
                    if (cell.hasClass("solved")) {
                        solved++;
                    } else if (cell.hasClass("pending")) {
                        submitted++;
                    } else if (cell.hasClass("attempted")) {
                        failed++;
                    } else if (!cell.hasText()) {
                        notTried++;
                    }
                }

                return new Contestant(name, contestId, solved, submitted, failed, notTried, time);
            }

            return null;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }
}
