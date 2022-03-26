package cn.wisewe.docx4j.convert.builder.slide;

import cn.wisewe.docx4j.convert.office.OfficeDocumentHandler;
import cn.wisewe.docx4j.convert.utils.ImageUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hwpf.converter.HtmlDocumentFacade;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ppt转html 使用图片
 * @author xiehai
 * @date 2022/03/26 14:45
 */
class HtmlHandler extends OfficeDocumentHandler {
    static final HtmlHandler INSTANCE = new HtmlHandler();

    @Override
    protected void handleFlat(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        // ppt不支持xml格式文件转换
        throw new SlideConvertException("slide file content error");
    }

    @Override
    protected void handleBinary(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        this.handleHtml(new HSLFSlideShow(inputStream), outputStream);
    }

    @Override
    protected void handleZipped(BufferedInputStream inputStream, OutputStream outputStream) throws Exception {
        this.handleHtml(new XMLSlideShow(inputStream), outputStream);
    }

    /**
     * ppt转html
     * @param ppt          {@link SlideShow}
     * @param outputStream {@link OutputStream}
     * @param <S>          {@link S}
     * @param <P>          {@link P}
     * @throws Exception 异常
     */
    protected <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> void handleHtml(
        SlideShow<S, P> ppt, OutputStream outputStream) throws Exception {
        HtmlDocumentFacade facade = new HtmlDocumentFacade(
            DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        );
        // 设置图片自适应
        Element style = facade.getDocument().createElement("style");
        style.setAttribute("type", "text/css");
        style.setTextContent("img {height: auto; width:98%; padding-left:1%; padding-right: 1%;}");
        facade.getHead().appendChild(style);
        Element body = facade.getBody();
        SlideImageUtils.handleImage(ppt, bi -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(bi, "png", baos);
            } catch (IOException e) {
                throw new SlideConvertException(e);
            }
            Element block = facade.createBlock();
            Element image = facade.createImage(ImageUtils.base64(baos.toByteArray()));
            block.appendChild(image);
            body.appendChild(block);
        });

        mobileSupport(facade.getDocument());

        htmlTransformer().transform(new DOMSource(facade.getDocument()), new StreamResult(outputStream));
    }
}
