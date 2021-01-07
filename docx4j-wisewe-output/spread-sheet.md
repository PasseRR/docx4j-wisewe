[excel导出测试用例](./src/test/java/cn/wisewe/docx4j/output/builder/sheet/SpreadSheetBuilderSpec.java)

<details>
<summary><b>1.简单表格导出</b></summary>

<h5> 效果 </h5>

<table style="text-align: center;">
    <tr >
	    <th style="border-bottom-color:black;border-right-color: black">姓名</th>
	    <th style="border-bottom-color:black;">年龄</th>
        <th style="border-bottom-color:black;">性别</th>
	</tr>
	<tr >
	    <td style="border-right-color:black;">张三</td>
	    <td>26</td>
	    <td>女</td>
	</tr>
	<tr >
	    <td style="border-right-color:black;">李四</td>
	    <td>50</td>
	    <td>男</td>
	</tr>
	<tr >
	    <td style="border-right-color:black;">王五</td>
	    <td>18</td>
	    <td>女</td>
	</tr>
	<tr >
	    <td style="border-right-color:black;">赵六</td>
	    <td>2</td>
	    <td>女</td>
	</tr>
	<tr >
	    <td style="border-right-color:black;">燕七</td>
	    <td>80</td>
	    <td>男</td>
	</tr>
</table>

<blockquote> 此处冻结了前 1 行、前 1 列的数据。 </blockquote>

<h5> 代码 </h5>

<pre><code lang="java">public void simple() throws FileNotFoundException {
    SpreadSheetBuilder.create()
        .workbook(wb ->
            wb.sheet(s ->
                // 表头行：填充顺序与列表顺序一致
                s.row(r -> r.headCells(Arrays.asList("姓名", "年龄", "性别")))
                    // 数据行：填充顺序与dataCell()依次追加的顺序一致
                    .rows(
                        SpecDataFactory.excelData(),
                        (it, row) -> row.dataCell(it::getName).dataCell(it::getAge).dataCell(it::getSex)
                    )
                    // 行列冻结
                    .freeze(1, 1)
            )
        )
        .writeTo(new FileOutputStream(prefix + "simple.xlsx"));
}
</code></pre>
</details>

<details>
<summary><b>2.表头单元格合并</b></summary>

<h5>效果</h5>

<table style="text-align: center;">
	<tr>
	    <th rowspan = "2">姓名</th>
	    <th colspan="2" >其他信息</th>
	</tr >
    <tr >
	    <th>年龄</th>
        <th>性别</th>
	</tr>
	<tr >
	    <td>张三</td>
	    <td>26</td>
	    <td>女</td>
	</tr>
	<tr >
	    <td>李四</td>
	    <td>50</td>
	    <td>男</td>
	</tr>
	<tr >
	    <td>王五</td>
	    <td>18</td>
	    <td>女</td>
	</tr>
	<tr >
	    <td>赵六</td>
	    <td>2</td>
	    <td>女</td>
	</tr>
	<tr >
	    <td>燕七</td>
	    <td>80</td>
	    <td>男</td>
	</tr>
</table>

<h5>代码</h5>

<pre><code lang="java">public void mergeHead() throws FileNotFoundException {
    SpreadSheetBuilder.create()
        .workbook(wb ->
            wb.sheet(s ->
                // 表头行，首行：设置“姓名”跨2行、“其他信息”跨2列（动态填充时跨n行需在下行对应列填充n-1个占位符）
                s.row(r -> r.headCell(c -> c.rowspan(2).text("姓名")).headCell(c -> c.colspan(2).text("其他信息")))
                     // 表头行，第2行：设置“姓名”、“年龄”、“性别”。“姓名”处为填充跨行占位符，可为任意字符
                    .row(r -> r.headCells(Arrays.asList("姓名", "年龄", "性别")))
                    // 数据行
                    .rows(
                        SpecDataFactory.excelData(),
                        (it, row) -> row.dataCell(it::getName).dataCell(it::getAge).dataCell(it::getSex)
                    )
            )
        )
        .writeTo(new FileOutputStream(prefix + "merge-head.xlsx"));
}
</code></pre>

