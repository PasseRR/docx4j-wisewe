package cn.wisewe.docx4j.convert.builder.sheet;

import cn.wisewe.docx4j.convert.builder.HtmlTransfer;
import com.aspose.cells.HtmlSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * excel转html处理器
 * @author xiehai
 * @date 2022/03/25 12:34
 */
class HtmlHandler extends SpreadSheetHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();
    private static final HtmlSaveOptions HTML_SAVE_OPTIONS = new HtmlSaveOptions();

    static {
        // 图片base64编码
        HTML_SAVE_OPTIONS.setExportImagesAsBase64(true);
        // css文件不独立
        HTML_SAVE_OPTIONS.setExportWorksheetCSSSeparately(false);
        // 只导出当前活动的sheet
        HTML_SAVE_OPTIONS.setExportActiveWorksheetOnly(true);
    }

    private static final String IFRAME_PREFIX = "iframe";
    private static final String NAV_SUFFIX = "nav";
    /**
     * 需要移除的link rel属性值
     */
    private static final Set<String> LINKS = new HashSet<>(
        Arrays.asList("File-List", "Edit-Time-Data", "OLE-Object-Data")
    );

    private HtmlHandler() {

    }

    @Override
    protected void postHandle(Workbook workbook, OutputStream outputStream) {
        WorksheetCollection worksheets = workbook.getWorksheets();
        Document document = getDocument();
        // 添加移动端支持
        HtmlTransfer.appendMeta(document);
        int size = worksheets.getCount();
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
                .append(
                    String.format("document.getElementById(holder + '%s').classList.remove('active');\n", NAV_SUFFIX))
                .append("document.getElementById(holder).style.display='none'; \n")
                .append("}\n")
                .append("holder = id; \n")
                .append(String.format("document.getElementById(id + '%s').classList.add('active');\n", NAV_SUFFIX))
                .append("document.getElementById(id).style.display='inline'; \n")
                .append("}\n");
            IntStream.range(0, size)
                .mapToObj(workbook.getWorksheets()::get)
                .map(Worksheet.class::cast)
                .forEach(it -> {
                    int index = it.getIndex();
                    // 激活sheet
                    worksheets.setActiveSheetIndex(index);
                    String id = IFRAME_PREFIX + index;
                    // 添加导航
                    div.appendChild(this.navElement(document, it.getName(), id));
                    // 添加iframe
                    this.appendIframe(document, id);
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        HtmlTransfer.create(os -> {
                                try {
                                    workbook.save(os, HTML_SAVE_OPTIONS);
                                } catch (Exception e) {
                                    throw new SpreadSheetConvertException(e);
                                }
                            })
                            .handle(d -> {
                                // 移除无用的link标签
                                d.head().getElementsByTag("link")
                                    .stream()
                                    .filter(e -> LINKS.contains(e.attr("rel")))
                                    .forEach(Element::remove);
                                // 移除script标签 反引号中不能有script标签
                                d.body().getElementsByTag("script")
                                    .stream()
                                    .filter(e -> e.html().endsWith("ChangeIEBase64();"))
                                    .forEach(Element::remove);
                            })
                            .transfer(baos);
                        this.appendScript(sb, id, new String(baos.toByteArray(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        throw new SpreadSheetConvertException(e.getMessage(), e);
                    }
                });
            // 菜单设置
            String style = "width: 100%;height: 30px;position: fixed;top: 0;left: 0;padding-left: 10px;" +
                "background-color: rgba(53, 53, 53, 1);line-height: 30px;font-size: 14px; opacity: .6";
            div.attr("style", style);
            div.attr("id", "header-nav");
            document.body().appendChild(div);
            sb.append(String.format("onNavClick('%s0');", IFRAME_PREFIX));
            script.append(sb.toString());
            document.body().appendChild(script);
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
     * 初始化html文档
     * @return {@link Document}
     */
    private static Document getDocument() {
        Document parse = Jsoup.parse("");
        Element style = parse.createElement("style");
        style.attr("type", "text/css");
        // 初始化导航点击样式
        String css = "a { padding: 5px; color: #f9f9f9; text-decoration:none; cursor:hand;}\n" +
            "a.active { color: #8df70c; }";
        style.append(css);
        parse.head().appendChild(style);
        return parse;
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
        a.attr("id", id + NAV_SUFFIX);
        a.attr("onclick", String.format("onNavClick('%s')", id));
        a.text(title);

        return a;
    }
}
