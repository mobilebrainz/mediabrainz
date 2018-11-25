package app.mediabrainz.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.AttributeSet;

import app.mediabrainz.util.MbUtils;


public class HtmlAssetTextView extends AppCompatTextView {
    
    private Context context;
    
    public HtmlAssetTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }
    
    public void setAsset(String asset) {
        setText(Html.fromHtml(MbUtils.stringFromAsset(context, asset)));
    }

}