</details>

<details>
<summary><b>3.数据单元格合并</b></summary>

<h5>效果</h5>

<table style="text-align: center;">
    <tr >
	    <th>姓名</th>
	    <th>年龄</th>
        <th>性别</th>
	</tr>
	<tr >
	    <td>张三</td>
	    <td>26</td>
	    <td rowspan = "3">女</td>
	</tr>
	<tr >
	    <td>王五</td>
	    <td>18</td>
	</tr>
	<tr >
	    <td>赵六</td>
	    <td>2</td>
	</tr>
	<tr >
	    <td>李四</td>
	    <td>50</td>
	    <td rowspan = "2">男</td>
	</tr>
	<tr >
	    <td>燕七</td>
	    <td>80</td>
	</tr>
</table>

<blockquote>注意：合并处理性别列模拟sql分组，**不保证列表数据顺序**。</blockquote>

<h5>代码</h5>

<pre><code lang="java">public void mergeData() throws FileNotFoundException {
    // 将数据按照性别分组 合并处理性别列 模拟sql分组 但不保证列表数据顺序
    Map<String, List<code>> groupBySex =
        SpecDataFactory.excelData().stream().collect(Collectors.groupingBy(Person::getSex));
    SpreadSheetBuilder.fastCreate(wb ->
        wb.sheet(s -> {
            // 表头行
            s.row(r -> r.headCells(Arrays.asList("姓名", "年龄", "性别")));
            // 按照性别渲染表格
            groupBySex.forEach((key, value) -> {
                AtomicBoolean merged = new AtomicBoolean();
                int rowspan = value.size();
                // 数据行
                s.rows(value, (t, row) ->
                    row.dataCell(t::getName)
                        .dataCell(t::getAge)
                        .dataCell(c -> {
                            c.text(t::getSex);
                            if (!merged.get()) {
                                // 只合并第一行
                                merged.set(Boolean.TRUE);
                                c.rowspan(rowspan);
                            }
                        })
                );
            });
        })
    ).writeTo(new FileOutputStream(prefix + "merge-data.xlsx"));
}
</code></pre>

</details>

<details>
<summary><b>4.动态Sheet页</b></summary>

<h5>效果</h5>

Sheet 名称：李四的Sheet

<table style="text-align: center;">
    <tr >
	    <th>姓名</th>
	    <th>年龄</th>
        <th>性别</th>
	</tr>
	<tr >
	    <td>张三</td>
	    <td>26</td>
	    <td>女</td>
	</tr>
</table>

Sheet 名称：王五的Sheet

<table style="text-align: center;">
    <tr >
	    <th>姓名</th>
	    <th>年龄</th>
        <th>性别</th>
	</tr>
	<tr >
	    <td>王五</td>
	    <td>18</td>
        <td>女</td>
	</tr>
</table>

…… 此处有若干个Sheet

<blockquote>导出的 Excel 文件中有若干个动态设定的 Sheet ， Sheet 名称命名为：某人员名称 + ”的Sheet“，各个 Sheet 中的数据是该人员对应的基础数据。</blockquote>

<h5>代码</h5>

<pre><code lang="java">public void dynamicSheet() throws FileNotFoundException {
    SpreadSheetBuilder.create()
        .workbook(wb ->
            // 动态sheet
            wb.sheets(SpecDataFactory.excelData(), it -> it.getName() + "的Sheet", (it, s) ->
                // 表头行
                s.row(r -> r.headCells(Arrays.asList("姓名", "年龄", "性别")))
                    .row(r -> r.dataCell(it::getName).dataCell(it::getAge).dataCell(it::getSex))
            )
        )
        .writeTo(new FileOutputStream(prefix + "dynamic-sheet.xlsx"));
}
</code></pre>

</details>

<details>
<summary><b>5.业务代码示例</b></summary>

<h5>效果</h5>

<blockquote> 导出如下样式的表格，其中星期几、一天几节课、班级数量、班级课程都是动态数据。</blockquote>

