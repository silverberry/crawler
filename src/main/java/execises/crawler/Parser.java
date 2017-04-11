package execises.crawler;

import java.util.function.Function;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.*;

// A simple HTML parser that takes a String->void callback as a constructor argument.
// The callback is called on the text value of any "href" attribute inside an "a" HTML tag.
public class Parser extends HTMLEditorKit.ParserCallback {
    private Function<String, Void> callback;

    Parser(Function<String, Void> callback) {
        this.callback = callback;
    }

    @Override
    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
        handleTag(tag, attrs);
    }

    @Override
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
        handleTag(tag, attrs);
    }

    private void handleTag(HTML.Tag tag, MutableAttributeSet attrs) {
        if (HTML.Tag.A == tag) {
            Object href = attrs.getAttribute(HTML.Attribute.HREF);
            if (href != null) {
                callback.apply(href.toString());
            }
        }
    }
}
