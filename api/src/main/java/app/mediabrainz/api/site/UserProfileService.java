package app.mediabrainz.api.site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;
import app.mediabrainz.api.core.WebService;
import app.mediabrainz.api.core.WebServiceInterface;

import static app.mediabrainz.api.Config.WEB_SERVICE;


public class UserProfileService implements UserProfileServiceInterface {

    private static final WebServiceInterface<SiteRetrofitService> webService =
            new WebService(SiteRetrofitService.class, WEB_SERVICE);

    public UserProfileService() {
    }

    @Override
    public Flowable<Result<UserProfile>> getUserProfile(String username) {
        return webService.getRetrofitService().getUserProfile(username)
                .flatMap((Function<Result<ResponseBody>, Flowable<Result<UserProfile>>>) result -> {
                    if (!result.isError()) {

                        ResponseBody errorBody = result.response().errorBody();
                        if (errorBody != null) {
                            String h1 = Jsoup.parse(errorBody.string()).getElementsByTag("h1").first().text();
                            if (h1.equals("Editor Not Found")) {
                                return Flowable.error(new EditorNotFoundException(username));
                            } else {
                                return Flowable.error(new EditorNotFoundException());
                            }
                        }

                        String html = result.response().body().string();
                        Document doc = Jsoup.parse(html);

                        UserProfile userProfile = new UserProfile(username);

                        String gravatar = doc.select("h1 img.gravatar").first().attr("src");
                        if (gravatar != null && !gravatar.equals("")) {
                            userProfile.setGravatar("https:" + gravatar);
                        }

                        Elements elements = doc.select("table.profileinfo tr");
                        for (Element tr : elements) {
                            String th = tr.getElementsByTag("th").first().text().trim().toLowerCase();
                            if (th != null) {
                                String td = tr.getElementsByTag("td").first().text().trim();
                                switch (th) {
                                    case "user type:":
                                        userProfile.setUserType(td);
                                        break;
                                    case "age:":
                                        userProfile.setAge(td);
                                        break;
                                    case "gender:":
                                        userProfile.setGender(td);
                                        break;
                                    case "location:":
                                        Elements as = tr.select("td a");
                                        for (Element a : as) {
                                            userProfile.getAreas().add(a.text().trim());
                                        }
                                        break;
                                    case "member since:":
                                        userProfile.setMemberSince(td);
                                        break;
                                    case "homepage:":
                                        userProfile.setHomepage(td);
                                        break;
                                    case "bio:":
                                        userProfile.setBio(td);
                                        break;
                                    case "languages:":
                                        Elements lis = tr.select("td li");
                                        for (Element li : lis) {
                                            userProfile.getLanguages().add(li.text().trim());
                                        }
                                        break;
                                }
                            }
                        }
                        return Flowable.just(Result.response(Response.success(userProfile)));
                    }
                    return Flowable.error(result.error());
                });

    }
}