<table style="text-align: center;">
	<tr>
		<th rowspan="2" style="width: 50px;">班级</th>
		<th colspan="2">星期一</th>
		<th colspan="2">星期二</th>
		<th colspan="2">星期X，动态数据</th>
	</tr>
	<tr>
		<th style="width: 50px;">第1节</th>
		<th style="width: 50px;">第Y节</th>
		<th style="width: 50px;">第1节</th>
		<th style="width: 50px;">第Y节</th>
		<th style="width: 50px;">第1节</th>
		<th style="width: 50px;">第Y节</th>
	</tr>
	<tr>
		<th>20护理1班</th>
		<th>1-18周（单）（理论）
			JAVA原理课程名
			（合）2020级护理班
			李四、王五 5-562</th>
		<th></th>
		<th></th>
		<th>1-18周（单）（理论）
			JAVA原理课程名
			（合）2020级护理班
			李四、王五 5-562</th>
		<th>1-18周（单）（理论）
			JAVA原理课程名
			（合）2020级护理班
			李四、王五 5-562</th>
		<th></th>
	</tr>
	<tr>
		<th>20护理Z班</th>
		<th></th>
		<th>1-18周（单）（理论）
			JAVA原理课程名
			（合）2020级护理班
			李四、王五 5-562</th>
		<th>1-18周（单）（理论）
			JAVA原理课程名
			（合）2020级护理班
			李四、王五 5-562</th>
		<th></th>
		<th></th>
		<th>1-18周（单）（理论）
			JAVA原理课程名
			（合）2020级护理班
			李四、王五 5-562</th>
	</tr>
</table>

<h5>代码</h5>
<pre><code lang="java">private void exportExcel(ScheduleBasicSetting setting,
                             List<ScheduleViewAllDataGridBodyVo> viewAllBodyRows,
                             String fileName) {
        SpreadSheetBuilder.fastCreate(wb ->
            wb.sheet(s ->
                // 表头行，首行：设置“班级”跨2行、“星期”跨课程总节次列
                s.row(r -> {
                    r.headCell(c -> c.rowspan(2).text("班级"));
                    // 设置星期几{it.getName()}、且让其跨课程总节次{setting.total()}列
                    ScheduleDayOfWeek.getDayOfWeekByDays(setting.getDaysPerWeek())
                        .forEach(it -> r.headCell(c -> c.colspan(setting.total()).text(it.getName())));
                })
                    // 表头行，第2行：设置所有课程节次 （班级跨了该行，需要班级个占位符）
                    .row(r -> {
                        // 生成一天总节次
                        List<Object> sections =
                            IntStream.rangeClosed(1, setting.total()).boxed().collect(Collectors.toList());

                        // 设置单元格内容
                        r.headCell("班级");
                        IntStream.rangeClosed(1, setting.getDaysPerWeek()).forEach(it -> r.headCells(sections));
                    })
                    // 数据行
                    .rows(
                        viewAllBodyRows,
                        (it, row) -> {
                            // 设置班级名称
                            row.dataCell(it.getOriginClassName());

                            // 获取当前行单元格结果集（按天序号、节次序号分组）
                            Map<Integer, Map<Integer, List<ScheduleViewAllDataGridBodyItemVo>>> cellsResultMap =
                                getCurrentLineCellsResultMap(it.getTemp());

                            // 设置该单元格对应课程
                            it.getTemp().forEach(col -> {
                                int day = col.getDayOrdinal();
                                int section = col.getSectionOrdinal();
                                // 将该单元格对应的课表信息包装好，并填充到单元格
                                row.dataCell(this.packageCellStyle(cellsResultMap.get(day).get(section)));
                            });
                        }
                    )
                    // 冻结前 1 列、前 2 行
                    .freeze(1, 2)
            ))
            .writeToServletResponse(fileName);
    }
</code> </pre>

<blockquote>源代码查看 /ems.manage/ems.manage.service/src/java/cn/wisewe/hv/ems/manage/biz/schedule/service/ScheduleViewAllService.java</blockquote>

</details>
