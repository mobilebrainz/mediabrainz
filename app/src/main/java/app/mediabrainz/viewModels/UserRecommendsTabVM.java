package app.mediabrainz.viewModels;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.api.site.TagEntity;
import app.mediabrainz.api.site.TagServiceInterface;
import app.mediabrainz.data.room.repository.RecommendRepository;

import static app.mediabrainz.MediaBrainzApp.api;


public class UserRecommendsTabVM extends BaseViewModel {


    private final int LIMIT_RECOMMENDS = 100; //range: 0 - 200
    private TagServiceInterface.TagType tagType;
    private String secondaryTag;
    private int tagRate = 0;
    private RecommendRepository recommendRepository = new RecommendRepository();

    public MutableLiveData<Resource<List<TagEntity>>> recommendsResource = new MutableLiveData<>();

    public UserRecommendsTabVM(TagServiceInterface.TagType tagType) {
        this.tagType = tagType;
    }

    public void load() {
        recommendsResource.setValue(Resource.loading());
        recommendRepository.getAll(recommends -> {
            if (!recommends.isEmpty()) {
                String primaryTag = recommends.get(0).getTag();
                int primaryNumber = recommends.get(0).getNumber();
                if (recommends.size() > 1) {
                    secondaryTag = recommends.get(1).getTag();
                    int secondaryNumber = recommends.get(1).getNumber();
                    tagRate = primaryNumber / secondaryNumber;
                }
                compositeDisposable.add(api.getTagEntities(tagType, primaryTag, 1,
                        primaryPage -> {
                            if (secondaryTag != null) {
                                api.getTagEntities(tagType, secondaryTag, 1,
                                        secondaryPage -> handleResult(primaryPage, secondaryPage),
                                        throwable -> recommendsResource.postValue(Resource.error(throwable)));
                            } else {
                                handleResult(primaryPage, null);
                            }
                        },
                        throwable -> recommendsResource.postValue(Resource.error(throwable))));
            } else {
                recommendsResource.postValue(Resource.success(new ArrayList<>()));
            }
        });
    }

    // сделать общий список из соотношения тегов rateTags
    // перемешать из соотношения rateTags (напр. rateTags=2.6, округл до 2 и тогда после каждых 2 primaryTag должен идти 1 secondaryTag
    private void handleResult(TagEntity.Page primaryPage, TagEntity.Page secondaryPage) {
        final List<TagEntity> recommends = new ArrayList<>();
        if (secondaryPage == null || secondaryPage.getTagEntities().isEmpty()) {
            recommends.addAll(primaryPage.getTagEntities());
        } else {
            int i = 0;
            int k = 0;
            List<TagEntity> secondaryEntities = secondaryPage.getTagEntities();
            int secondarySize = secondaryEntities.size();
            for (TagEntity primaryEntity : primaryPage.getTagEntities()) {
                recommends.add(primaryEntity);
                i++;
                if (i % tagRate == 0 && secondarySize > k) {
                    recommends.add(secondaryEntities.get(k));
                    k++;
                }
                if (i + k == LIMIT_RECOMMENDS) {
                    break;
                }
            }
        }
        recommendsResource.postValue(Resource.success(recommends));
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final TagServiceInterface.TagType tagType;

        public Factory(@NonNull TagServiceInterface.TagType tagType) {
            super();
            this.tagType = tagType;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserRecommendsTabVM(tagType);
        }
    }

}
