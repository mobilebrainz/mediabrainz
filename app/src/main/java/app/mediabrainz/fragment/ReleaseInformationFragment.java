package app.mediabrainz.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.R;
import app.mediabrainz.api.core.ApiUtils;
import app.mediabrainz.api.model.Area;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Label;
import app.mediabrainz.api.model.Media;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseEvent;
import app.mediabrainz.apihandler.StringMapper;
import app.mediabrainz.communicator.GetReleaseCommunicator;
import app.mediabrainz.util.MbUtils;
import app.mediabrainz.util.StringFormat;


public class ReleaseInformationFragment extends BaseFragment {

    private Release release;

    private TextView releaseNameView;
    private TextView releaseTypeYearView;
    private TextView artistNameView;
    private TableLayout releaseInfoTableView;

    public static ReleaseInformationFragment newInstance() {
        Bundle args = new Bundle();

        ReleaseInformationFragment fragment = new ReleaseInformationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_release_information, container);

        releaseNameView = layout.findViewById(R.id.releaseNameView);
        releaseTypeYearView = layout.findViewById(R.id.releaseTypeYearView);
        artistNameView = layout.findViewById(R.id.artistNameView);
        releaseInfoTableView = layout.findViewById(R.id.releaseInfoTableView);

        load();
        return layout;
    }


    public void load() {
        release = ((GetReleaseCommunicator) getContext()).getRelease();
        if (release != null) {
            setReleaseName();
            setArtistName();
            setTypeYear();
            setReleaseInfoTable();
        }
    }

    private void setReleaseName() {
        String name = release.getTitle();
        if (!TextUtils.isEmpty(name)) {
            releaseNameView.setVisibility(View.VISIBLE);
            releaseNameView.setText(name);
        } else {
            releaseNameView.setVisibility(View.GONE);
        }
    }

    private void setArtistName() {
        List<Artist.ArtistCredit> artistCredits = release.getReleaseGroup().getArtistCredits();
        List<String> artistNames = new ArrayList<>();
        for (Artist.ArtistCredit artistCredit : artistCredits) {
            artistNames.add(artistCredit.getName());
        }
        String artistNamesString = ApiUtils.getStringFromList(artistNames, ", ");
        if (!TextUtils.isEmpty(artistNamesString)) {
            artistNameView.setVisibility(View.VISIBLE);
            artistNameView.setText(artistNamesString);
        } else {
            artistNameView.setVisibility(View.GONE);
        }
    }

    private void setTypeYear() {
        String typeYearStr = ApiUtils.getStringFromArray(new String[]{
                StringMapper.mapReleaseGroupAllType(release.getReleaseGroup()),
                release.getReleaseGroup().getFirstReleaseDate()
        }, ", ");

        if (!TextUtils.isEmpty(typeYearStr)) {
            releaseTypeYearView.setVisibility(View.VISIBLE);
            releaseTypeYearView.setText(typeYearStr);
        } else {
            releaseTypeYearView.setVisibility(View.GONE);
        }
    }

    private void setReleaseInfoTable() {
        addFormatRow();
        addDetailsRow();
        addLabelRow();
        addReleasedRow();
        addLanguageRow();
        addBarcodeRow();
        addAsinRow();
    }

    private void addFormatRow() {
        String format = ApiUtils.getStringFromArray(new String[]{
                StringFormat.buildReleaseFormatsString(getContext(), release.getMedia()),
                release.getPackaging(),
                release.getStatus()
        }, ", ");
        if (!TextUtils.isEmpty(format)) {
            releaseInfoTableView.addView(getTableRow(getContext().getString(R.string.release_info_format), format));
        }
    }

    private void addDetailsRow() {
        int trackCount = 0;
        long length = 0;
        for (Media media : release.getMedia()) {
            trackCount += media.getTrackCount();
            List<Media.Track> tracks = media.getTracks();
            if (tracks != null && !tracks.isEmpty()) {
                for (Media.Track track : tracks) {
                    length += track.getLength() != null ? track.getLength() : 0;
                }
            }
        }
        releaseInfoTableView.addView(getTableRow(
                getContext().getString(R.string.release_info_details),
                getContext().getString(R.string.release_info_details_template, trackCount, MbUtils.formatTime(length))
        ));
    }

    private void addLabelRow() {
        List<Label.LabelInfo> labelInfos = release.getLabelInfo();
        String labelName = "";
        if (labelInfos != null && !labelInfos.isEmpty()) {
            Label label = labelInfos.get(0).getLabel();
            if (label != null) {
                labelName = label.getName();
            }
            String labelCatalog = labelInfos.get(0).getCatalogNumber();
            if (!TextUtils.isEmpty(labelCatalog)) {
                labelName += ", " + labelCatalog;
            }
            releaseInfoTableView.addView(getTableRow(
                    getContext().getString(R.string.release_info_label), labelName));
        }
    }

    private void addReleasedRow() {
        List<ReleaseEvent> releaseEvents = release.getReleaseEvents();
        if (releaseEvents != null && !releaseEvents.isEmpty()) {
            ReleaseEvent releaseEvent = releaseEvents.get(0);
            Area area = releaseEvent.getArea();
            String releasedStr = ApiUtils.getStringFromArray(new String[]{
                    (area != null) ? area.getName() : "",
                    releaseEvent.getDate()
            }, ", ");
            if (!TextUtils.isEmpty(releasedStr)) {
                releaseInfoTableView.addView(getTableRow(
                        getContext().getString(R.string.release_info_released), releasedStr));
            }
        }
    }

    private void addBarcodeRow() {
        if (!TextUtils.isEmpty(release.getBarcode())) {
            releaseInfoTableView.addView(getTableRow(
                    getContext().getString(R.string.release_info_barcode), release.getBarcode()));
        }
    }

    private void addAsinRow() {
        if (!TextUtils.isEmpty(release.getAsin())) {
            releaseInfoTableView.addView(getTableRow(
                    getContext().getString(R.string.release_info_asin), release.getAsin()));
        }
    }

    private void addLanguageRow() {
        Release.TextRepresentation textRepresentation = release.getTextRepresentation();
        if (textRepresentation != null && !TextUtils.isEmpty(textRepresentation.getLanguage())) {
            releaseInfoTableView.addView(getTableRow(
                    getContext().getString(R.string.release_info_language), textRepresentation.getLanguage()));
        }
    }

    private TableRow getTableRow(String... columns) {
        TableRow tableRow = new TableRow(getContext());
        tableRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < columns.length; i++) {
            TextView textView = new TextView(getContext());
            textView.setText(columns[i]);
            textView.setTextSize(14);
            textView.setPadding(10, 2, 10, 2);
            textView.setSingleLine(false);
            tableRow.addView(textView, i);
        }
        return tableRow;
    }

}
