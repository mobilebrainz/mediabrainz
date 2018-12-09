package app.mediabrainz.data.room.repository;


import android.os.AsyncTask;
import android.support.v4.util.Consumer;

import java.util.List;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.data.room.dao.UserDao;
import app.mediabrainz.data.room.entity.User;
import app.mediabrainz.functions.Action;


public class UserRepository {

    private UserDao userDao;

    public UserRepository() {
        userDao = MediaBrainzApp.appDatabase.userDao();
    }

    public void insert(User... users) {
        insert(null, users);
    }

    public void insert(Action postAction, User... users) {
        new InsertUserTask(userDao, postAction).execute(users);
    }

    public void deleteAll(Action postAction) {
        new DeleteAllUsersTask(userDao, postAction).execute();
    }

    public void delete(Action postAction, User... users) {
        new DeleteUsersTask(userDao, postAction).execute(users);
    }

    public void getUsers(Consumer<List<User>> consumer) {
        new GetUsersTask(userDao, consumer).execute();
    }

    public void findUser(String username, Consumer<User> consumer) {
        new FindUserTask(userDao, consumer).execute(username);
    }

    static class FindUserTask extends AsyncTask<String, Void, User> {
        private UserDao userDao;
        private Consumer<User> consumer;

        private FindUserTask(UserDao userDao, Consumer<User> consumer) {
            this.userDao = userDao;
            this.consumer = consumer;
        }

        @Override
        protected User doInBackground(String... where) {
            return userDao.findUser(where[0]);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (consumer != null) {
                consumer.accept(user);
            }
        }
    }

    static class GetUsersTask extends AsyncTask<Void, Void, List<User>> {
        private UserDao userDao;
        private Consumer<List<User>> consumer;

        private GetUsersTask(UserDao userDao, Consumer<List<User>> consumer) {
            this.userDao = userDao;
            this.consumer = consumer;
        }

        @Override
        protected List<User> doInBackground(Void... where) {
            return userDao.getAllUsers();
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            if (consumer != null) {
                consumer.accept(users);
            }
        }
    }

    static class DeleteUsersTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;
        private Action postAction;

        private DeleteUsersTask(UserDao userDao, Action postAction) {
            this.userDao = userDao;
            this.postAction = postAction;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.deleteUser(users);
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

    static class DeleteAllUsersTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;
        private Action postAction;

        private DeleteAllUsersTask(UserDao userDao, Action postAction) {
            this.userDao = userDao;
            this.postAction = postAction;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAll();
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


    static class InsertUserTask extends AsyncTask<User, Void, Void> {
        private UserDao asyncUserDao;
        private Action postAction;

        private InsertUserTask(UserDao userDao, Action postAction) {
            this.asyncUserDao = userDao;
            this.postAction = postAction;
        }

        @Override
        protected Void doInBackground(User... users) {
            asyncUserDao.insert(users);
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
