package app.mediabrainz.data.room.repository;


import android.os.AsyncTask;
import android.support.v4.util.Consumer;

import java.util.List;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.data.room.dao.SuggestionDao;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.functions.Action;


public class SuggestionRepository {

    private SuggestionDao suggestionDao;

    public SuggestionRepository() {
        suggestionDao = MediaBrainzApp.appDatabase.suggestionDao();
    }

    public void insert(Suggestion suggestion) {
        insert(suggestion, null);
    }

    public void insert(Suggestion suggestion, Action postAction) {
        new InsertSuggestionTask(suggestionDao, postAction).execute(suggestion);
    }

    public void deleteAll(Action postAction) {
        new DeleteAllSuggestionsTask(suggestionDao, postAction).execute();
    }

    public void selectSuggestions(String word, String field, Consumer<List<Suggestion>> consumer) {
        new FindSuggestionsByWordAndField(suggestionDao, consumer).execute(word, field);
    }

    public List<Suggestion> getSuggestions(String word, String field) {
        return suggestionDao.findSuggestionsByWordAndField(word+"%", field);
    }

    static class FindSuggestionsByWordAndField extends AsyncTask<String, Void, List<Suggestion>> {
        private SuggestionDao asyncSuggestionDao;
        private Consumer<List<Suggestion>> consumer;

        private FindSuggestionsByWordAndField(SuggestionDao suggestionDao, Consumer<List<Suggestion>> consumer) {
            this.asyncSuggestionDao = suggestionDao;
            this.consumer = consumer;
        }

        @Override
        protected List<Suggestion> doInBackground(String... where) {
            return asyncSuggestionDao.findSuggestionsByWordAndField(where[0], where[1]);
        }

        @Override
        protected void onPostExecute(List<Suggestion> recommends) {
            super.onPostExecute(recommends);
            if (consumer != null) {
                consumer.accept(recommends);
            }
        }
    }

    static class InsertSuggestionTask extends AsyncTask<Suggestion, Void, Void> {
        private SuggestionDao asyncSuggestionDao;
        private Action postAction;

        private InsertSuggestionTask(SuggestionDao suggestionDao, Action postAction) {
            this.asyncSuggestionDao = suggestionDao;
            this.postAction = postAction;
        }

        @Override
        protected Void doInBackground(Suggestion... suggestions) {
            asyncSuggestionDao.insert(suggestions);
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

    static class DeleteAllSuggestionsTask extends AsyncTask<Void, Void, Void> {
        private SuggestionDao asyncSuggestionDao;
        private Action postAction;

        private DeleteAllSuggestionsTask(SuggestionDao suggestionDao, Action postAction) {
            this.asyncSuggestionDao = suggestionDao;
            this.postAction = postAction;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncSuggestionDao.deleteAll();
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

}
