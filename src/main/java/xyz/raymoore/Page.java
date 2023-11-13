package xyz.raymoore;

import net.jextra.tucker.tucker.Block;
import net.jextra.tucker.tucker.Tucker;

import java.io.File;
import java.io.IOException;

public class Page {
    public static final String TUCK = "src/main/webapp/page.thtml";

    private final Tucker tucker;
    private final Block root;

    private Block content;

    public Page() throws IOException {
        File file = new File(TUCK);
        this.tucker = new Tucker(file);
        this.root = tucker.buildBlock("root");
    }

    public Page(String title) throws IOException {
        this();
        root.setVariable("title", title);
    }

    public void addStylesheet(String path) {
        Block css = tucker.buildBlock("css");
        css.setVariable("path", path);
        root.insert("css", css);
    }

    public void addScript(String path) {
        Block js = tucker.buildBlock("js");
        js.setVariable("path", path);
        root.insert("js", js);
    }

    public void setTitle(String title) {
        root.setVariable("title", title);
    }

    public void setContent(Block content) {
        this.content = content;
    }

    public String render() {
        root.insert("content", content);

        return root.toString();
    }
}
