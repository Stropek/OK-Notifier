package com.przemolab.oknotifier.services;

import com.przemolab.oknotifier.models.Contest;
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

    private Contest getContest(Element row) {
        try {
            Elements cells = row.select("td");

            String name = cells.get(0).select("a").text();
            String contestUrl = String.format("%s%s", BaseKattisUrl, cells.get(0).select("a").attr("href"));
            String[] parts = contestUrl.split("/");
            String id = parts[parts.length - 1];

            String startDateText = cells.get(3).text().replace(" CEST", "");
            Date startDate = DateUtils.getDate(startDateText);

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
}

