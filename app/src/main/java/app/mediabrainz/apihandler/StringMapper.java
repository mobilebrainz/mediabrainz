package app.mediabrainz.apihandler;

import android.content.res.Resources;
import android.text.TextUtils;

import java.util.List;

import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.ReleaseGroup.PrimaryType;
import app.mediabrainz.api.model.ReleaseGroup.SecondaryType;


public class StringMapper {

    public static String mapReleaseGroupTypeString(ReleaseGroup releaseGroup) {
        Resources res = MediaBrainzApp.getContext().getResources();
        String primaryType = releaseGroup.getPrimaryType();
        List<String> secondaryTypes = releaseGroup.getSecondaryTypes();
        String result = "";

        if (!TextUtils.isEmpty(primaryType)) {
            if (primaryType.equalsIgnoreCase(PrimaryType.ALBUM.toString())) {
                result = res.getString(R.string.rt_album);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.BROADCAST.toString())) {
                result = res.getString(R.string.rt_broadcast);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.EP.toString())) {
                result = res.getString(R.string.rt_ep);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.SINGLE.toString())) {
                result = res.getString(R.string.rt_single);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.OTHER.toString())) {
                result = res.getString(R.string.rt_other);
            }
        }
        String secResult = "";
        if (secondaryTypes != null && !secondaryTypes.isEmpty()) {
            String secondaryType = secondaryTypes.get(0);
            if (secondaryType.equalsIgnoreCase(SecondaryType.AUDIOBOOK.toString())) {
                secResult = res.getString(R.string.rt_audiobook);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.COMPILATION.toString())) {
                secResult = res.getString(R.string.rt_compilation);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.DJ_MIX.toString())) {
                secResult = res.getString(R.string.rt_dj_mix);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.INTERVIEW.toString())) {
                secResult = res.getString(R.string.rt_interview);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.LIVE.toString())) {
                secResult = res.getString(R.string.rt_live);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.MIXTAPE.toString())) {
                secResult = res.getString(R.string.rt_mixtape);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.REMIX.toString())) {
                secResult = res.getString(R.string.rt_remix);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.SOUNDTRACK.toString())) {
                secResult = res.getString(R.string.rt_soundtrack);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.SPOKENWORD.toString())) {
                secResult = res.getString(R.string.rt_spokenword);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.STREET.toString())) {
                secResult = res.getString(R.string.rt_street);
            }
        }
        if (!result.equals("") && !secResult.equals("")) {
            return result + "/" + secResult;
        } else if (!result.equals("") && secResult.equals("")) {
            return result;
        } else if (result.equals("") && !secResult.equals("")) {
            return secResult;
        }
        return res.getString(R.string.rt_unknown);
    }

    public static String mapReleaseGroupPrimaryType(ReleaseGroup releaseGroup) {
        Resources res = MediaBrainzApp.getContext().getResources();
        String primaryType = releaseGroup.getPrimaryType();
        String result = "";
        if (!TextUtils.isEmpty(primaryType)) {
            if (primaryType.equalsIgnoreCase(PrimaryType.ALBUM.toString())) {
                result = res.getString(R.string.rt_album);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.BROADCAST.toString())) {
                result = res.getString(R.string.rt_broadcast);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.EP.toString())) {
                result = res.getString(R.string.rt_ep);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.SINGLE.toString())) {
                result = res.getString(R.string.rt_single);
            } else if (primaryType.equalsIgnoreCase(PrimaryType.OTHER.toString())) {
                result = res.getString(R.string.rt_other);
            }
        }
        return result;
    }

    public static String mapReleaseGroupSecondaryType(ReleaseGroup releaseGroup) {
        Resources res = MediaBrainzApp.getContext().getResources();
        List<String> secondaryTypes = releaseGroup.getSecondaryTypes();
        String result = "";
        if (secondaryTypes != null && !secondaryTypes.isEmpty()) {
            String secondaryType = secondaryTypes.get(0);
            if (secondaryType.equalsIgnoreCase(SecondaryType.AUDIOBOOK.toString())) {
                result = res.getString(R.string.rt_audiobook);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.COMPILATION.toString())) {
                result = res.getString(R.string.rt_compilation);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.DJ_MIX.toString())) {
                result = res.getString(R.string.rt_dj_mix);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.INTERVIEW.toString())) {
                result = res.getString(R.string.rt_interview);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.LIVE.toString())) {
                result = res.getString(R.string.rt_live);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.MIXTAPE.toString())) {
                result = res.getString(R.string.rt_mixtape);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.REMIX.toString())) {
                result = res.getString(R.string.rt_remix);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.SOUNDTRACK.toString())) {
                result = res.getString(R.string.rt_soundtrack);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.SPOKENWORD.toString())) {
                result = res.getString(R.string.rt_spokenword);
            } else if (secondaryType.equalsIgnoreCase(SecondaryType.STREET.toString())) {
                result = res.getString(R.string.rt_street);
            }
        }
        return result;
    }

    public static String mapReleaseGroupOneType(ReleaseGroup releaseGroup) {
        String secondaryType = mapReleaseGroupSecondaryType(releaseGroup);
        return !secondaryType.equals("") ? secondaryType : mapReleaseGroupPrimaryType(releaseGroup);
    }

    public static String mapReleaseGroupAllType(ReleaseGroup releaseGroup) {
        String primaryType = mapReleaseGroupPrimaryType(releaseGroup);
        String secondaryType = mapReleaseGroupSecondaryType(releaseGroup);
        if (!primaryType.equals("") && !secondaryType.equals("")) {
            return primaryType + "/" + secondaryType;
        } else if (!primaryType.equals("") && secondaryType.equals("")) {
            return primaryType;
        } else if (primaryType.equals("") && !secondaryType.equals("")) {
            return secondaryType;
        } else {
            return "";
        }
    }

}
