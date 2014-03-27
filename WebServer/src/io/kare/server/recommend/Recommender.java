package io.kare.server.recommend;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author arshsab
 * @since 03 2014
 */

public class Recommender {
    private final DBCollection scores;
    private final DBCollection repos;

    public Recommender(DBCollection scores, DBCollection repos) {
        this.scores = scores;
        this.repos = repos;
    }

    public List<Recommendation> getLinearRecommendations(String repo) {
        List<Recommendation> ret = new ArrayList<>();

        for (DBObject obj : scores.find(new BasicDBObject("repo", repo))) {
            BasicDBObject recommendation = (BasicDBObject) obj;

            String otherName = recommendation.getString("other");
            int score = recommendation.getInt("score");

            BasicDBObject otherRepo = (BasicDBObject) repos.findOne(new BasicDBObject("name", otherName));

            double corrected = score / Math.sqrt(otherRepo.getInt("gazers"));

            ret.add(new Recommendation(repo, otherName, corrected));
        }

        Collections.sort(ret, (a, b) -> {
            double attempt = a.getScore() - b.getScore();

            if (attempt == 0.0) {
                return 0;
            }

            if (attempt > 0.0) {
                return -1;
            }

            return 1;
        });

        ret = ret.subList(0, Math.min(ret.size(), 75));

        return ret;
    }

    public List<Recommendation> getGraphRecommendations(String repo) {
        // todo

        return null;
    }
}
