package app.mediabrainz.util;


import android.content.Context;
import android.content.res.Resources;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import app.mediabrainz.R;
import app.mediabrainz.api.model.Media;


public class StringFormat {

    public static String buildReleaseFormatsString(Context c, List<Media> medias) {

        Map<String, Integer> formatCounts = getFormatCounts(medias);
        Set<String> formatKeys = formatCounts.keySet();

        if (formatKeys.isEmpty()) {
            return "";
        }

        Resources res = c.getResources();
        StringBuilder sb = new StringBuilder();

        for (String format : formatKeys) {
            if (format == null) {
                sb.append(", ");
                continue;
            }
            Integer number = formatCounts.get(format);
            if (number > 1) {
                sb.append(number + "x");
            }
            if (format.equals("cd")) {
                sb.append(res.getString(R.string.fm_cd));
            } else if (format.equals("vinyl")) {
                sb.append(res.getString(R.string.fm_vinyl));
            } else if (format.equals("cassette")) {
                sb.append(res.getString(R.string.fm_cassette));
            } else if (format.equals("dvd")) {
                sb.append(res.getString(R.string.fm_dvd));
            } else if (format.equals("digital media")) {
                sb.append(res.getString(R.string.fm_dm));
            } else if (format.equals("sacd")) {
                sb.append(res.getString(R.string.fm_sacd));
            } else if (format.equals("dualdisc")) {
                sb.append(res.getString(R.string.fm_dd));
            } else if (format.equals("laserdisc")) {
                sb.append(res.getString(R.string.fm_ld));
            } else if (format.equals("minidisc")) {
                sb.append(res.getString(R.string.fm_md));
            } else if (format.equals("cartridge")) {
                sb.append(res.getString(R.string.fm_cartridge));
            } else if (format.equals("reel-to-reel")) {
                sb.append(res.getString(R.string.fm_rtr));
            } else if (format.equals("dat")) {
                sb.append(res.getString(R.string.fm_dat));
            } else if (format.equals("other")) {
                sb.append(res.getString(R.string.fm_other));
            } else if (format.equals("wax cylinder")) {
                sb.append(res.getString(R.string.fm_wax));
            } else if (format.equals("piano roll")) {
                sb.append(res.getString(R.string.fm_pr));
            } else if (format.equals("digital compact cassette")) {
                sb.append(res.getString(R.string.fm_dcc));
            } else if (format.equals("vhs")) {
                sb.append(res.getString(R.string.fm_vhs));
            } else if (format.equals("video-cd")) {
                sb.append(res.getString(R.string.fm_vcd));
            } else if (format.equals("super video-cd")) {
                sb.append(res.getString(R.string.fm_svcd));
            } else if (format.equals("betamax")) {
                sb.append(res.getString(R.string.fm_bm));
            } else if (format.equals("hd compatible digital")) {
                sb.append(res.getString(R.string.fm_hdcd));
            } else if (format.equals("usb flash drive")) {
                sb.append(res.getString(R.string.fm_usb));
            } else if (format.equals("slotmusic")) {
                sb.append(res.getString(R.string.fm_sm));
            } else if (format.equals("universal media disc")) {
                sb.append(res.getString(R.string.fm_umd));
            } else if (format.equals("hd-dvd")) {
                sb.append(res.getString(R.string.fm_hddvd));
            } else if (format.equals("dvd-audio")) {
                sb.append(res.getString(R.string.fm_dvda));
            } else if (format.equals("dvd-video")) {
                sb.append(res.getString(R.string.fm_dvdv));
            } else if (format.equals("blu-ray")) {
                sb.append(res.getString(R.string.fm_br));
            } else {
                sb.append(format);
            }
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    private static Map<String, Integer> getFormatCounts(List<Media> medias) {
        Map<String, Integer> formatCounts = new HashMap<>();
        for (Media media : medias) {
            Integer count = formatCounts.get(media.getFormat());
            formatCounts.put(media.getFormat(), (count == null) ? 1 : count + 1);
        }
        return formatCounts;
    }

    static public String decimalFormat(long value) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(value);
    }

    public static String join(String delimiter, List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            sb.append(str + delimiter);
        }
        return sb.substring(0, sb.length() - delimiter.length());
    }

}
