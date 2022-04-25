# 前言

基于[spire.office](https://www.e-iceblue.com/)文档转换，支持文件转换如下

|源文件|源文件类型|目标文件类型|
|:---|:---|:---|
|PDF|pdf|html|
|Word文档|doc、docx、xml|html、pdf|
|PowerPoint文档|ppt、pptx|html、pdf|
|Excel文档|xls、xlsx、xml|html|

> 转换的源文件和目标文件可以为文件、流、文件路径

# pdf文档转换

```java
PortableConverter.create()
    .input(new FileInputStream(FileUtil.brotherPath(this.getClass(),"pdf.pdf")))
    .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(),"pdf.html")))
    .convert(PortableConvertType.HTML);
```

# word文档转换

```java
DocumentConverter.create()
    .input(new FileInputStream(FileUtil.brotherPath(this.getClass(),"xml.doc")))
    .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(),"xml.html")))
    // DocumentConvertType.PDF转为pdf
    .convert(DocumentConvertType.HTML);
```

# PowerPoint文档转换

```java
SlideConverter.create()
    .input(new FileInputStream(FileUtil.brotherPath(this.getClass(),"2003.ppt")))
    .output(new FileOutputStream(FileUtil.brotherPath(this.getClass(),"2003.html")))
    // SlideConvertType.PDF转为pdf
    .convert(SlideConvertType.HTML);
```

# excel文档转换

```java
SpreadSheetConverter.create()
    .input(new File(FileUtil.brotherPath(this.getClass(), "/2007.xlsx")))
    .output(new File(FileUtil.brotherPath(this.getClass(), "/2007.html")))
    .convert(SpreadSheetOfficeConvertType.HTML);
```