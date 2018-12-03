package app.mediabrainz.data.room.repository;


import android.os.AsyncTask;
import android.support.v4.util.Consumer;

import java.util.Collections;
import java.util.List;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.data.room.dao.RecommendDao;
import app.mediabrainz.data.room.entity.Recommend;
import app.mediabrainz.functions.Action;


public class RecommendRepository {

    private RecommendDao recommendDao;

    public RecommendRepository() {
        recommendDao = MediaBrainzApp.appDatabase.recommendDao();
    }

    public void getAll(Consumer<List<Recommend>> consumer) {
        new GetAllRecommendsTask(recommendDao,  consumer).execute();
    }

    public void setRecommends(List<Tag> tags) {
        new FindRecommendByTagTask(recommendDao, tags).execute();
    }

    public void deleteAll(Action postAction) {
        new DeleteAllRecommendsTask(recommendDao, postAction).execute();
    }

    static class FindRecommendByTagTask extends AsyncTask<Void, Void, Void> {
        private RecommendDao asyncRecommendDao;
        private List<Tag> tags;

        private FindRecommendByTagTask(RecommendDao asyncRecommendDao, List<Tag> tags) {
            this.asyncRecommendDao = asyncRecommendDao;
            this.tags = tags;
        }

        private Void insertTag(String tagName, int number) {
            Recommend recommend = asyncRecommendDao.findRecommendByTag(tagName);
            if (recommend == null) {
                asyncRecommendDao.insert(new Recommend(tagName, number));
            } else {
                recommend.setNumber(recommend.getNumber() + number);
                asyncRecommendDao.insert(recommend);
            }
            return null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (tags != null && !tags.isEmpty()) {
                Collections.sort(tags, (t1, t2) -> t2.getCount() - t1.getCount());
                Tag tag1 = tags.get(0);
                if (tags.size() == 1) {
                    insertTag(tag1.getName(), 1);
                } else {
                    Tag tag2 = tags.get(1);
                    insertTag(tag2.getName(), 1);
                    insertTag(tag1.getName(), tag1.getCount() > tag2.getCount() ? 2 : 1);
                }
            }
            return null;
        }
    }

    static class DeleteAllRecommendsTask extends AsyncTask<Void, Void, Void> {
        private RecommendDao asyncRecommendDao;
        private Action postAction;

        private DeleteAllRecommendsTask(RecommendDao asyncRecommendDao, Action postAction) {
            this.asyncRecommendDao = asyncRecommendDao;
            this.postAction = postAction;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncRecommendDao.deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (postAction != null) {
                postAction.run();
            }
        }
    }

    static class GetAllRecommendsTask extends AsyncTask<Void, Void, List<Recommend>> {

        private RecommendDao asyncRecommendDao;
        private Consumer<List<Recommend>> consumer;

        private GetAllRecommendsTask(RecommendDao asyncRecommendDao, Consumer<List<Recommend>> consumer) {
            this.asyncRecommendDao = asyncRecommendDao;
            this.consumer = consumer;
        }

        @Override
        protected List<Recommend> doInBackground(Void... voids) {
            return asyncRecommendDao.getAllRecommends();
        }

        @Override
        protected void onPostExecute(List<Recommend> recommends) {
            super.onPostExecute(recommends);
            if (consumer != null) {
                consumer.accept(recommends);
            }
        }
    }

}
