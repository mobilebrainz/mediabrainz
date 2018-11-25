package app.mediabrainz.suggestion;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;

import android.widget.ArrayAdapter;
import android.widget.FilterQueryProvider;

import app.mediabrainz.R;


public class SuggestionHelper {

    public static final String COLUMN = "display1";
    private static final String URI = "content://" + SuggestionProvider.AUTHORITY + "/suggestions";
    private static final String[] FROM = new String[]{COLUMN};
    private static final int[] TO = new int[]{R.id.dropdown_item};

    private final Context context;

    public SuggestionHelper(Context context) {
        this.context = context;
    }

    public SimpleCursorAdapter getAdapter() {
        return getAdapter(R.layout.layout_dropdown_item);
    }

    public SimpleCursorAdapter getAdapter(int layout) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, layout, null, FROM, TO, 0);
        adapter.setCursorToStringConverter(new SuggestionCursorToString());
        adapter.setFilterQueryProvider(new SuggestionFilterQuery());
        return adapter;
    }

    public ArrayAdapter<String> getEmptyAdapter() {
        return new ArrayAdapter<>(context, R.layout.layout_dropdown_item, new String[]{});
    }

    private class SuggestionCursorToString implements SimpleCursorAdapter.CursorToStringConverter {

        @Override
        public CharSequence convertToString(Cursor cursor) {
            int columnIndex = cursor.getColumnIndexOrThrow(COLUMN);
            return cursor.getString(columnIndex);
        }
    }

    private class SuggestionFilterQuery implements FilterQueryProvider {

        @Override
        public Cursor runQuery(CharSequence constraint) {
            return getMatchingEntries((constraint != null ? constraint.toString() : null));
        }
    }

    private Cursor getMatchingEntries(String constraint) {
        if (constraint == null) {
            return null;
        }

        String where = COLUMN + " LIKE ?";
        String[] args = {constraint.trim() + "%"};

        CursorLoader cursorLoader = new CursorLoader(context, Uri.parse(URI), null, where, args, COLUMN + " ASC");
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

}
