package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.spire.xls.Workbook;
import com.spire.xls.collections.WorksheetsCollection;
import com.spire.xls.core.spreadsheet.HTMLOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

/**
 * excel转html处理器
 * @author xiehai
 * @date 2022/03/25 12:34
 */
class HtmlHandler extends SpreadSheetHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Workbook workbook, OutputStream outputStream) {
        HTMLOptions options = new HTMLOptions();
        options.setImageEmbedded(true);
        WorksheetsCollection worksheets = workbook.getWorksheets();
        Document document = Jsoup.parse("");
        // 添加移动端支持
        HtmlTransfer.appendMeta(document);
        int size = worksheets.size();
        if (size > 0) {
            Element script = document.createElement("script");
            script.attr("type", "text/javascript");
            Element div = document.createElement("div");
            StringBuilder sb = new StringBuilder();
            sb.append("var holder; \n");
            // sheet菜单点击事件
            sb.append("function onNavClick(id) {\n")
                .append("if(holder) { \n")
                .append("if(holder === id){return;}\n")
                .append("document.getElementById(holder).style.display='none'; \n")
                .append("}\n")
                .append("holder = id; \n")
                .append("document.getElementById(id).style.display='inline'; \n")
                .append("}");
            IntStream.range(0, size)
                .mapToObj(workbook.getWorksheets()::get)
                .forEach(it -> {
                    int index = it.getIndex();
                    String id = "iframe" + index;
                    // 添加导航
                    div.appendChild(this.navElement(document, it.getName(), id));
                    // 添加iframe
                    this.appendIframe(document, id);
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        HtmlTransfer.create(os -> it.saveToHtml(os, options))
                            .handle(d -> d.body().select("h2:lt(1)").remove())
                            .transfer(baos);
                        this.appendScript(sb, id, new String(baos.toByteArray(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        throw new SpreadSheetConvertException(e.getMessage(), e);
                    }
                });
            sb.append("onNavClick('iframe0');");
            script.append(sb.toString());
            document.body().appendChild(script);
            // 菜单设置
            String style = "width: 100%;height: 30px;position: fixed;top: 0;left: 0;padding-left: 10px;" +
                "background-color: rgba(53, 53, 53, 1);line-height: 30px;font-size: 14px; opacity: .8";
            div.attr("style", style);
            div.attr("id", "header-nav");
            document.body().appendChild(div);
        }

        // html转换
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write(document.html().getBytes(StandardCharsets.UTF_8));
            outputStream.write(baos.toByteArray());
        } catch (IOException e) {
            throw new SpreadSheetConvertException(e.getMessage(), e);
        }
    }

    /**
     * 内容追加
     * @param sb   脚本
     * @param id   iframe id
     * @param body iframe内容
     */
    private void appendScript(StringBuilder sb, String id, String body) {
        String temp = "t" + id;
        sb.append(String.format("var %s = document.getElementById(\"%s\");\n", id, id))
            .append(
                String.format(
                    "var %s = document.all ? %s.contentWindow.document : %s.contentDocument;\n", temp, id, id
                )
            )
            .append(String.format("%s.open();\n", temp))
            .append(String.format("%s.write(`%s`);\n", temp, body))
            .append(String.format("%s.close();\n", temp));
    }

    /**
     * 添加iframe标签
     * @param document {@link Document}
     * @param id       iframe id
     */
    private void appendIframe(Document document, String id) {
        Element iframe = document.createElement("iframe");
        iframe.attr("id", id);
        iframe.attr("frameborder", "0");
        iframe.attr("width", "100%");
        iframe.attr("height", "95%");
        iframe.attr("scrolling", "auto");
        // 同导航高度
        iframe.attr("style", "padding-top: 30px; display: none");
        document.body().appendChild(iframe);
    }

    /**
     * 添加导航元素
     * @param document {@link Document}
     * @param title    sheet标题
     * @param id       iframe id
     * @return {@link Element}
     */
    private Element navElement(Document document, String title, String id) {
        Element a = document.createElement("a");
        a.attr("href", "javascript:void(0)");
        a.attr("onclick", String.format("onNavClick('%s')", id));
        a.attr("style", "padding: 5px; color: #f9f9f9; text-decoration:none; border-right: 1px solid white");
        a.text(title);

        return a;
    }
}
