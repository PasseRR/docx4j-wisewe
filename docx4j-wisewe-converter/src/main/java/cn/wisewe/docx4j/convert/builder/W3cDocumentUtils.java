package cn.wisewe.docx4j.convert.builder;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * w3c html{@link Document}工具
 * @author xiehai
 * @date 2022/03/24 10:27
 */
public interface W3cDocumentUtils {
    /**
     * html {@link Document} 移动端支持
     * @param document {@link Document}
     */
    static void mobileSupport(Document document) {
        NodeList head = document.getElementsByTagName("head");
        if (head.getLength() > 0) {
            Element meta = document.createElement("meta");
            meta.setAttribute("name", "viewport");
            meta.setAttribute(
                "content",
                "width=device-width,height=device-height, user-scalable=no,initial-scale=1, minimum-scale=1," +
                    "maximum-scale=1,target-densitydpi=device-dpi"
            );
            head.item(0).appendChild(meta);
        }
    }
}
