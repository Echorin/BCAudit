package com.oschain.fastchaindb.common.datapage;

import com.oschain.fastchaindb.common.utils.ObjectUtil;
import com.oschain.fastchaindb.common.utils.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class QueryConfig {

	/**
	 * 生成下拉属性
	 * @param name
	 * @return
	 */
	public static Map<String, QueryAttr> getComboPageAttr(String name) {
		QueryAttr queryAttr;
		Map<String, QueryAttr> mapList = new HashMap<String, QueryAttr>();

		Document document = getDocument(name);
		Element root = document.getRootElement();
		Iterator it = root.elementIterator();

		while (it.hasNext()) {
			Element elm = (Element)it.next();
			
			if (ObjectUtil.isNotEmpty(elm.elementText("ComboFields"))) {
				JSONArray arr = JSONArray.fromObject("[" + elm.elementText("ComboFields") + "]");
				for (int i = 0; i < arr.size(); i++) {

					JSONObject obj = (JSONObject) arr.get(i);
					queryAttr = new QueryAttr();
					if (ObjectUtil.isNotEmpty(obj.get("combourl")))
						queryAttr.setTableUrl(obj.get("combourl").toString().toLowerCase());
					else {
						continue;
					}

					if (obj.containsKey("field")) {
						queryAttr.setFields((String) obj.get("field"));
					}
					if (obj.containsKey("table")) {
						queryAttr.setTableName((String) obj.get("table"));
					}
					if (obj.containsKey("sort")) {
						queryAttr.setSort((String) obj.get("sort"));
					}
					if (obj.containsKey("query")) {
						queryAttr.setFilter((String) obj.get("query"));
					}
					if (obj.containsKey("data")) {
						queryAttr.setKey((String) obj.get("data"));
					}
					if (obj.containsKey("check")) {
						queryAttr.setCheckKey((String) obj.get("check"));
					}

					mapList.put(queryAttr.getTableUrl(), queryAttr);
				}
			}
		}
		
		return mapList;
	}
	
	/**
	 * 生成列表属性
	 * @param name
	 * @return
	 */
	public static Map<String, QueryAttr> getQueryPageAttr(String name) {

		QueryAttr queryAttr;// 静态值不会改变
		Map<String, QueryAttr> mapList = new HashMap<String, QueryAttr>();

		try {
			
			Document document = getDocument(name);
			Element root = document.getRootElement();
			Iterator it = root.elementIterator();
			while (it.hasNext()) {
				Element elm = (Element) it.next();

				queryAttr = new QueryAttr();
				if (ObjectUtil.isNotEmpty(elm.attributeValue("TableUrl")))
					queryAttr.setTableUrl(elm.attributeValue("TableUrl"));
				else {
					continue;
				}

				if (ObjectUtil.isNotEmpty(elm.attributeValue("TitleName")))
					queryAttr.setTitle(elm.attributeValue("TitleName"));
				if (ObjectUtil.isNotEmpty(elm.attributeValue("TableName")))
					queryAttr.setTableName(elm.attributeValue("TableName"));
//				if (ObjectUtils.isNotEmpty(elm.attributeValue("OpenSize")))
//					queryAttr.setOpenSize(elm.attributeValue("OpenSize"));
				if (ObjectUtil.isNotEmpty(elm.attributeValue("TableType")))
					queryAttr.setTableType(elm.attributeValue("TableType"));
				if (ObjectUtil.isNotEmpty(elm.elementText("OrderBy")))
					queryAttr.setSort(elm.elementText("OrderBy"));
				if (ObjectUtil.isNotEmpty(elm.elementText("GroupBy")))
					queryAttr.setGroup(elm.elementText("GroupBy"));
				if (ObjectUtil.isNotEmpty(elm.elementText("Filter")))
					queryAttr.setFilter(elm.elementText("Filter"));
				if (ObjectUtil.isNotEmpty(elm.elementText("HidFields")))
					queryAttr.setHidFields(elm.elementText("HidFields"));
				if (ObjectUtil.isNotEmpty(elm.elementText("Sql")))
					queryAttr.setSql(elm.elementText("Sql"));

				//如果是SQL，直接生成FieldName，用于查询条件过滤
				if (ObjectUtil.isNotEmpty(elm.elementText("FilterKey"))){
					List<String> fieldName = Arrays.asList(elm.elementText("FilterKey").split(","));
					queryAttr.setFieldName(fieldName);
				}

				
				//数据源类型，table:表，mapping：mybatis映射
				if (ObjectUtil.isNotEmpty(elm.attributeValue("DataType")))
					queryAttr.setDataType(elm.attributeValue("DataType"));
				else
					queryAttr.setDataType("table");
				

				if (ObjectUtil.isNotEmpty(elm.elementText("OpenForm"))) {

					QueryForm form;
					Map<String, QueryForm> formAttr = new LinkedHashMap<String, QueryForm>();

					List lielms = elm.elements("OpenForm");
					for (int i = 0; i < lielms.size(); i++) {
						form = new QueryForm();
						Element celms = (Element) lielms.get(i);

						if (ObjectUtil.isNotEmpty(celms.attributeValue("formCmd")))
							form.setFormCmd(celms.attributeValue("formCmd"));

						if (ObjectUtil.isNotEmpty(celms.attributeValue("formPkid")))
							form.setFormPkid(celms.attributeValue("formPkid"));

						if (ObjectUtil.isNotEmpty(celms.attributeValue("formTitle")))
							form.setFormTitle(celms.attributeValue("formTitle"));
						
						if (ObjectUtil.isNotEmpty(celms.attributeValue("formMode")))
							form.setFormMode(celms.attributeValue("formMode"));

						if (ObjectUtil.isNotEmpty(celms.getData()))
							form.setFormUrl(celms.getData().toString());

						formAttr.put(celms.attributeValue("formCmd"), form);
					}
					queryAttr.setFormAttr(formAttr);
				}

				// 字段属性
				JSONArray arr;
				if (ObjectUtil.isNotEmpty(elm.elementText("Fields"))) {
					QueryField field;
					StringBuffer sbfield = new StringBuffer();
					StringBuffer sbsumfield = new StringBuffer();
					Map<String, QueryField> fieldAttr = new LinkedHashMap<String, QueryField>();
					List<String> fieldName = new ArrayList<String>();
					List<String> sumField = new ArrayList<String>();

					arr = JSONArray.fromObject("[" + elm.elementText("Fields") + "]");// 解析JSON数据
					for (int i = 0; i < arr.size(); i++) {
						field = new QueryField();
						JSONObject obj = (JSONObject) arr.get(i);
						String sfield = (String) obj.get("field");
						
						
						if (!fieldAttr.containsKey(sfield)) {
							if (obj.containsKey("field")) {
								field.setField((String) obj.get("field"));
							}
							if (obj.containsKey("name")) {
								field.setName((String) obj.get("name"));
							}
							if (obj.containsKey("sort")) {
								field.setSort((String) obj.get("sort"));
							}
							if (obj.containsKey("width")) {
								field.setWidth((String) obj.get("width"));
							}
							if (obj.containsKey("data")) {
								field.setData((String) obj.get("data"));
							}
							if (obj.containsKey("type")) {
								field.setType((String) obj.get("type"));
							}
							if (obj.containsKey("format")) {
								field.setFormat((String) obj.get("format"));
							}
							if (obj.containsKey("align")) {
								field.setAlign((String) obj.get("align"));
							}

							if (obj.containsKey("issum")) {
								sumField.add(sfield);
								sbsumfield.append(",sum(" + sfield + ") as " + sfield);
								field.setIsSum(true);
							}

							fieldAttr.put(sfield.toLowerCase(), field);
							fieldName.add(sfield.toLowerCase());

							if (field.getData() != null && field.getData() != "")
								sbfield.append("," + field.getData() + " as " + sfield);
							else
								sbfield.append("," + sfield);
						}
					}
					if (sbfield.length() > 0)
						queryAttr.setFields(sbfield.toString().substring(1));
					else
						queryAttr.setFields("*");

					if (sbsumfield.length() > 0)
					{
						queryAttr.setTotalKey(sbsumfield.substring(1));
						queryAttr.setSumField(sumField);
					}
					
					if(StringUtil.isNotBlank(queryAttr.getHidFields())){
						String[] hidFields=queryAttr.getHidFields().split(",");
						for(int i=0;i<hidFields.length;i++){
							fieldName.add(hidFields[i]);
						}
					}

					queryAttr.setFieldAttr(fieldAttr);
					queryAttr.setFieldName(fieldName);
					
				}

				mapList.put(queryAttr.getTableUrl(), queryAttr);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return mapList;
	}
	
	/**
	 * 生成弹出查询属性
	 * @param name
	 * @return
	 */
	public static Map<String, QueryAttr> getSelectPageAttr(String name) {
		
		QueryAttr queryAttr;
		Map<String, QueryAttr> mapList =new HashMap<String, QueryAttr>();
		
		try {
			Document document=getDocument(name);
			Element root = document.getRootElement();
			Iterator it = root.elementIterator();
			while (it.hasNext()) {
				Element elm = (Element) it.next();
				
				queryAttr=new QueryAttr();
				if (ObjectUtil.isNotEmpty(elm.attributeValue("TableUrl")))
					queryAttr.setTableUrl(elm.attributeValue("TableUrl"));
				else {
					continue;
				}
				
				if (ObjectUtil.isNotEmpty(elm.attributeValue("TitleName")))
					queryAttr.setTitle(elm.attributeValue("TitleName"));
				if (ObjectUtil.isNotEmpty(elm.attributeValue("TableName")))
					queryAttr.setTableName(elm.attributeValue("TableName"));
				if (ObjectUtil.isNotEmpty(elm.attributeValue("TableType")))
					queryAttr.setTableType(elm.attributeValue("TableType"));
				if (ObjectUtil.isNotEmpty(elm.elementText("OrderBy")))
					queryAttr.setSort(elm.elementText("OrderBy"));
				if (ObjectUtil.isNotEmpty(elm.elementText("GroupBy")))
					queryAttr.setGroup(elm.elementText("GroupBy"));
				if (ObjectUtil.isNotEmpty(elm.elementText("Filter")))
					queryAttr.setFilter(elm.elementText("Filter"));
				if (ObjectUtil.isNotEmpty(elm.elementText("HidFields")))
					queryAttr.setHidFields(elm.elementText("HidFields"));
			
				if (ObjectUtil.isNotEmpty(elm.elementText("OpenForm"))) {
					QueryForm form;
					Map<String,QueryForm> formAttr = new LinkedHashMap<String, QueryForm>();
					List lielms = elm.elements("OpenForm");
					for (int i = 0; i < lielms.size(); i++) {
						form = new QueryForm();
						Element celms = (Element) lielms.get(i);

						if (ObjectUtil.isNotEmpty(celms.attributeValue("formCmd")))
							form.setFormCmd(celms.attributeValue("formCmd"));

						if (ObjectUtil.isNotEmpty(celms.attributeValue("formPkid")))
							form.setFormPkid(celms.attributeValue("formPkid"));

						if (ObjectUtil.isNotEmpty(celms.attributeValue("formTitle")))
							form.setFormTitle(celms.attributeValue("formTitle"));

						if (ObjectUtil.isNotEmpty(celms.getData()))
							form.setFormUrl(celms.getData().toString());
						
						formAttr.put(celms.attributeValue("formCmd"),form);
					}
					queryAttr.setFormAttr(formAttr);
				}

				// 字段属性
				JSONArray arr;
				if (ObjectUtil.isNotEmpty(elm.elementText("Fields"))) {
					QueryField field;
					StringBuffer sbfield = new StringBuffer();
					StringBuffer sbsumfield = new StringBuffer();
					Map<String, QueryField> fieldAttr = new LinkedHashMap<String, QueryField>();
					List<String> fieldName = new ArrayList<String>();

					arr = JSONArray.fromObject("[" + elm.elementText("Fields") + "]");//解析JSON数据
					for (int i = 0; i < arr.size(); i++) {
						field = new QueryField();
						JSONObject obj = (JSONObject) arr.get(i);
						String sfield = (String) obj.get("field");

						if (!fieldAttr.containsKey(sfield)) {
							if (obj.containsKey("field")) {
								field.setField((String) obj.get("field"));
							}
							if (obj.containsKey("name")) {
								field.setName((String) obj.get("name"));
							}
							if (obj.containsKey("sort")) {
								field.setSort((String) obj.get("sort"));
							}
							if (obj.containsKey("width")) {
								field.setWidth((String) obj.get("width"));
							}
							if (obj.containsKey("data")) {
								field.setData((String) obj.get("data"));
							}
							if (obj.containsKey("type")) {
								field.setType((String) obj.get("type"));
							}
							if (obj.containsKey("format")) {
								field.setFormat((String) obj.get("format"));
							}
							if (obj.containsKey("align")) {
								field.setAlign((String) obj.get("align"));
							}

							if (obj.containsKey("issum")) {
								sbsumfield.append(",sum(" + sfield + ") as " + sfield);
								field.setIsSum(true);
							}

							fieldAttr.put(sfield.toLowerCase(), field);
							fieldName.add(sfield.toLowerCase());

							if (field.getData() != null && field.getData() != "")
								sbfield.append("," + field.getData() + " as " + sfield);
							else
								sbfield.append("," + sfield);
						}
					}
					if (sbfield.length() > 0)
						queryAttr.setFields(sbfield.toString().substring(1));
					else
						queryAttr.setFields("*");

					if (sbsumfield.length() > 0)
						queryAttr.setTotalKey(sbsumfield.substring(1));

					queryAttr.setFieldAttr(fieldAttr);
					
					if(StringUtil.isNotBlank(queryAttr.getHidFields())){
						String[] hidFields=queryAttr.getHidFields().split(",");
						for(int i=0;i<hidFields.length;i++){
							fieldName.add(hidFields[i]);
						}
					}
					
					queryAttr.setFieldName(fieldName);
					
				}
				
				mapList.put(queryAttr.getTableUrl(), queryAttr);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return mapList;
	}

	private static Document getDocument(String name) {

		//String path = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"/dataset/"+name+".xml";
		//String path = QueryConfig.class.getResource("/dataset/"+name+".xml").getPath();


		//PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		//Resource res = resolver.getResource("classpath:dataset/"+name+".xml");




		//FileUtils.copyInputStreamToFile(stream, targetFile);

		//ApplicationContext applicationContext=new ClassPathXmlApplicationContext();

//		ApplicationContext appContext=(ApplicationContext) new ClassPathXmlApplicationContext();

		//Resource res = applicationContext.getResource("classpath*:dataset/"+name+".xml");

		InputStream stream=null;
		ClassPathResource classPathResource = new ClassPathResource("dataset/"+name+".xml");
		try {
			 stream = classPathResource.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//File files=null;


//		InputStream stream = getClass().getClassLoader().getResourceAsStream("dataset/"+name+".xml");
//		files = new File(name+".xml");
//			File targetFile = new File("xxx.pdf");
//			FileUtils.(stream, targetFile);

			//files = res.getFile();

		SAXReader reader = new SAXReader();
		Document document=null;
		try {
			//document = reader.read(new File(path));
			document = reader.read(stream);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}
//	public static void main(String[] args) {
//		Map<String, QueryAttr> mapQueryAttr = QueryConfig.getQueryPageAttr("listconfig");
//		int i=mapQueryAttr.size();
//		System.out.println(i);
//
//		mapQueryAttr = QueryConfig.getComboPageAttr("comboconfig");
//		i=mapQueryAttr.size();
//		System.out.println(i);
//
//		mapQueryAttr = QueryConfig.getSelectPageAttr("selectconfig");
//		i=mapQueryAttr.size();
//		System.out.println(i);
//
//	}
}
