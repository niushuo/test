package cn.com.ecict.app.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tika.exception.TikaException;
import org.codehaus.stax2.ri.Stax2FilteredStreamReader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.sun.scenario.effect.DisplacementMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import cn.com.ecict.app.bean.KBEntity;
import cn.com.ecict.app.bean.Term32s;
import cn.com.ecict.app.table.cmsEvent;
import cn.com.ecict.app.table.MultiAttributes;
import cn.com.ecict.app.table.cmsMain;
import cn.com.ecict.app.web.servlet.LaunchListener;
import cn.com.ecict.util.FileUtil;
import sun.nio.cs.ext.MacHebrew;

/**
 * 
 * 图谱构建的各接口的封装，供文档上传、统一检索等Action调用。
 * 把图谱的接口进行封装，以适配Action调用。
 * 
 * 包含以下功能的接口封装：
 * 1、知识查询：action传来一个查询关键词（代表武器装备名称），从知识库中查询武器装备的各个属性-属性值，并返回；
 *          传来的也可能是一个问句，则从知识库中查询到答案后返回；--暂时不考虑智能问答；
 * 2、知识抽取入库：action传来一段文本，从中抽取出知识（实体、关系、属性和事件），然后入知识库保存；--暂时不考虑知识融合，也不考虑一个属性有多个值的情况；
 *
 * 3、知识更新：action传来一个实体的属性-属性值，把这个信息更新到知识库里；--暂时不考虑一个属性有多个值的情况；
 * 4、知识删除：action传来一个待删除的实体，从知识库中将其删除；或者接收action传来的一个实体的待删除的属性-属性值，将其从知识库中删除；
 *
 * 5、知识融合*：在知识入库时考虑融合；
 * 6、知识的人工修正**：在知识入库、知识更新时，考虑一个属性有多个值的情况。
 * 
 * 7、智能问答的支持***：当知识库新增了知识后，知识库应该可以回答相关的知识提问。智能问答的答案是怎么产生的？
 * 
 * ----WJT，LDQ，  2019.11.11
 * 
 */
/**
MGM-29飞弹--查询返回值示例:

{
"msg":"success",
"code":200,
"data":{
	"relationships":[
	{"id":1,"source":"0x1d5355f","type":"制造国","target":"0x25f9ae9"},
	{"id":2,"source":"0x1d535b9","type":"拥有国","target":"0x25f9ae9"},
	{"id":3,"source":"0x1d535ec","type":"原产地","target":"0x25f9ae9"},
	{"id":4,"source":"0x1d58358","type":"拥有国","target":"0x25f9ae9"},
	{"id":5,"source":"0x1d5d209","type":"原产地","target":"0x25f9ae9"},
	{"id":6,"source":"0x1f0ad54","type":"原产地","target":"0x25f9ae9"},
	{"id":7,"source":"0x25b2e3e","type":"关联","target":"0x1d5d19c"},
	{"id":8,"source":"0x1d73138","type":"关联","target":"0x25b2e3e"},
	{"id":9,"source":"0x1d81bad","type":"关联","target":"0x25b2e3e"},
	{"id":10,"source":"0x1d84271","type":"主要用户","target":"0x25b2e3e"},
	{"id":11,"source":"0x1da1742","type":"主要用户","target":"0x25b2e3e"},
	{"id":12,"source":"0x1f0ad54","type":"使用方","target":"0x25b2e3e"}],

	"nodes":[
attrs-->{"id":"0x1f0ad54",
		 "word":"MGM-29飞弹",
attrKeyVals-->"properties":{
                "弹头":{"source":"维基百科","value":"高爆炸药；W52核弹头"},
                "重量":{"source":"维基百科","value":"4,530公斤（9,990英磅）"},
                "图片":{"source":"维基百科","value":"MGM-29飞弹.jpg"},
                "长度":{"source":"维基百科","value":"10.52米（34.5英尺）"},
                "制造国家":{"source":"维基百科","value":"美国"},
                "发动机":{"source":"维基百科","value":"固体燃料火箭发动机；200千牛"},
                "使用方":{"source":"维基百科","value":"美国陆军"},
                "直径":{"source":"维基百科","value":"790毫米（31英寸）"},
                "作战范围":{"source":"维基百科","value":"25英里（40公里）至84英里（135公里）"},
                "类型":{"source":"维基百科","value":"短程弹道导弹"}
              },
          "labels":"missile"
        },

		{"id":"0x25f9ae9","word":"美国","properties":{},"labels":"loc"},

		{"id":"0x1d5355f","word":"猎鹰9号运载火箭","properties":{},"labels":"spaceship"},

		{"id":"0x1d535b9","word":"普罗维登斯","properties":{},"labels":"loc"},

		{"id":"0x1d535ec","word":"FN FNX手枪","properties":{"alias":"FNFNX手枪"},"labels":"guns"},
		{"id":"0x1d58358","word":"约克号驱逐舰","properties":{"alias":"约克城号$CV10$CV－10$Yorktown/约克城级$约克级$CV－10/“约克城”号$CV－5/约克城号航空母舰/Yorktown$CV5/‘约克城’号/Yorktown$“约克城”号$CV10/‘约克城’号$CV-10$CV-10/“约克城”号$CV10/“约克城”号$‘约克城’号$约克城号航母$CV5/“约克城”号/Yorktown$CV5/约克城号航空母舰/Yorktown$“约克城”号航母$CV5/约克城号/Yorktown$约克城号航空母舰$CV-5/约克城号航空母舰/Yorktown$CV-10/约克城号$约克号重巡洋舰$CV-10/‘约克城’号$CV－10/约克城号$约克城级$CV10/约克城号$约克级重巡洋舰$约克城级航母$CV-5/约克城号/Yorktown$CV－10/‘约克城’号$CV-5“约克城”号$CV5"},"labels":"warship"},

		{"id":"0x1d5d209","word":"勃朗宁BLR杠杆式步枪","properties":{},"labels":"guns"},

		{"id":"0x25b2e3e","word":"美国陆军","properties":{"alias":"美陆军"},"labels":"force"},

		{"id":"0x1d5d19c","word":"美国陆军第1骑兵师","properties":{"alias":"第1骑兵师$美陆军第1骑兵师"},"labels":"force"},
		{"id":"0x1d73138","word":"M8装甲车","properties":{"alias":"Greyhound装甲车$M8轻型装甲车$美国M8型轮式装甲车$m8灰狗装甲车$M8轻型坦克$M8/猎犬/Greyhound装甲车$M-8"},"labels":"tank"},
		{"id":"0x1d81bad","word":"“爱国者”-3","properties":{"alias":"爱国者三型防空飞弹$“爱国者”-3海基型$爱国者PAC-2+$爱国者飞弹二型$爱国者-3型导弹$爱国者三型$MIM-104爱国者飞弹$爱国者防空飞弹系统$爱国者反飞弹系统$“爱国者”防空导弹系统$“爱国者”地空导弹系统$爱国者三型导弹$爱国者导弹$爱国者飞弹系统$爱国者三型飞弹$爱国者"},"labels":"missile"},

		{"id":"0x1d84271","word":"RAH-66卡曼契直升机","properties":{},"labels":"aircraft"},

		{"id":"0x1da1742","word":"AH-64阿帕契直升机","properties":{},"labels":"aircraft"}],

	"source":{"word":"维基百科","labels":"维基百科"}
	}
}

 */

/**
MGM-29飞弹相似实体查询的返回值：

{"code":0,
"data":
{"entitys":
[
{"_index":"entity","_type":"missile","_source":{"name":"MGM-1导弹","freq":0,"attr":1,"index_word":"M"},"_id":"AWx_2mCFKeBcVr2WP3EE","_score":10.324861},
{"_index":"entity","_type":"missile","_source":{"name":"AA-12飞弹","freq":0,"attr":1,"index_word":"A"},"_id":"AWx_2mCFKeBcVr2WP3Bq","_score":8.1973505},{"_index":"entity","_type":"missile","_source":{"name":"Kh-15飞弹","freq":0,"attr":1,"index_word":"K"},"_id":"AWx_2mCFKeBcVr2WP3CH","_score":8.1973505},
{"_index":"entity","_type":"missile","_source":{"name":"AA-2飞弹","freq":0,"attr":1,"index_word":"A"},"_id":"AWx_2mCEKeBcVr2WP3BJ","_score":8.155281},
{"_index":"entity","_type":"missile","_source":{"name":"R-60飞弹","freq":9,"attr":1,"index_word":"R"},"_id":"AWx_2mCFKeBcVr2WP3Bs","_score":8.027419},
{"_index":"entity","_type":"missile","_source":{"name":"UR-100N飞弹","freq":8,"attr":1,"index_word":"U"},"_id":"AWx_2mCEKeBcVr2WP3BI","_score":7.4105206},
{"_index":"entity","_type":"missile","_source":{"name":"玄武三型巡弋飞弹","freq":0,"attr":1,"index_word":"X"},"_id":"AWx_2mCFKeBcVr2WP3Bv","_score":7.0568347},
{"_index":"entity","_type":"missile","_source":{"name":"AGM-65小牛飞弹","freq":55,"attr":1,"index_word":"A"},"_id":"AWx_2mCFKeBcVr2WP3CA","_score":7.0568347},
{"_index":"entity","_type":"missile","_source":{"name":"魔术空对空飞弹","freq":0,"attr":1,"index_word":"M"},"_id":"AWx_2mCEKeBcVr2WP3BR","_score":6.045445},
{"_index":"entity","_type":"missile","_source":{"name":"雄风一型反舰飞弹","freq":64,"attr":1,"index_word":"X"},"_id":"AWx_2mCFKeBcVr2WP3B7","_score":5.948664}
]
},

"message":"success"}
 */


public class KBUtil {

	//	private String text; //传入的文本字符串
	//	private String question;//查询关键词，往往代表一个实体名称，也可能是一个问句
	//	private Map<String, String> entityInfo = new HashMap<>();//一个实体的属性-属性值
	//
	//	private String entityName;//一个实体的名称
	//	
	//	
	//	public KBApiUtils (String name, String url, String method) {
	//		 
	//	}

	private static String ip   = LaunchListener.kbIP;  //?
	private static String port = LaunchListener.kbPort; //?
	private static String esUrl        = LaunchListener.kbEsUrl;//"/es/search/entity?q=";  
	private static String apiUrl       = LaunchListener.kbApiUrl;//api/entity/dg
	private static String recommendUrl = LaunchListener.kbRecommendUrl;//api/entity/dg
	private static String mediaUrl = LaunchListener.kbMediaUrl;
	private static String textPrefix  = LaunchListener.kbTextPrefix;//"{\"text\":\"";
	private static String labelPrefix = LaunchListener.kbLabelPrefix; //\"label\":\"
	private static String textSuffix  = LaunchListener.kbTextSuffix;//"\"}";

	private static String kbqaPort = LaunchListener.kbqaPort;// "30010"
	private static String kbqaUrl = LaunchListener.kbqaUrl;  // "/kbqa_cetc32"

	private static String hintsUrl = LaunchListener.hintsUrl; //"/es/search/entity?q="
	private static String candidatesUrl = LaunchListener.candidatesUrl; //"/es/search/cetc?q=";

	private static String attributePort = LaunchListener.attributePort; //"12345"
	private static String attributeUrl = LaunchListener.attributeUrl; // "/attribute_cetc32_v1"

	private static String relationPort = LaunchListener.relationPort; //"17398"
	private static String relationUrl = LaunchListener.relationUrl; //"/relation_cetc32_v2"

	private static String eventPort = LaunchListener.eventPort; //"17974"
	private static String eventUrl = LaunchListener.eventUrl; //"/event_cetc32"

	private static String posPort = LaunchListener.posPort;//调用分词接口的端口号
	private static String posUrl = LaunchListener.posUrl;//调用分词接口的部分url

	private static String kbFusionUrl = LaunchListener.kbFusionUrl; //"/api/mergetext"

	private static String kbProperEditUrl = LaunchListener.kbProperEditUrl; // "/api/proper"
	private static String sourceManageUrl = LaunchListener.sourceManageUrl; // "/api/sourcemanage/"
	private static String kbEntityEditUrl = LaunchListener.kbEntityEditUrl; //"/api/entity"

	private static String allEntitiesUrl = LaunchListener.allEntitiesUrl;//

	private static String urlPrefix   = "http://" + ip + ":" + port;
	private static String entityUrl = "http://" + ip + ":" + port;

	private static String nerPort = LaunchListener.nerPort;
	private static String nerUrl = LaunchListener.nerUrl;

	static boolean DEBUG = true; //true; //true; //true; //??正式版应该改为false获取去掉！！

	/**
	 *  {"words":[{"offset":0,"pos":"r","length":1,"id":0,"text":"他"},{"offset":1,"pos":"v","length":1,"id":1,"text":"叫"},{"offset":2,"pos":"nh","length":2,"id":2,"text":"汤姆"},{"offset":4,"pos":"v","length":1,"id":3,"text":"去"},{"offset":5,"pos":"v","length":1,"id":4,"text":"拿"},{"offset":6,"pos":"n","length":2,"id":5,"text":"外套"}],"text":"他叫汤姆去拿外套"}
	 * 调用词性标注的接口，返回的数据格式是List<Term32s>
	 * @param text
	 * @return
	 * NSS -----2019-11-28
	 */

	public static List<Term32s> segmentWords(String text) {
		if(DEBUG){
			ip =  "192.168.6.15";
			posPort = "30000";
			posUrl = "/api/v1/nlp/pos";
		}

		String url = "http://"+ip+":"+posPort+posUrl;
		String params = "{\"text\": \""+text+"\"}";

		JSONObject jObj = postResp(url,params); //接口返回的数据,返回数据见方法前注释

		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
			return null;
		}

		//将posToTerm32s()方法中提取的分词结果封装在Term32s对象中
		List<Term32s> segWords = posToTerm32s(jObj); //提取返回数据中分词的结果

		return segWords;
	}

	/**
	 * 将词性标注接口中返回参数进行解析，获取分词的结果
	 * @param jObj
	 * @return
	 * NSS ----- 2019-11-27
	 */
	private static List<Term32s> posToTerm32s(JSONObject jObj) {
		List<Term32s> parse = new ArrayList<>(); 
		if(jObj == null) return parse;

		JSONArray words = (JSONArray) jObj.get("words");//获取具体的分词返回结果

		for(Object word: words){
			JSONObject wd = (JSONObject) word;

			//封装term
			Term32s term = new Term32s(wd.getString("text"), wd.getInteger("offset"), wd.getInteger("length"), wd.getString("pos"));
			parse.add(term);
		}

		return parse;
	}




	/**
	 * 判断一个输入的字符串是实体名称还是一个自然语言提问。
	 * ----LDQ，2019.11.15
	 * 
	 * @param keyword
	 * @return
	 */
	public static boolean isEntityOrQuestion(String keyword){
		boolean result = true;
		// .....  <-------------- 待完成。
		return result;
	}

	/**
	 * 获取所有实体及其类别
	 * @param entityCategory
	 * @return
	 * NSS ----2019-12-3
	 */

	public static List<String> allEntities(String entityCategory){
		List<String> entityresult = new ArrayList<>();//将所有实体存放在set集合中

		String urlHead = entityUrl + allEntitiesUrl + entityCategory;
		String url = urlHead + "?p=1"; 
		int totalPages = countPage(url);//获取一个category中的页数
		for(int page = 1; page <= totalPages; page++){
			url = urlHead + "?p=" + page;
			JSONObject jObj =  getResp(url);
			if(jObj == null){
				System.err.println("服务异常，url="+url+" 不能被访问。");
				return null;
			}
			JSONObject entities = ((JSONObject)jObj.get("data"));
			List<String> onePageEntities = getEntity(entities);//获取实体
			entityresult.addAll(onePageEntities);
		}

		return entityresult;
	}


	/**
	 * 获取实体
	 * @param entities
	 * @return
	 */
	private static List<String> getEntity(JSONObject entities) {
		List<String> result = new ArrayList<>();

		JSONArray sources= (JSONArray) ((JSONObject) entities).get("data");
		if(sources == null) sources= (JSONArray) ((JSONObject) entities).get("entitys");
		for(int i = 0;i < sources.size();i++){
			JSONObject object = (JSONObject) sources.get(i);

			//应对不同图谱接口版本，需要不同的解析：
			if(object.containsKey("name") && object.containsKey("label")){ //{"name":"P－3M","label":"aircraft"}
				result.add(object.getString("name"));
			}
			else if(object.containsKey("_source") && ((JSONObject)object.get("_source")).containsKey("name")){
				result.add((String) ((JSONObject)object.get("_source")).get("name"));
			}
		}
		return result;
	}

	/**
	 * 统计每种类别实体的总页数，需要调用的接口totalPages<最大页数时，有300个实体
	 * @param url
	 * @return
	 */

	private static int countPage(String url) {
		JSONObject jObj =  getResp(url);
		if(jObj == null){
			System.err.println("服务异常，url="+url+" 不能被访问。");
			return 0;
		}
		JSONObject entities = ((JSONObject)jObj.get("data"));
		int entityNum = 0;
		//		for(Entry<String, Object> map :entities.entrySet()){
		//			if(map.getKey().equals("total"))  entityNum = (int) map.getValue();
		//		}
		if(entities.containsKey("total")){
			entityNum = Integer.valueOf((entities.get("total").toString()));
		}
		else {
			System.err.println("解析异常，查询实体时，data字段中没有包含total字段，无法进一步解析。");
			return 0;
		}

		//totalPages<最大页数时，每页包含300个实体词
		int totalPages = entityNum % 300 == 0 ? entityNum/300 : entityNum/300 + 1;
		return totalPages;
	}

	/**
	 * 0.1  输入提示：action传来一个查询关键词（可能是武器装备名称的一部分），从知识库中查询输入提示；
	 * eg. 输入“MGM-”，
	 * 图谱返回 entitys={MGM-1导弹, MGM-134侏儒洲际弹道导弹}, query={MGM-1导弹的制造国家,MGM-1导弹的发射平台,MGM-1导弹的使用方}
	 * 我们组合成一个字符串：hints = {MGM-1导弹,MGM-1导弹的制造国家,MGM-1导弹的发射平台,MGM-1导弹的使用方, MGM-134侏儒洲际弹道导弹}
	 * 
	 * ----LDQ,2019.11.15
	 * 
	 * @param keyword
	 * @return 输入提示
	 */
	public static List<String> kbInputHintsQuery(String keyword){
		List<String> hints = new ArrayList<>();

		if(DEBUG){
			urlPrefix = "http://192.168.6.15:18804";
			hintsUrl = "/api/search/entities/recommendations/?q="; //《--返回输入提示 
		}
		try {
			keyword = URLEncoder.encode(keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = urlPrefix + hintsUrl + keyword; 
		JSONObject jObj =  getResp(url);
		if(jObj == null){
			System.err.println("服务异常，url="+url+" 不能被访问。");
			return hints;
		}

		JSONArray entitys = (JSONArray)((JSONObject)jObj.get("data")).get("entitys");
		JSONArray query   = (JSONArray)((JSONObject)jObj.get("data")).get("query");

		if(entitys == null || entitys.size() == 0) return hints;

		hints.add(entitys.getJSONObject(0).getString("name"));
		for(Object q : query){
			hints.add(q.toString());
		}
		for(int i=1; i<entitys.size(); i++){
			hints.add(entitys.getJSONObject(i).getString("name"));
		}

		return hints;
	}
	//	public static List<String> kbInputHintsQuery(String keyword){
	//		List<String> hints = new ArrayList<>();
	//
	//		if(DEBUG){
	//			urlPrefix = "http://192.168.6.15:18804";
	//			hintsUrl = "/es/search/entity?q="; //《--返回输入提示 
	//		}
	//		String url = urlPrefix + hintsUrl + keyword; 
	//		JSONObject jObj =  getResp(url);
	//		if(jObj == null){
	//			System.err.println("服务异常，url="+url+" 不能被访问。");
	//			return null;
	//		}
	//
	//		JSONArray entitys = (JSONArray)((JSONObject)jObj.get("data")).get("entitys");
	//		JSONArray query   = (JSONArray)((JSONObject)jObj.get("data")).get("query");
	//
	//		if(entitys == null || entitys.size() == 0) return hints;
	//
	//		hints.add(entitys.getJSONObject(0).getString("name"));
	//		for(Object q : query){
	//			hints.add(q.toString());
	//		}
	//		for(int i=1; i<entitys.size(); i++){
	//			hints.add(entitys.getJSONObject(i).getString("name"));
	//		}
	//
	//		return hints;
	//	}
	//	


	/**
	 * 0.2  输入提示：action传来一个查询关键词（可能是武器装备名称的一部分），从知识库中查询候选实体（模糊匹配）-- 结果中不含文章。
	 * eg. 输入“MGM-”，
	 * 返回 candidates={MGM-1导弹, MGM-5导弹，MGM-1飞弹, MGM-29导弹, MGM-29导弹,...}
	 * 
	 * ----LDQ,2019.11.15
	 * 
	 * @param keyword
	 * @return 输入提示
	 */
	public static List<String> kbCandidatesQuery(String keyword){
		List<String> candidates = new ArrayList<>();

		if(DEBUG){
			urlPrefix = "http://192.168.6.15:18804";
			candidatesUrl = "/es/search/cetc?q="; //《--返回输入提示 
		}

		String url = urlPrefix + candidatesUrl + keyword; 
		JSONObject jObj =  getResp(url);
		if(jObj == null){
			System.err.println("服务异常，url="+url+" 不能被访问。");
			return null;
		}

		JSONArray entitys = (JSONArray)((JSONObject)jObj.get("data")).get("entity");
		if(entitys == null || entitys.size() == 0) return candidates;

		for(int i=0; i<entitys.size(); i++){
			candidates.add(entitys.getJSONObject(i).getJSONObject("_source").getString("name"));

		}

		return candidates;
	}


	/**
	 * 输入实体名称及类别返回实体id。
	 * @param entity {"name":"测试枪支24","label":"guns"}
	 * @return
	 */
	private static String kbNodeIdQuery(JSONObject entity) {

		if (DEBUG) {
			ip = "192.168.6.15";
			port = "18804";
			apiUrl = "/api/kg/entities/graphs";// "/api/entity/dg";
		}
		// String url = "http://" + ip + ":" + port + apiUrl;
		String url = "http://" + ip + ":" + port + apiUrl + "/?name=" + entity.getString("name") + "&label="
				+ entity.getString("label");
		// JSONObject jObj = postResp(url, entity.toJSONString());
		JSONObject jObj = getResp(url);
		if (jObj == null) {
			System.err.println("服务异常，url=" + url + ", params=" + entity.toString() + " 不能被访问。");
		}
		JSONObject entityInfo = jObj.getJSONObject("data").getJSONArray("nodes").getJSONObject(0);
		String id = entityInfo.getString("id");
		return id;
	}
	/*private static String kbNodeIdQuery(JSONObject entity){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			apiUrl = "/api/kg/entities/graphs";//"/api/entity/dg";
		}
		String url = "http://" + ip + ":" + port + apiUrl;

		JSONObject jObj = postResp(url, entity.toJSONString());
		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+entity.toString()+" 不能被访问。");
		}

		JSONObject entityInfo = jObj.getJSONObject("data").getJSONArray("nodes").getJSONObject(0);
		String id = entityInfo.getString("id");

		return id;
	}*/


	//	/**
	//	 * 实体查询 通过输入关键字返回精确实体名称以及实体类型 --wjt
	//	 * @param keyword
	//	 * @return
	//	 */
	//	private static List<String> getKbNodeName(String keyword){
	//		List<String> node = new ArrayList<>();
	//		
	//		if(DEBUG){
	//			urlPrefix = "http://192.168.6.15:18804";
	//			candidatesUrl = "/es/search/cetc?q="; //《--返回输入提示 
	//		}
	//		
	//		String url = urlPrefix + candidatesUrl + keyword; 
	//		JSONObject jObj =  getResp(url);
	//		if(jObj == null){
	//			System.err.println("服务异常，url="+url+" 不能被访问。");
	//			return null;
	//		}
	//		
	//		JSONArray entitys = (JSONArray)((JSONObject)jObj.get("data")).get("entity");
	//		if(entitys == null || entitys.size() == 0) return node;
	//
	//		
	//		node.add(entitys.getJSONObject(0).getJSONObject("_source").getString("name"));
	//		node.add(entitys.getJSONObject(0).getString("_type"));
	//	
	//		
	//
	//		return node;
	//	}



	/**
	 * 通过输入关键字查询实体的知识库id --wjt
	 * @param keyword
	 * @return
	 */
	//	private static JSONObject fromNametoId(String keyword){
	//		
	//		JSONObject nodeIdAndName = new JSONObject();
	//		List<String> node = getKbNodeName(keyword);
	//		
	//		JSONObject desiredNode = (JSONObject) kbNodeQuery(node).getJSONArray("nodes").get(0);
	//		
	//		String id = desiredNode.getString("id");
	//		String name = desiredNode.getString("word");
	//		
	//		nodeIdAndName.put("name", name);
	//		nodeIdAndName.put("id",id);
	//		
	//		return nodeIdAndName;
	//			
	//	}



	/**
	 * 有部分被查询错误的，通过这个函数来纠正。
	 * 比如：F-22被错误地查询成KR-22. 
	 * ----LDQ, 2019.12.24
	 *  
	 * @param inputName
	 * @return
	 */
	private static String rectify(String inputName){
		String realName = LaunchListener.rectifyTable.get(inputName);
		if(realName != null) return realName;
		else return inputName;
	}

	/**
	 * 读取纠错表
	 * eg：F-22被查询成RK-22；纠错：F-22 --> F-22猛禽战斗机
	 * ----LDQ, 2019.12.24
	 *  
	 * @throws IOException 
	 */
	public static void readRectifyTable(String readRectifyPath) throws IOException{
		List<String> lines = FileUtils.readLines(new File(readRectifyPath),"UTF-8");

		System.out.println("\n正在从纠错表中读实体的纠错名称...path="+readRectifyPath);
		int count = 0;
		for(String line : lines){
			if(count == 0) { count++; continue; }//略去第一行

			String[] parts = line.split(" = ");
			if(parts.length < 2) {
				System.err.println("line error: "+line);
				continue;
			}
			String wrongName   = parts[0].trim();
			String correctName = parts[1].trim(); 

			System.out.println(wrongName+" = "+correctName);

			LaunchListener.rectifyTable.put(wrongName, correctName); //纠错：F-22 --> F-22猛禽战斗机
		}
		System.out.println("一共从纠错表中读出"+LaunchListener.rectifyTable.size()+"个实体。"); 
		System.out.println("实体读取结束.\n");
	}

	/**
	 * 1、知识查询：action传来一个查询关键词（代表武器装备名称），从知识库中查询武器装备的各个属性-属性值，并返回；
	 * 
	 * @param entityName
	 * @return 武器装备的各个属性-属性值
	 */
	/*	public static KBEntityLinkedHashMap<String, Object> kbEntityQuery(String entityName){
		KBEntity entity = null;

		entityName = rectify(entityName); //if(entityName.equalsIgnoreCase("F-22")) entityName = "F-22猛禽战斗机";//F-22被错误地查询成KR-22. --LDQ, 2019.12.24

		//1.第一步，模糊查询获取实体的类型：
		if(DEBUG){
			urlPrefix = "http://192.168.6.15:18804";
			//candidatesUrl = "/es/search?q=";//"/es/search?q=";//<--返回几个候选实体  //"/es/search?q=";<--返回很多候选实体+文章 //"/es/search/entity?q=";《--返回输入提示 
			candidatesUrl = "/es/search/cetc?q=";//<--返回几个候选实体  //"/es/search?q=";<--返回很多候选实体+文章 //"/es/search/entity?q=";《--返回输入提示 

		}
		String url = urlPrefix + candidatesUrl + entityName; 

		JSONObject candidates =  getResp(url);
		if(candidates == null){
			System.err.println("服务异常，url="+url+" 不能被访问。");
			return null;
		}

		JSONObject data = (JSONObject)candidates.get("data");
		if(data == null){
			System.err.println("解析异常，"+entityName+": 返回数据data字段为空，无法进一步解析实体标准名称和别名。");
			return null;
		}

		//最匹配的实体的类型和名称
		String weaponType     = null;
		String bestEntityName = null;

		JSONArray entitys = (JSONArray)(data).get("entity");
		JSONObject alias = null;
		List<String> bias = (List<String>)data.get("bias"); //??
		if(entitys == null || entitys.size() == 0){
//			System.out.println(entityName+": 该实体不存在。");
//			return null;
			alias = (JSONObject) data.get("alias");
			if(alias == null){
				System.err.println("解析异常，"+entityName+": data数据中，entity和bias均为空，无法进一步解析实体标准名称和别名。");
				return null;
			}
			else {
				weaponType     = alias.getString("label");
				bestEntityName = alias.getString("name");
			}
		}
		else{
			//最匹配的实体的类型和名称 //??entitys.getJSONObject(0)？不完全准！有时候第二个实体才是真正要查询的实体！比如：苏-35战斗机
			weaponType     = entitys.getJSONObject(0).getString("_type");
			bestEntityName = entitys.getJSONObject(0).getJSONObject("_source").getString("name");
		}

		if(weaponType == null || bestEntityName == null){
			System.err.println("解析异常，"+"weaponType="+weaponType+", bestEntityName="+bestEntityName+", 实体查询时，数据无法进一步解析。");
			return null;
		}

		//2.第二步，精确查询，获取实体的属性值、关联关系：
		if(DEBUG){
			urlPrefix = "http://192.168.6.15:18804";
			apiUrl = "/api/entity/dg";
		}
		url = urlPrefix + apiUrl;

		if(DEBUG){
			textPrefix = "{\"text\":\"";
			labelPrefix = "\"label\":\"";
			textSuffix = "\"}";
		}
		String params = textPrefix + bestEntityName + "\"," + labelPrefix + weaponType + textSuffix;
		JSONObject jObj = postResp(url, params);
		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
		}

		//3.封装返回值：
		String msg = jObj.getString("msg");
		int code = Integer.valueOf(jObj.getString("code"));
		if(msg.equalsIgnoreCase("success") || code == 200 || code == 0){//??
			data = jObj.getJSONObject("data");
			JSONArray relations = data.getJSONArray("relationships"); //实体之间的关联关系，用来展示图谱上的圈圈
			JSONArray nodes = data.getJSONArray("nodes"); //实体之间的关联关系，用来展示图谱上的圈圈
			JSONObject attrs = (JSONObject) nodes.get(0);
			LinkedHashMap<String, List<Object>> attrKeyVals = fillAttrKeyVals(attrs); //实体的各个属性-属性值对  -- 一个属性可以对应多个属性值！！

			Map<String, String> id2name = getId2NameMap(nodes);

			String source = data.getJSONObject("source").getString("word");

			//4. 查询相似实体（可选）：
			List<String> recommends = recommendsQuery(entityName, weaponType);
			if(recommends == null){
				System.err.println("服务异常，url="+url+" 不能被访问。");
				return null;
			}

			//5. 获取装备实体的图片文件的url：eg. http://192.168.6.15:18804/media/weapon/MGM-29飞弹.jpg
			List<String> imgUrls = new ArrayList<>();
			String imgFile = null;
			if(attrs.getJSONObject("properties") != null && attrs.getJSONObject("properties").getJSONObject("图片") != null){
				imgFile = attrs.getJSONObject("properties").getJSONObject("图片").getString("value");
				String imgUrl = urlPrefix + LaunchListener.kbMediaUrl + imgFile;
				imgUrls.add(imgUrl);
			}
			entity = new KBEntity(bestEntityName, jObj, msg, code, relations, nodes, attrs, attrKeyVals, source, recommends, imgUrls, id2name, bias, weaponType);
		}
		else {
			entity = new KBEntity(bestEntityName, jObj, msg, code, null, null, null, null, null, null, null, null, null, null);//? entity = null;
		}

		//System.out.println("\nmsg:"+msg+"\ncode:"+code+"\n");

		return entity;
	}*/
	/**
	 * 
	 * @param query {"name":String,"label":String}
	 * @return
	 */
	public static KBEntity kbEntityQueryPro(JSONObject query){
		String entityType = null;
		String entityName = null;
		JSONObject data = new JSONObject();
		JSONObject jObj = new JSONObject();
		JSONArray relations = new JSONArray();
		JSONArray nodes = new JSONArray();
		JSONObject attrs = new JSONObject();
		LinkedHashMap<String, List<Object>> attrKeyVals = new LinkedHashMap<>();
		Map<String, String> id2name = new HashMap<>();
		JSONArray recommends = new JSONArray();
		List<String> imgUrls = new ArrayList<String>();
		String source = "";
		String msg;
		int code;
		List<String> bias = new ArrayList<String>();

		if(DEBUG){
			urlPrefix = "http://192.168.6.15:18804";
			apiUrl = "/api/kg/entities/graphs";
		}

		String name = query.getString("name");
		String label = query.getString("label");
		String UName=null;
		String Ulabel=null;
		try {
			UName = URLEncoder.encode(name, "utf-8");
			Ulabel = URLEncoder.encode(label, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = urlPrefix + apiUrl + "/?name=" + UName + "&label=" + Ulabel;
		jObj = getResp(url);
		if(jObj == null){
			//System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
			System.err.println("服务异常，url="+url+" 不能被访问。");
			return null;
		}
		msg = "msg";
		code  = 200;
		data = jObj.getJSONObject("data");
		relations = data.getJSONArray("relationships"); //实体之间的关联关系，用来展示图谱上的圈圈
		nodes = data.getJSONArray("nodes"); //实体之间的关联关系，用来展示图谱上的圈圈
		attrs = (JSONObject) nodes.get(0);
		attrKeyVals = fillAttrKeyVals(attrs); //实体的各个属性-属性值对  -- 一个属性可以对应多个属性值！！
		id2name = getId2NameMap(nodes);
		source = "";
		entityName = name;
		entityType = label;
		//4. 查询相似实体（可选）：
		recommends = recommendsQuery(entityName, entityType);
		String imgFile = null;
		if(attrKeyVals.containsKey("图片")){
			imgFile = ((JSONObject)attrKeyVals.get("图片").get(0)).getString("value");
			if (imgFile.isEmpty()==false){
				String imgUrl = urlPrefix + LaunchListener.kbMediaUrl + imgFile;
				imgUrls.add(imgUrl);
			}
		}
		KBEntity entity = new KBEntity(entityName, jObj, msg, code, relations, nodes, attrs, attrKeyVals, source, recommends, imgUrls, id2name, bias, entityType);
		return entity;
	}


	public static KBEntity kbEntityQuery(String entityName){
		KBEntity entity = null;

		//		entityName = rectify(entityName); //if(entityName.equalsIgnoreCase("F-22")) entityName = "F-22猛禽战斗机";//F-22被错误地查询成KR-22. --LDQ, 2019.12.24

		//1.第一步，模糊查询获取实体的类型：
		if(DEBUG){
			urlPrefix = "http://192.168.6.15:18804";
			//candidatesUrl = "/es/search?q=";//"/es/search?q=";//<--返回几个候选实体  //"/es/search?q=";<--返回很多候选实体+文章 //"/es/search/entity?q=";《--返回输入提示 
			candidatesUrl = "/api/search/entities/?q=";//<--返回几个候选实体  //"/es/search?q=";<--返回很多候选实体+文章 //"/es/search/entity?q=";《--返回输入提示 

		}
		try {
			entityName = URLEncoder.encode(entityName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = urlPrefix + candidatesUrl + entityName; 

		System.out.println("kbEntityQuery(): url=\n"+url);

		JSONObject candidates =  getResp(url);
		if(candidates == null){
			System.err.println("服务异常，url="+url+" 不能被访问。");
			return null;
		}

		JSONObject data = (JSONObject)candidates.get("data");
		if(data == null){
			System.err.println("解析异常，"+entityName+": 返回数据data字段为空，无法进一步解析实体标准名称和别名。");
			return null;
		}

		//KBEntity 参数
		String weaponType     = null;
		String bestEntityName = null;
		String UbestEntityName=null;
		String UweaponType=null;
		JSONObject jObj = new JSONObject();
		JSONArray relations = new JSONArray();
		JSONArray nodes = new JSONArray();
		JSONObject attrs = new JSONObject();
		LinkedHashMap<String, List<Object>> attrKeyVals = new LinkedHashMap<>();
		Map<String, String> id2name = new HashMap<>();
		JSONArray recommends = new JSONArray();
		List<String> imgUrls = new ArrayList<String>();
		String source = "";
		String msg;
		int code;

		JSONArray entitys = (JSONArray)(data).get("entity");
		JSONObject alias = null;
		List<String> bias = (List<String>)data.get("bias"); //??
		if(entitys == null || entitys.size() == 0){
			//			System.out.println(entityName+": 该实体不存在。");
			//			return null;
			alias = (JSONObject) data.get("alias");
			if(alias == null){
				System.err.println("解析异常，"+entityName+": data数据中，entity和bias均为空，无法进一步解析实体标准名称和别名。");
				return null;
			}
			else {
				weaponType     = alias.getString("label");
				bestEntityName = alias.getString("name");
				//2.第二步，精确查询，获取实体的属性值、关联关系：
				if(DEBUG){
					urlPrefix = "http://192.168.6.15:18804";
					apiUrl = "/api/kg/entities/graphs";
				}
				try {
					UbestEntityName = URLEncoder.encode(bestEntityName, "utf-8");
					UweaponType = URLEncoder.encode(weaponType, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				url = urlPrefix + apiUrl + "/?name="+UbestEntityName + "&label="+ UweaponType;
				jObj = getResp(url);
				if(jObj == null){
					//System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
					System.err.println("服务异常，url="+url+" 不能被访问。");
					return null;
				}
				//else System.out.println("jObj="+jObj);

				//3.封装返回值：
				//				String msg = jObj.getString("msg");
				//				int code = Integer.valueOf(jObj.getString("code"));
				//				if(msg.equalsIgnoreCase("success") || code == 200 || code == 0){//??
				msg = "msg";
				code  = 200;
				data = jObj.getJSONObject("data");
				relations = data.getJSONArray("relationships"); //实体之间的关联关系，用来展示图谱上的圈圈
				nodes = data.getJSONArray("nodes"); //实体之间的关联关系，用来展示图谱上的圈圈
				attrs = (JSONObject) nodes.get(0);
				attrKeyVals = fillAttrKeyVals(attrs); //实体的各个属性-属性值对  -- 一个属性可以对应多个属性值！！
				id2name = getId2NameMap(nodes);
				source = "";//data.getJSONObject("source").getString("word");
				//4. 查询相似实体（可选）：
				recommends = recommendsQuery(bestEntityName, weaponType);//{"name":String,"type":String}
				if(recommends == null){
					System.err.println("服务异常，url="+url+" 不能被访问。");
					return null;
				}
				//5. 获取装备实体的图片文件的url：eg. http://192.168.6.15:18804/media/weapon/MGM-29飞弹.jpg
				String imgFile = null;
				if(attrKeyVals.containsKey("图片")){
					imgFile = ((JSONObject)attrKeyVals.get("图片").get(0)).getString("value");
					if (imgFile.isEmpty()==false){
						String imgUrl = urlPrefix + LaunchListener.kbMediaUrl + imgFile;
						imgUrls.add(imgUrl);
					}
				}
			}
		}
		else{
			//相似实体列表
			for (int i=0; i<entitys.size();i++){
				JSONObject possibleEntity = new JSONObject();
				String possibleName = entitys.getJSONObject(i).getJSONObject("_source").getString("name");
				String possibleType = entitys.getJSONObject(i).getString("_type");
				possibleEntity.put("name",possibleName);
				possibleEntity.put("type", possibleType);
				recommends.add(possibleEntity);
			}
			msg = "msg";
			code  = 200;
		}
		if((weaponType == null || bestEntityName == null) && recommends.size()==0){
			System.err.println("解析异常，"+"weaponType="+weaponType+", bestEntityName="+bestEntityName+", 实体查询时，数据无法进一步解析。");
			return null;
		}

		entity = new KBEntity(bestEntityName, jObj, msg, code, relations, nodes, attrs, attrKeyVals, source, recommends, imgUrls, id2name, bias, weaponType);
		return entity;
	}

	/**
	 * 判断一个URI是否存在。
	 * ----LDQ，2019.12.31
	 * 
	 * @return
	 */
	public static boolean existsURI(String url){
		//		try{
		//			java.net.HttpURLConnection.setFollowRedirects(false);
		//			java.net.HttpURLConnection con = (java.net.HttpURLConnection)new URL(uri).openConnection();
		//			con.setRequestMethod("HEAD");
		//			boolean result = con.getResponseCode() == java.net.HttpURLConnection.HTTP_OK;
		//			return result;
		//		}catch(Exception e){
		//			e.printStackTrace();
		//		}
		if(url.contains(" ")){
			try{
				url = UrlEncoding.encode(url, "utf-8");
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}

		CloseableHttpClient client = HttpClients.createDefault();
		try{
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			return statusCode == java.net.HttpURLConnection.HTTP_OK;
		}catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 实体链接：给定的实体名称ne，看看会链接到知识库里面哪个标准实体名称上去。
	 * 如果知识库里没有该实体，返回null。
	 * ----LDQ，2019.12.31
	 * 
	 * @param ne
	 * @return
	 */
	public static String entityLink(String ne){
		String normName = null;

		return normName;
	}

	/**
	 * 给定一个装备的标准名称，获取它的所有别名列表bias。
	 * ----LDQ, 2019.12.12
	 * 
	 * @param normName
	 * @return
	 */
	public static Set<String> getBias(String normName, String category){
		Set<String> bias = new HashSet<>();
		/* 
		KBEntity entity = kbEntityQuery(normName);
		if(entity == null){
			return bias;
		}

		LinkedHashMap<String, List<Object>> attrKV = entity.getAttrKeyVals();
		if(attrKV == null){
			return bias;
		}

		List<Object> biasObj = attrKV.get("bias");
		if(biasObj == null){
			return bias;
		}

		for(Object bs : biasObj){
			bias.add(bs.toString());
		}*/


		//别名查询时，解析异常。url=http://192.168.6.15:18804/api/entity/dg, 
		//params={"label":"aircraft","text":"C-5\"银河\"飞机"}

		//		if(DEBUG) normName = "“猎迷”反潜巡逻机";//"XK2'黑豹'主战坦克";//"XK2“黑豹“主战坦克";//"C-5\"银河\"飞机";//"苏-30战斗机";//"枭龙战机";

		JSONObject params = new JSONObject();
		params.put("label", category);
		params.put("text", normName);

		String biasUrl = LaunchListener.biasUrl;
		String url = "http://" + ip + ":" + port + biasUrl;
		if(DEBUG) url = "http://192.168.6.15:18804/api/entity/dg";
		JSONObject jObj = postResp(url, params.toJSONString());
		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
		}
		else try{ //bias.add(normName); //?? ---- 待完善
			JSONObject data = (JSONObject)jObj.get("data");
			boolean err = true;
			int errorCode = 0;
			if(data != null){
				JSONArray nodes = (JSONArray)data.get("nodes");
				if(nodes != null && nodes.size() > 0){
					JSONObject obj = nodes.getJSONObject(0);
					if(obj != null){
						JSONObject properties = obj.getJSONObject("properties");
						if(properties != null){
							JSONObject alias = properties.getJSONObject("别名");
							if(alias != null){
								String names = alias.getString("value");
								bias.addAll(Arrays.asList(names.split("\\$")));
								err = false;
							}
							alias = properties.getJSONObject("代号");
							if(alias != null){
								String names = alias.getString("value");
								bias.addAll(Arrays.asList(names.split("；|;|,|，|、")));
								err = false;
							}
							if(err) errorCode = 4;
						}else errorCode = 3;
					}else errorCode = 2;
				}else errorCode = 1;
			}

			if(err)	System.err.println("别名查询为空。"+(errorCode==4?""+normName:("errorCode="+errorCode+" url="+url+", params="+params.toString())));

		}catch(Exception e){
			e.printStackTrace();
			System.err.println("别名查询时，解析异常。url="+url+", params="+params.toString());
		}

		return bias;
	}

	//	/**
	//	 * 给定实体名称，查询实体类型。
	//	 * ----LDQ，2019.11.11
	//	 * 
	//	 * @param url
	//	 * @param entityName
	//	 * @return
	//	 */
	//	private static String getType(String url){
	//		JSONObject jObj =  getResp(url);
	//		JSONArray jArray = (JSONArray)((JSONObject)jObj.get("data")).get("entity");
	//		if(jArray == null || jArray.size() == 0) return null;
	//		
	//		Object result = jArray.get(0);
	//		result = ((JSONObject)result).get("_type");
	//		
	//		return result.toString();
	//	}

	/**
	 * 查询相似的实体。
	 * ----LDQ，2019.11.12
	 * 
	 * @param entityName
	 * @return
	 */
	public static JSONArray recommendsQuery(String entityName, String weaponType){
		JSONArray recommends = new JSONArray();
		try {
			entityName = URLEncoder.encode(entityName, "utf-8");
			weaponType = URLEncoder.encode(weaponType, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(DEBUG) recommendUrl = "/api/search/entities/recommendations/correlation/?q=";
		String url = urlPrefix + recommendUrl + entityName + "&t=" + weaponType; //----LDQ，2019.12.13

		JSONObject query = getResp(url);
		if(query == null){
			System.err.println("服务异常，url="+url+" 不能被访问。");
			return null;
		}

		if((Integer)(query.get("code")) == 0){
			recommends = query.getJSONArray("data");
		}
		return recommends;
	}

	/**
	 * 从图谱的节点数据中提取出实体id到实体name的映射；便于前端展示节点的关联关系时使用。
	 * eg：
	 * 图谱节点(nodes): [{"id":"0x1f0ad54","word":"MGM-29飞弹","properties":....{"id":"0x25f9ae9","word":"美国","properties":{},"labels":"loc"},{"id":"0x1d5355f","word":"猎鹰9号运载火箭","properties":{},"labels":"spaceship"},
	 * --> {"0x1f0ad54":"MGM-29飞弹", "0x25f9ae9":"美国", "0x1d5355f":"猎鹰9号运载火箭", ....} 
	 * 
	 * ----LDQ， 2019.11.15
	 * 
	 * @param relations
	 * @return 实体id到实体name的映射
	 */
	private static Map<String, String> getId2NameMap(JSONArray nodes){
		Map<String, String> id2name = new HashMap<>();

		for(int i=0; i<nodes.size(); i++){
			JSONObject node = nodes.getJSONObject(i);
			String id = node.getString("id");
			String name = node.getString("name");
			id2name.put(id, name);
		}

		return id2name;
	}

	/**
	 * 从properties中获取属性值对，然后存入attrKeyVals中
	 * 
	 * ----LDQ, 2019.11.15
	 * 
	 * attrs-->{"id":"0x1f0ad54",
		 		"word":"MGM-29飞弹",  
  attrKeyVals-->"properties":{
	                 "弹头":{"source":"维基百科","value":"高爆炸药；W52核弹头"},
	                 "重量":{"source":"维基百科","value":"4,530公斤（9,990英磅）"},
	                 "图片":{"source":"维基百科","value":"MGM-29飞弹.jpg"},
	                 "长度":{"source":"维基百科","value":"10.52米（34.5英尺）"},
	                 "制造国家":{"source":"维基百科","value":"美国"},
	                 "发动机":{"source":"维基百科","value":"固体燃料火箭发动机；200千牛"},
	                 "使用方":{"source":"维基百科","value":"美国陆军"},
	                 "直径":{"source":"维基百科","value":"790毫米（31英寸）"},
	                 "作战范围":{"source":"维基百科","value":"25英里（40公里）至84英里（135公里）"},
	                 "类型":{"source":"维基百科","value":"短程弹道导弹"}
	              }, 
		         "labels":"missile"
		       }

	 * @param attrs
	 * @return attrKeyVals eg.: ["弹头"=["高爆炸药；W52核弹头"], "重量"=["4,530公斤（9,990英磅）"], "图片"=["MGM-29飞弹.jpg"], "测试属性名"=["测试属性值"], "长度"=["10.52米（34.5英尺）"], "制造国家"=["美国"], "发动机"=["固体燃料火箭发动机；200千牛"], "使用方"=["美国陆军"], "直径"=["790毫米（31英寸）"], "作战范围"=["25英里（40公里）至84英里（135公里）"], "类型"=["短程弹道导弹"]]
	 */
	/**
	 * 填充属性摘要。
	 * 
	 * 注：待改进。需注意以下两点：
	 * 1、目前版本只支持value里面不再嵌套JSONObject，只是一个字符串的情况；
	 * 2、目标版本只支持一个属性只有一个value值的情况，后续需要支持多个value值。所以目前用List<Object> values而不是String value来表示一个key的值，这是为了后续兼容考虑的。
	 * ----LDQ, 2019.11.15
	 * 
	 * 修改属性解析方式，但是properties应该为List<Map<>>
	 * ---hzh,20191126
	 * 
	 * @param attrs
	 * @return
	 */
	private static LinkedHashMap<String, List<Object>> fillAttrKeyVals(JSONObject attrs){ //?? 见两点“待改进”
		LinkedHashMap<String, List<Object>> attrKeyVals = new LinkedHashMap<>();
		if(null == attrs || !attrs.containsKey("properties"))
			return attrKeyVals;

		JSONArray attrMap = attrs.getJSONArray("properties");
		//Iterator<Entry<String, Object>> it = attrMap.entrySet().iterator();
		for(Object obj : attrMap){
			JSONObject item = (JSONObject)obj;
			String name  = item.getString("name");
			String value = item.getString("value");
			String sourceName = item.getJSONObject("source").getString("source");
			String sourceId = item.getJSONObject("source").getString("id");
			String sourceScore = item.getJSONObject("source").getString("score");
			JSONObject valueInfo = new JSONObject();
			valueInfo.put("value", value);
			valueInfo.put("sourceName", sourceName);
			valueInfo.put("sourceScore", sourceScore);
			valueInfo.put("sourceId", sourceId);
			List<Object> valList = new ArrayList<>();
			valList.add(valueInfo);
			attrKeyVals.put(name, valList);
		}

		/*while(it.hasNext()){
			Entry<String, Object> entry = it.next();
			Map<String, Object> valMap = (Map<String, Object>) entry.getValue();
			List<Object> valList = new ArrayList<>();
			if(null == valMap || valMap.isEmpty()){
				attrKeyVals.put(entry.getKey(), valList);
				continue;
			}
			valList.add(valMap.get("value"));
			attrKeyVals.put(entry.getKey(), valList);
		}*/

		return attrKeyVals;		
	}

	/*	private static LinkedHashMap<String, List<Object>> fillAttrKeyVals_0(JSONObject attrs){ //?? 见两点“待改进”
		LinkedHashMap<String, List<Object>> attrKeyVals = new LinkedHashMap<>();
		if(null == attrs || !attrs.containsKey("properties"))
			return attrKeyVals;

		Map<String, Object> attrMap = attrs.getJSONObject("properties");
		Iterator<Entry<String, Object>> it = attrMap.entrySet().iterator();

		while(it.hasNext()){
			Entry<String, Object> entry = it.next();
			Map<String, Object> valMap = (Map<String, Object>) entry.getValue();
			List<Object> valList = new ArrayList<>();
			if(null == valMap || valMap.isEmpty()){
				attrKeyVals.put(entry.getKey(), valList);
				continue;
			}
			valList.add(valMap.get("value"));
			attrKeyVals.put(entry.getKey(), valList);
		}

		return attrKeyVals;		
	}*/

	//	private static LinkedHashMap<String, List<Object>> fillAttrKeyVals(JSONObject attrs){ //?? 见两点“待改进”
	//		LinkedHashMap<String, List<Object>> attrKeyVals = new LinkedHashMap<>();
	//		
	//		String strProp = attrs.getJSONObject("properties").toJSONString();
	//		while(strProp.length() > 1){
	//			String key = strProp.substring(1, strProp.indexOf(':'));
	//			String value = strProp.substring(strProp.indexOf("\"value\":")+8, strProp.indexOf("\"}")+1);
	//			List<Object> values = new ArrayList<>();
	//			values.add(value);
	//			attrKeyVals.put(key, values); 
	//			strProp = strProp.substring(strProp.indexOf("\"}")+2);
	//			System.out.println(strProp);
	//		}
	//		
	//		return attrKeyVals;		
	//	}	
	/**
	 * 人工修正之属性修改。为防止以后文章融合修改属性被覆盖，修改完的属性赋予最高可信度—人工修正            --wjt ++
	 * 硬修改属性值 e.g.参数 newProperty = {"id":"2732539","name":"测试属性名2","value":"测试属性值2"}
	 * @param newProperty
	 */
	public static void editEnittyProperty(JSONObject newProperty){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			kbProperEditUrl = "/api/kg/entities/";//"/api/proper";
		}
		newProperty.put("source", 4);

		String url = "http://"+ip+":"+port+kbProperEditUrl + newProperty.getString("id")+"/properties/";

		putResp(url, newProperty.toJSONString());



	}
	/**
	 * 人工修正之属性删除。
	 * e.g.参数 toDelete = {"id":"2732539","name":"测试属性名5"，"value":"测试属性值5"}
	 * @param deleteProperty
	 */
	public static void deleteEnittyProperty(JSONObject deleteProperty){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			kbProperEditUrl = "/api/kg/entities/";//"/api/proper";
		}


		String url = "http://"+ip+":"+port+kbProperEditUrl + deleteProperty.getString("id") + "/properties/";

		JSONObject toDelete = new JSONObject();
		toDelete.put("name", deleteProperty.getString("name"));
		toDelete.put("value", deleteProperty.getString("value"));
		deleteResp(url, toDelete.toJSONString());



	}

	/**
	 * 添加实体返回实体id
	 * @param addEntity 格式{"name":"测试枪支11","label":"guns","source":1 #信源id}
	 */
	public static String addEntity(JSONObject addEntity){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			kbEntityEditUrl = "/api/kg/entities/";//"/api/entity";
		}


		String url = "http://"+ip+":"+port+kbEntityEditUrl;
		postResp(url, addEntity.toJSONString());
		addEntity.remove("source");
		String id = kbNodeIdQuery(addEntity);
		return id;

	}


	public static void deleteEnitty(String entityId){
		System.out.println(entityId);
		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			kbEntityEditUrl = "/api/kg/entities/";//"/api/entity";
		}
		String url = "http://"+ip+":"+port+kbEntityEditUrl+entityId+"/";
		deleteResp(url,null);
	}

	//	/**
	//	 * 人工修正之实体删除。
	//	 * e.g.参数 deleteEntity = {"label":"guns","name":"测试枪支"}
	//	 * @param deleteEntity
	//	 */
	//	public static void deleteEnitty(JSONObject deleteEntity){
	//		String id = kbNodeIdQuery(deleteEntity);
	//		System.out.println(id);
	//		if(DEBUG){
	//			ip = "192.168.6.15";
	//			port = "18804";
	//			kbEntityEditUrl = "/api/kg/entities/";//"/api/entity";
	//		}
	//		String url = "http://"+ip+":"+port+kbEntityEditUrl+id+"/";
	//		deleteResp(url,null);
	//	}
	//	/**
	//	 * 人工修正之实体删除。
	//	 * e.g.参数 deleteEntity = {"label":"guns","text":"测试枪支"}
	//	 * @param deleteEntity
	//	 */
	/*public static void deleteEnitty(JSONObject deleteEntity){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			kbEntityEditUrl = "/api/entity";
		}


		String url = "http://"+ip+":"+port+kbEntityEditUrl;

		deleteResp(url, deleteEntity.toJSONString());



	}*/

	/**
	 * 知识图谱服务端更新信源信息 --wjt
	 * 格式为 {"name":"中华军事网","host":"https://3g.china.com","score":0.72}
	 * @param sourceObj
	 */



	public static void updateSource(JSONObject sourceObj){

		if(DEBUG){
			ip = "192.168.6.15";//"kg.cetc32.yunfutech.com";//
			port = "18804";//"";//
			sourceManageUrl = "/api/setting/sources/";//"/api/sourcemanage/";
		}

		JSONObject sourceDict = getSourceIdDict();
		String id = sourceDict.getString(sourceObj.getString("name"));

		String url = "http://"+ip+":"+port+sourceManageUrl+id+"/";
		putResp(url,sourceObj.toJSONString());
	}


	/**
	 * 删除信源 --wjt
	 * 格式为 {"name":"中华军事网","host":"https://3g.china.com","score":0.7}
	 * @param sourceObj
	 */
	public static void deleteSource(JSONObject sourceObj){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			sourceManageUrl = "/api/setting/sources/";//"/api/sourcemanage/";
		}

		JSONObject sourceDict = getSourceIdDict();
		String id = sourceDict.getString(sourceObj.getString("name"));

		String url = "http://"+ip+":"+port+sourceManageUrl+id+"/";
		//deleteResp(url,null);
		JSONObject deleteObj = new JSONObject();
		deleteObj.put("name", "delete");
		deleteObj.put("host", "x");
		deleteObj.put("score",0);
		putResp(url,deleteObj.toJSONString());

	}





	/**
	 * 知识图谱服务端上传信源信息，如果信源名重复则禁止上传
	 * 格式为 {"name":"中华军事网","host":"https://3g.china.com","score":0.7}
	 * @param sourceObj
	 */
	public static void uploadSource(JSONObject sourceObj){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			sourceManageUrl = "/api/setting/sources/";//"/api/sourcemanage/";

		}

		JSONObject sourceDict = getSourceDict();
		if(sourceDict.getString(sourceObj.getString("name"))!=null){
			updateSource(sourceObj);
		}else if(sourceDict.getString("delete")!=null){	
			JSONObject sourceIdDict = getSourceIdDict();
			String id = sourceIdDict.getString("delete");
			String url = "http://"+ip+":"+port+sourceManageUrl+id+"/";
			putResp(url,sourceObj.toJSONString());
		}else{
			String url = "http://"+ip+":"+port+sourceManageUrl;
			/*if(sourceDict.getString(sourceObj.getString("name"))==null){
			postResp(url,sourceObj.toJSONString());
		}*/
			postResp(url,sourceObj.toJSONString());
		}

	}

	/**
	 * 获取服务端得信源表 --wjt
	 */
	public static JSONObject getSourceDict(){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			sourceManageUrl = "/api/setting/sources/";//"/api/sourcemanage/";
		}

		String url = "http://"+ip+":"+port+sourceManageUrl;

		JSONObject sourceDict = new JSONObject();
		JSONArray data = null;
		if(getResp(url).containsKey("results")){
			data = getResp(url).getJSONArray("results");

			for(int i=0; i<data.size(); i++){

				String name = data.getJSONObject(i).getString("name");
				Float score = data.getJSONObject(i).getFloatValue("score");

				sourceDict.put(name, score);	
			}
		}

		return sourceDict;	

	}

	/**
	 * 获取信源id表
	 * @return
	 */
	public static JSONObject getSourceIdDict(){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			sourceManageUrl = "/api/setting/sources/";

		}

		String url = "http://"+ip+":"+port+sourceManageUrl;
		JSONArray data = getResp(url).getJSONArray("results");
		JSONObject sourceIdDict = new JSONObject();

		for(int i=0; i<data.size(); i++){
			String name = data.getJSONObject(i).getString("name");
			String id = String.valueOf(data.getJSONObject(i).getInteger("id"));
			sourceIdDict.put(name, id);	
		}
		return sourceIdDict;	
	}


	/**
	 * 知识融合融合的实体存储到图数据库，并返回未融合的实体属性数据  
	 * e.g. artObject = {"text":(String),"source":(String),"articleId":(Long)}
	 * @param artObject
	 * @return
	 */
	public static JSONArray kbFusion(JSONObject artObject){

		if(DEBUG){
			ip = "192.168.6.15";
			port = "18804";
			kbFusionUrl = "/api/fusion/article/";//"/api/mergetext";
		}

		JSONObject params = new JSONObject();

		String url = "http://"+ip+":"+port+kbFusionUrl;

		String text = artObject.getString("text");
		text = text.replace("\n", "。").replace("。。", "。");//??以后yf新版本也许不需要
		String source = artObject.getString("source");		
		String articleId = artObject.get("articleId").toString();

		params.put("text", text);
		params.put("source",source);
		params.put("id", articleId);

		JSONObject fusionResp = postResp(url,params.toJSONString());

		System.out.println(fusionResp.toJSONString());

		JSONArray unfusionAttr = new JSONArray();

		try{//增加try-catch --LDQ,2020.1.11 LuoYang
			JSONObject unfusion = fusionResp.getJSONObject("unfusion");
			if(unfusion != null){
				unfusionAttr = unfusion.getJSONArray("attribute");
			}
			else{
				System.err.println("kbFusion()融合时，解析异常。unfusion=null。url="+url+", params="+params.toString());
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println("kbFusion()融合时，解析异常。url="+url+", params="+params.toString());
		}

		return unfusionAttr;

	}


	/**
	 * 将未融合的属性转化封装成多值属性表对象，为下一步存储到oracle数据库做准备；
	 * @param unfusionAttr
	 * @return
	 */
	public static List<MultiAttributes> unfusionAttrToMultiAttr(JSONArray unfusionAttr){

		List<MultiAttributes> multiAttrArray= new ArrayList<>();


		if (unfusionAttr != null && unfusionAttr.size() > 0){
			for(int i=0; i<unfusionAttr.size(); i++){

				MultiAttributes multiAttributes = new MultiAttributes();

				multiAttributes.setArticleId(Long.valueOf(unfusionAttr.getJSONObject(i).getString("new_text_id")));
				multiAttributes.setEntityId(Long.valueOf(unfusionAttr.getJSONObject(i).getString("entity1_id")));
				multiAttributes.setEntityName(unfusionAttr.getJSONObject(i).getString("entity1_name"));
				multiAttributes.setAttributeName(unfusionAttr.getJSONObject(i).getString("attribute_name"));
				multiAttributes.setAttributeValue(unfusionAttr.getJSONObject(i).getString("attribute_value"));
				multiAttributes.setSourceName(unfusionAttr.getJSONObject(i).getString("new_source"));
				multiAttrArray.add(multiAttributes);		
			}
		}
		return multiAttrArray;
	}





	/**
	 * 关系抽取；输入一段文本，提取文本中实体之间的相互关系 --wjt
	 * @param text
	 * @return
	 */
	public static JSONArray relaExtract(String text){

		if(DEBUG){
			ip = "192.168.6.15";
			relationPort = "17398";
			relationUrl = "/relation_cetc32_v2";
		}

		String url = "http://"+ip+":"+relationPort+relationUrl;
		String params = "{\"text\": \""+text+"\" }";

		JSONObject jObj = postResp(url,params);
		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
			return null;
		}

		JSONArray jArray = (JSONArray) jObj.get("relations");
		return jArray;

	}

	/**
	 * 属性抽取；输入一段文本返回文章中的实体属性 --wjt
	 * @param text
	 * @return
	 */
	public static JSONArray attrExtract(String text){

		if(DEBUG){
			ip =  "192.168.6.15";
			attributePort = "12345";
			attributeUrl = "/attribute_cetc32_v1";
		}


		String url = "http://"+ip+":"+attributePort+attributeUrl;
		String params = "{\"text\": \""+text+"\" }";
		JSONObject jObj = postResp(url,params);
		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
			return null;
		}

		JSONArray jArray = (JSONArray)jObj.get("attributes_from_text");
		return jArray;

	}

	public static List<cmsEvent> eventExtract(String text,Long savedid) {
		if(DEBUG){
			ip =  "192.168.6.15";
			eventPort = "21021";
			eventUrl = "/api/event/";
		}

		text = text.replace("\n", "。").replace("\r", "。").replace("。。", "。");//??（以后新版本也许不需要）。除去事件返回的结果中的\n，产生原因待确定？？？

		String url = "http://"+ip+":" + eventPort + eventUrl;
		String params = "{\"text\": \""+text+"\",\"default_time\": \"\"}";
		JSONObject jObj = postResp(url,params);
		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
			return new ArrayList<cmsEvent>();
		}

		JSONArray eventArr = (JSONArray) jObj.get("events");//获取事件解析以后包含触发词语和事件元素的列表
		String eventContext = (String) jObj.get("text");//获取事件完整的上下文信息
		List<cmsEvent> eventResults = eventToParams(eventArr, savedid,eventContext);//把eventArr、savedid、eventContext传递给eventToParams()提取参数
		return eventResults;
	}

	/**
	 * 事件抽取返回值转化为融合接口对应的参数形式
	 * 将事件返回结果对应的参数封装在cmsEvent对象中，入库
	 * @param event
	 * @return
	 * NSS   2020-4-17
	 */
	private static List<cmsEvent> eventToParams(JSONArray eventArr, long savedId,String eventContext) {
		List<cmsEvent> eventResult = new ArrayList<>();
		if(eventArr != null){
			for(Object events:eventArr){

				cmsEvent eventParams = new cmsEvent();
				JSONObject eventTrigger = (JSONObject) ((JSONObject) events).get("trigger");//解析出事件列表中每个事件的trigger
				if(eventTrigger != null){
					eventParams.actions = (String) ((JSONObject) eventTrigger).get("text");//解析出该事件trigger对象中的text，即触发词本身
					
				} else {
					eventParams.actions = "触发词不存在";
				}

				String eventType = (String) ((JSONObject) events).get("type");//解析出事件列表中每个事件的类型
				if(eventType != null){
					eventParams.title = eventType;
				} else {
					eventParams.title = eventParams.actions;
				}

				eventParams.task = eventParams.actions;//将事件触发词定义为事件的任务

				String description = (String) ((JSONObject) events).get("description");//解析出事件列表中每个事件的事件描述
				if(description != null){
					eventParams.activity = description;
					eventParams.task = description;//将事件触发词定义为事件的描述
				} else {
					eventParams.activity = "无该事件描述";
				}

				eventParams.context = eventContext;//提取出事件完整的上下文

				JSONArray eventArg = (JSONArray) ((JSONObject) events).get("arguments");//解析出事件列表中每个事件的arguments
				if(eventArg != null){
				
					int neCounts = 0;
					for(Object argument:eventArg){
						if((eventParams.time == null) && (((String) ((JSONObject) argument).get("role")).equals("time"))){
							eventParams.time = (String) ((JSONObject) argument).get("text");//解析出该事件time对象中的text，即事件发生的时间

							boolean flag = false;//判断eventParams.time的中是否包含数字，如果包含数字，取时间戳，默认不包含数字flag为false
							java.util.regex.Pattern p = java.util.regex.Pattern.compile(".*\\d+.*");
							Matcher m = p.matcher(eventParams.time);
							if(m.matches()){
								flag = true;
							}
							
							if(eventParams.time.length() != 5 && eventParams.time.length() != 10 && flag == true){
								String stampTemp = eventParams.time.replace("年", "").replace("月", "").replace("日", "");
								eventParams.stamp1 = Long.parseLong(stampTemp);//取时间戳1
							}

							if(eventParams.time.length() == 5 && flag == true){ //时间形式为“xxxx年”
								String stamp1Temp = eventParams.time.replace("年", "").replace("月", "").replace("日", "");
								eventParams.stamp1 = Long.parseLong(stamp1Temp);//取时间戳1
							} 
							
							if(eventParams.time.length() == 7 && flag == true){//时间形式为“xxxx年x月”
								String stamp1Temp = eventParams.time.substring(0, 4);
								eventParams.stamp1 = Long.parseLong(stamp1Temp);//取时间戳1
							} 
							
							if(eventParams.time.length() == 8 && flag == true){//时间形式为“xxxx年xx月”
								String stamp1Temp = eventParams.time.substring(0, 4);
								eventParams.stamp1 = Long.parseLong(stamp1Temp);//取时间戳1
							}
							
							if(eventParams.time.length() == 10 && flag == true){//时间形式为“xxxx-xxxx年”
								String[] times = (eventParams.time).split("-");
								if(times.length>0) eventParams.stamp1 = Long.parseLong(times[0].substring(0, 4));//取时间戳1
								if(times.length>0) eventParams.stamp2 = Long.parseLong(times[1].substring(0, 4));//取时间戳2
							} 
						} 

						if(((String) ((JSONObject) argument).get("role")).equals("subject")){//解析出事件的主体
							if((String) ((JSONObject) argument).get("ner") != "time" && (String) ((JSONObject) argument).get("ner") != "loc" && (String) ((JSONObject) argument).get("ner") != "null"){
								eventParams.ne = (String) ((JSONObject) argument).get("text");
							}
							neCounts++;
						}

						if(((String) ((JSONObject) argument).get("role")).equals("loc")){//解析出事件涉及的地点实体
							eventParams.locations = (String) ((JSONObject) argument).get("text");
							neCounts++;
						}

						if(((String) ((JSONObject) argument).get("role")).equals("country")){//解析出事件中涉及的国家
							eventParams.neCountry = (String) ((JSONObject) argument).get("text");
							neCounts++;
						}

					}
					
					/*
					 * 1.如果事件抽取的接口没有抽取出事件的时间，则自行处理获取事件的时间
					 * 2.获取的原则是匹配出事件中所有的时间实体，然后将这些实体与触发词的距离做比较，取距离触发词最近的时间词作为该事件的时间
					 */
					if(eventParams.time == null){//如果该事件中没有解析出时间，就不再调用接口去获取事件的时间
//						String eventTime = getEventTime(eventContext,eventParams,eventParams.actions);//处理获取文章中距离触发词最近的时间实体
//					    if(eventTime == null) eventParams.time = "未抽取出时间";
						if(eventParams.time == null) eventParams.time = "null";
						
					}
					eventParams.neCount = neCounts + "个";//统计出该事件中的所有实体数目
				}
				eventParams.cmsId = savedId;//保存文章的Id
				if(eventParams.cmsId == null) eventParams.cmsId = (long) 0;
				eventResult.add(eventParams);	
			}
		}
		return eventResult;
	}

//	private static String getEventTime(String eventContext, cmsEvent eventParams,String trigger) {
//		LinkedHashMap<String, String> ner2types = ner(eventContext);
//		long triggerId = (Long) null;
////		JSONArray entities = jObj.getJSONArray("words");//调用ner接口，获取ner的分 词后的text和id
//		for(Object entity:entities){
//			if(((String)((JSONObject) entity).get("text")) == trigger){
//				triggerId = (long)((JSONObject) entity).get("id");
//			}
//		}
//		
//		long timesId = (Long) null;
//		HashSet<String> times = new HashSet<>();
//		HashMap<String, Long> distMap = new HashMap<>();
//		for(Map.Entry<String, String> map:ner2types.entrySet()){
//			if(map.getValue() == "time"){
//				times.add(map.getValue());
//				for(Object entity:entities){
//					if(((String)((JSONObject) entity).get("text")) == map.getKey()){
//						 timesId = (long)((JSONObject) entity).get("id");
//					}
//					long distance = Math.abs(triggerId - timesId);
//					distMap.put(map.getKey(),distance);
//				}
//			}
//		}
////		java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\d{1,2})月(\\d{1,2})日");
////		Matcher m = p.matcher(eventContext);
////		if(m.find()){
////			eventParams.time = m.group(1);	
////		}
//		String finalTime = sortTime(distMap);
//		return eventParams.time;
//	}
//
//
//	private static String sortTime(HashMap<String,Long> distMap) {
//		String resultTime = null;
//		List<Long> list = new ArrayList<>();
//		for(Map.Entry<String, Long> map:distMap.entrySet()){
//			list.add(map.getValue());
//		}
//		
//		long temp = list.get(0);
//		for(int i = 0;i < list.size();i++){
//			if(list.get(i) < temp)
//				temp = list.get(i);
//		}
//		
//		for(Map.Entry<String, Long> map:distMap.entrySet()){
//			if(map.getValue() == temp) resultTime = map.getKey();
//		}
//		return resultTime;		
//	
//	}

	/**
	 * 1、知识查询：action传来一个查询问句，则从知识库中查询到答案后返回
	 * 
	 * @param question
	 * @return 问题答案
	 */
	public static Map<String, Object> kbQA(String question){
		Map<String, Object> result = new HashMap<>();

		if(DEBUG){
			ip = "192.168.6.15";
			kbqaPort = "30010";
			kbqaUrl  = "/kbqa_cetc32";
		}
		String url = "http://" + ip + ":" + kbqaPort + kbqaUrl;		
		String params = "{\"question\":\"" + question + "\"}";

		System.out.println("url="+url+"\nparams="+params);

		JSONObject jObj = postResp(url, params);
		if(jObj == null){
			System.err.println("服务异常，url="+url+", params="+params.toString()+" 不能被访问。");
			return null;
		}

		String answer = jObj.getString("answer");
		if(answer.isEmpty()) answer = "我现在还不知道答案。让我想想..."; 
		result.put("answer", answer);
		result.put("msg", jObj.getString("message"));
		result.put("code", jObj.getString("code"));

		return result;

	}

	/**
	 * 判断一个输入关键词是否为问句 -------- 待改进。
	 * ----LDQ，2020.1.2 
	 * 
	 * @param input
	 * @return
	 */
	public static String getBestQuestion(String input){
		//1. 先用常规\常见的问句试探input是否为一个问句：
		String result = "";
		if(input.endsWith("？") || input.endsWith("?") || 
				input.contains("有多少") || input.contains("是多少") || 
				input.contains("是什么") || input.contains("是哪里") || 
				input.contains("哪个地方") || input.contains("在哪里") ||
				input.contains("多高") || input.contains("多宽") ||
				input.contains("几个") || input.contains("多重") ||
				input.contains("的") || input.contains("是谁") ||
				input.contains("哪家") || input.contains("怎么样") ||
				input.contains("是啥") || input.contains("如何")
				){
			result = input;
			return result;
		}

		//2. 如果以上不行，则判断是否问句为这样的结构： 一个实体名称（包括别名）+ 属性名称， 比如：歼-20首飞时间；辽宁号舰载机；辽宁号最大行程; 辽宁号排水量
		//   有时候问最xx时候，人们可能会把“最”给去掉。我们需要尝试加上“最大”、“最小”、“最长”、“最多”、“最高”、“最少”、“最低”、“最短”，等等。
		//2.1 从问句中发现实体名称，并取第一个为问句主语：
		LinkedHashSet<String> nes = KBUtil.findNEs(input, ""); //e.g 辽宁号排水量 ==> 辽宁号航空母舰
		String entityName = "";
		for(String ne : nes){
			entityName = ne; 
			break; //取第一个实体名称（未标准化）作为问句主语
		}
		if(entityName.isEmpty() || entityName.equals(input)) return result;

		//2.2 查询该实体的所有属性，并与问句进行（近似）匹配：
		KBEntity kbEntity = KBUtil.kbEntityQuery(entityName);
		Set<String> attrs = kbEntity.getAttrKeyVals().keySet();
		List<String> mosts = new ArrayList<>();
		mosts.add(""); //不要去掉这个!!
		mosts.add("最大"); mosts.add("最长"); mosts.add("最多"); mosts.add("最高"); mosts.add("最早");
		mosts.add("最小"); mosts.add("最短"); mosts.add("最少"); mosts.add("最低"); mosts.add("最晚"); 
		String bestQuestion = "";
		double maxScore = 0.0f;
		for(String attr : attrs){
			for(String most : mosts){
				String possibleQuestion = entityName + most + attr;//eg. 辽宁号航空母舰最大排水量
				double score = match(possibleQuestion, input);
				if(score > maxScore){
					bestQuestion = possibleQuestion;
					maxScore = score;
				}
			}
		}

		double threshold = 0.79f;//??这只是个估计值，待日后调整
		if(maxScore >= threshold){
			result = bestQuestion;
		}

		return result;
	}

	private static double match(String str1, String str2){
		double score =  cn.com.ecict.util.StringUtil.levenshtein_percent(str1, str2);
		return score;
	}

	/**
	 * 从一段字符串中发现不同于entryName的其他实体名称。-- 不含在超链接<a ... </a>里面的实体名称
	 * ----LDQ，2020.1.2
	 * 
	 * @param content
	 * @return
	 */
	public static LinkedHashSet<String> findNEs(String content, String entryName){
		LinkedHashSet<String> nes = new LinkedHashSet<>();//待替换的实体名称
		Set<String> allBias = LaunchListener.bias2NormEntity.keySet(); //所有实体的所有别名
		Set<String> myBias = LaunchListener.normEntity2Bias.get(entryName); //本实体的所有别名
		if(myBias == null) myBias = new HashSet<>();

		content = content.replaceAll("<a\\s.*?</a>", ""); //不含在超链接<a ... </a>里面的实体名称 --LDQ,2020.2.26

		//content = "歼-10S战斗教练机 。俄罗斯苏霍伊苏-57战斗机由于。";//"整个首飞过程在歼-10S战斗教练机陪伴下完成 。俄罗斯苏霍伊苏-57战斗机由于研制进度几度推迟。";

		int len = content.length();
		int i=0; 
		while(i<len-1){ //以线性 2n的速度在文本中匹配实体名称
			String c2 = content.substring(i, i+2);
			//System.out.println("c2="+c2+" i="+i);

			if(LaunchListener.allBiasHead.contains(c2)){
				String name = null;
				String str = "" + content.charAt(i);
				int p = i+1;
				int pAtBestFinding = 0;
				while(p<len){//在一个子句中寻找实体名称（尽量找较长的名称）
					char c = content.charAt(p);
					//System.out.println(" c="+c+" p="+p);

					if(c=='，' || c=='。' || c=='；' || c=='、' || c=='？' || c=='！' ||
							c==',' || c=='.' || c==';' || c=='?' || c=='!'){
						break;
					}
					str += content.charAt(p);
					if(allBias.contains(str)){ 
						name = str;//尽量取一个子句中较长的名称
						pAtBestFinding = p; //!! 防止p++走过头
					}
					p++;
				}
				if(name != null && !myBias.contains(name) /*&& !inUrl(name, content, p)*/){ //不对词条所属的本装备名称entryName及其别名添加链接。
					nes.add(name);
					i = pAtBestFinding + 1; //!! 再回到这次找到的地方的末尾，防止走过头
				}
				else{
					i = i+1; //!!!
				}
				//System.out.println("   p="+p+" i="+i);
			}
			else{
				i++; //i++; //"俄罗斯苏霍伊苏-57战斗机由于研制进度几度推迟。": 苏霍伊被发现后，i++，就不能发现苏-57战斗机了 --LDQ,2020.3.11
				//System.out.println("i="+i);
			}
		}

		System.out.println("\nfindNEs(): nes="+nes);
		return nes;
	}

	/**
	 * 知识抽取入库：action传来一段文本，从中抽取出知识（实体、关系、属性和事件），然后入知识库保存；
	 * 
	 * @param text 待抽取的文本内容
	 * @return int 是否抽取、入库成功
	 */
	public static int kbExtractAndStore(String text, String title, String infoSource, long cmsId){
		int result = 0; //0: 成功； 1：... 

		// ..........

		return result;
	}

	/**
	 * 知识抽取入库：action传来一个cmsMain对象，从中抽取出知识（实体、关系、属性和事件），然后入知识库保存；
	 * 
	 * @param text 待抽取的文本内容
	 * @return int 是否抽取、入库成功
	 */
	public static int kbExtractAndStore(String text, cmsMain cms){
		int result = 0; //0: 成功； 1：... 

		// ..........

		return result;
	}

	public static String kbSave(String entityName, String weaponType, String infoSource){

		String sourcePrefix = "source";

		//2.第二步，精确查询，获取实体的属性值、关联关系：
		if(DEBUG){
			urlPrefix = "http://192.168.6.15:18804";
			apiUrl = "/api/entity/dg";
		}
		String url = urlPrefix + apiUrl;

		if(DEBUG){
			textPrefix = "{\"text\":\"";
			labelPrefix = "\"label\":\"";
			sourcePrefix = "source";
			textSuffix = "\"}";
		}
		String params = textPrefix + entityName + "\"," + 
				labelPrefix + weaponType + "\"," + 
				sourcePrefix + infoSource + textSuffix;
		JSONObject jObj = postResp(url, params);

		//3.封装返回值：
		String msg = jObj.getString("msg");
		int code = Integer.valueOf(jObj.getString("code"));

		System.out.println("\nmsg:"+msg+"\ncode:"+code+"\n");

		return msg;
	}

	public static JSONObject getResp(String url){
		//if(url.contains(" ")){
		try{
			url = UrlEncoding.encode(url, "utf-8");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		//}

		CloseableHttpClient client = HttpClients.createDefault();
		try{
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Authorization", "Token f44444f9ee90aef7e7c14f8900645ff699a39299");
			CloseableHttpResponse resp = client.execute(httpGet);
			HttpEntity entity = resp.getEntity();
			if (entity!=null){
				String content = EntityUtils.toString(entity, "utf-8");
				//				System.out.println("resp entity="+content);
				JSONObject result = JSONObject.parseObject(content);

				//String msg = result.getString("msg");
				//int code = Integer.valueOf(result.getString("code"));
				//System.out.println("\nmsg:"+msg+"\ncode:"+code+"\n");

				return result;
			}
			resp.close();	

		}catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject postResp(String url, String param){
		CloseableHttpClient client = HttpClients.createDefault();

		try{
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Type", "application/json;charset=\"utf-8\"");
			post.addHeader("Authorization", "Token f44444f9ee90aef7e7c14f8900645ff699a39299");
			StringEntity stringEntity= new StringEntity(param, "utf-8");
			stringEntity.setContentType("application/json");
			post.setEntity(stringEntity);

			CloseableHttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			if (entity!=null){
				String content = EntityUtils.toString(entity, "utf-8");
				//增加报错  --LDQ，2020.1.11 LuoYang
				if(content.equals("<html><title>500: Internal Server Error</title><body>500: Internal Server Error</body></html>")){
					resp.close();
					System.err.println("postResp(): 解析出错，500: Internal Server Error!");
					return null;
				}

				JSONObject result = JSONObject.parseObject(content);
				resp.close();
				return result;
			}

			resp.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static JSONObject putResp(String url, String param){
		CloseableHttpClient client = HttpClients.createDefault();

		try{
			HttpPut put = new HttpPut(url);
			put.addHeader("Content-Type", "application/json;charset=\"utf-8\"");
			put.addHeader("Authorization", "Token f44444f9ee90aef7e7c14f8900645ff699a39299");
			StringEntity stringEntity= new StringEntity(param, "utf-8");
			put.setEntity(stringEntity);
			CloseableHttpResponse resp = client.execute(put);
			HttpEntity entity = resp.getEntity();
			if (entity!=null){
				String content = EntityUtils.toString(entity, "utf-8");
				JSONObject result = JSONObject.parseObject(content);
				resp.close();
				return result;
			}

			resp.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static JSONObject deleteResp(String url, String param){
		CloseableHttpClient client = HttpClients.createDefault();

		try{
			if(param==null){
				HttpDeleteWithBody delete = new HttpDeleteWithBody(url);
				delete.addHeader("Authorization", "Token f44444f9ee90aef7e7c14f8900645ff699a39299");
				CloseableHttpResponse resp = client.execute(delete);
				HttpEntity entity = resp.getEntity();
				if (entity!=null){
					String content = EntityUtils.toString(entity, "utf-8");
					JSONObject result = JSONObject.parseObject(content);
					resp.close();
					return result;
				}
				resp.close();
			}else{
				HttpDeleteWithBody delete = new HttpDeleteWithBody(url);
				delete.addHeader("Content-Type", "application/json;charset=\"utf-8\"");
				delete.addHeader("Authorization", "Token f44444f9ee90aef7e7c14f8900645ff699a39299");
				StringEntity stringEntity= new StringEntity(param, "utf-8");
				delete.setEntity(stringEntity);
				CloseableHttpResponse resp = client.execute(delete);
				HttpEntity entity = resp.getEntity();
				if (entity!=null){
					String content = EntityUtils.toString(entity, "utf-8");
					JSONObject result = JSONObject.parseObject(content);
					resp.close();
					return result;
				}
				resp.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 从知识库中获取所有实体的标准名称及其别名。以及实体类型。
	 * 并写入词典文件/dict/实体及类型及别名映射表.txt里面。
	 * --LDQ，2019.12.12
	 * 
	 * @throws FileNotFoundException 
	 */
	public static void queryAllKBEntities(String normEntity2CategoryPath) throws FileNotFoundException{
		//1、查询所有实体名称、及其类型 ---- 待完善，增加别名。 LDQ，2019.12.12 ---- 已增加

		System.out.println("\n正在从知识库中查询实体的标准名称...");
		for(String entityCategory : LaunchListener.EntityCategories){
			System.out.println("\n"+entityCategory+"...");
			List<String> entities = KBUtil.allEntities(entityCategory);
			System.out.println(entities.size()+"个实体。");//entities.toString());
			for(String entityName : entities){
				LaunchListener.normEntity2Category.put(entityName, entityCategory);
				//pw1.write(entityName+" = " + entityCategory + "\n");
			}
		}
		//pw1.close();
		System.out.println("一共从知识库中查询出"+LaunchListener.normEntity2Category.size()+"个实体。\n实体类型有"+LaunchListener.EntityCategories.size()+"个。\n");

		//2、为每个实体查询其别名、为每个别名查询其标准实体名称：
		//3. 写入词典文件中：
		PrintWriter pw = new PrintWriter(normEntity2CategoryPath);
		System.out.println("\n正在从知识库中查询实体的别名bias...");
		Set<String> allEntityNames = LaunchListener.normEntity2Category.keySet();

		int count = 0;
		for(String normName : allEntityNames){
			if(normName == null) continue;
			String category = LaunchListener.normEntity2Category.get(normName);
			String line = normName + " = " + category + " = ";
			Set<String> bias = KBUtil.getBias(normName, LaunchListener.normEntity2Category.get(normName));
			//System.out.println(normName+": bias="+bias.toString());
			//if(bias.isEmpty()) bias.add(normName);
			//pw2.write(normName+" = " + bias.toString() + "\n");
			Set<String> nonDuplicatedBias = new HashSet<>();
			nonDuplicatedBias.add(normName);//?把自身标准名称也当做一个bias
			for(String bs : bias){
				//名称归一化  //?? ----可配置编程思想
				bs = bs.replace("－", "-").replace("III", "Ⅲ").replace("II", "Ⅱ").replace("（", "(").replace("）", ")").replace("「", "“").replace("」", "”").replace("‘", "“").replace("’", "”");
				nonDuplicatedBias.add(bs);
			}
			for(String bs : nonDuplicatedBias){
				LaunchListener.bias2NormEntity.put(bs, normName);
				line += bs+"$";
				//pw3.write(bs+" = " + normName + "\n");
			}

			LaunchListener.normEntity2Bias.put(normName, nonDuplicatedBias);
			//System.out.println(normName+" bias="+bias.toString());

			pw.write(line + "\n");

			if(DEBUG && count++>100) break;
		}
		pw.close();

		//保存到备份文件里：
		ConfigOp op = new ConfigOp();
		String fileRoot = op.getValueFromConfig("fileRoot");
		String tmppath = fileRoot + "/cmsdata/allEntitiesAndBias";
		if(!(new File(tmppath).exists())){
			FileUtil.mkdirs(tmppath);
		}
		String allEntitiesAndBias = tmppath+"/实体及类型及别名映射表_"+System.currentTimeMillis()+".txt";
		FileUtil.copyFile(new File(normEntity2CategoryPath), new File(allEntitiesAndBias));
		System.out.println("别名bias一共有" + LaunchListener.bias2NormEntity.size()+"个。");
		System.out.println("实体查询结束。\n备份文件在"+tmppath+"\n");

		//生成别名的头部，以提高实体名称在文本中发现的效率
		Set<String> allBias = LaunchListener.bias2NormEntity.keySet();
		for(String bias : allBias){
			if(bias.length() > 1) LaunchListener.allBiasHead.add(bias.substring(0, 2));
		}
	}

	/**
	 * 所有实体和别名的名称最长的长度
	 * --LDQ，2019.12.17
	 * 
	 * @return
	 */
	public static int maxLength(){
		int maxLen = 0;
		for(String name : LaunchListener.bias2NormEntity.keySet()){
			if(name.length() > maxLen){
				maxLen = name.length();
				System.out.println("long name="+name);
			}
		}

		return maxLen;
	}


	/**
	 * 从词典文件中读取所有实体的标准名称及其别名。以及实体类型。
	 * --LDQ，2019.12.18
	 * @throws IOException 
	 */
	public static void readAllKBEntities(String normEntity2CategoryPath) throws IOException{
		//1、读取所有实体名称、及其类型 ---- 待完善，增加别名。 LDQ，2019.12.12 ---- 已增加
		//2、为每个实体读取其别名、为每个别名读取其标准实体名称：
		List<String> normEntity2CategoryList = FileUtils.readLines(new File(normEntity2CategoryPath),"UTF-8");//FileUtil.readFileByLines(normEntity2CategoryPath);
		Set<String> entityCategorySet = new HashSet<>();

		//		System.out.println("\n正在从词典文件中读实体的标准名称...");
		int count = 0;
		for(String line : normEntity2CategoryList){
			if(count == 0) { count++; continue; }//略去第一行

			String[] parts = line.split(" = ");
			if(parts.length < 3) {
				System.err.println("line error: "+line);
				continue;
			}
			String normName = parts[0].trim();
			String entityCategory = parts[1].trim();
			String alias = parts[2].trim();

			//			System.out.println(normName+" = "+entityCategory + " = " + alias);

			LaunchListener.normEntity2Category.put(normName, entityCategory);
			//String[] bias = alias.split("$");
			Set<String> bias = new HashSet<>();
			bias.addAll(Arrays.asList(alias.split("\\$")));//!! split("$")错
			//String strBias = bias.toString();
			//strBias = strBias.substring(1, strBias.length()-1);
			//bias.addAll(Arrays.asList(strBias.split(", ")));
			LaunchListener.normEntity2Bias.put(normName, bias);
			//System.out.println(normName+" bias="+bias.toString());

			for(String bs : bias){
				LaunchListener.bias2NormEntity.put(bs, normName);
				if(bs.length() > 1) LaunchListener.allBiasHead.add(bs.substring(0,2));
			}

			entityCategorySet.add(entityCategory);
		}
		//		LaunchListener.EntityCategories.clear();//??不能clear？
		//		LaunchListener.EntityCategories.addAll(entityCategorySet);//??不能addAll？
		System.out.println("一共从词典文件中读出"+LaunchListener.normEntity2Category.size()+"个实体。\n实体类型有"+LaunchListener.EntityCategories.size()+"个。\n");
		System.out.println("别名bias一共有" + LaunchListener.bias2NormEntity.size()+"个。");
		System.out.println("实体读取结束.\n");
	}

	/**
	 * 命名实体识别(NER)
	 * ----LDQ，2019.12.30
	 * 
	 * @return
	 */
	public static LinkedHashMap<String, String> ner(String text){
		LinkedHashMap<String, String> nes = new LinkedHashMap<>(); //实体识别结果: <名称 : 类型>

		text = text.replace("\n", "。").replace("。。", "。");//按说不用替换的。

		if(DEBUG){ //http://192.168.6.15:30010/ner_cetc32_v2
			ip = "192.168.6.15";
			nerPort = "30012";//"30010";
			nerUrl = "/api/v1/nlp/ner";//"/ner_cetc32_v2";
		}
		String url = "http://" + ip + ":" + nerPort + nerUrl;

		String params = "{\"text\": \""+text+"\"}";
		JSONObject jObj = postResp(url, params);
		String message = "";
		int code = -1;
		if(jObj != null){ 
			message = jObj.getString("message");
			code = Integer.valueOf(jObj.getString("code"));

			if(message.equalsIgnoreCase("ok") || code == 0 || code == 200){
				JSONArray entities = jObj.getJSONArray("entities");
				if(entities != null){
					for(Object entity : entities){
						JSONObject ne = (JSONObject)entity;
						nes.put(ne.getString("word") + "@" + ne.getString("link")/*LDQ,2020.01.11 LY*/, ne.getString("type"));
					}
				}
			}
		}
		else {
			System.err.println("\nmessage:"+message+" code:"+code+"\n");
			System.err.println("解析异常，实体识别时，返回数据中没有包含message、code字段，无法进一步解析。");
			//System.err.println(text+"\n"+jObj.toJSONString());
		}

		return nes;

	}

	public static void main(String[] args) throws FileNotFoundException{
		System.out.println(getSourceIdDict());
		//		deleteProperTest();
		//		editEnittyPropertyTest();
		//		kbEntityQueryTest();
		//		kbEntityQueryTestPro();
		//		deleteEntityTest();
		//ner("CV-22“鱼鹰”是装备美空军和特种作战司令部的倾转旋翼机（注：CV-22“鱼鹰”是美军V-22“鱼鹰”系列型号之一，此外还有装备美海军陆战队的MV-22“鱼鹰”），机上集成有“综合射频对抗套件”（SIRFC）和“定向红外对抗”（DIRCM）系统，可执行渗透、撤出、再补给、直接攻击和战斗搜救等任务。");
		//ner("外媒称，中国首艘055型导弹驱逐舰24日上午离开上海江南造船厂码头");
		//		ner("巨浪-3洲际战略弹道导弹今日试射成功。");

		//		boolean exist = existsURI("http://192.168.6.15:18804/media/weapon/歼-9.jpg");
		//		System.out.println("exist="+exist);

		//		testUrlRepalce(5579L);

		//		addEntityTest();
		//JSONObject obj = getResp("http://192.168.6.15:18804/entity?q=F-22猛禽战斗机&t=aircraft");
		//System.out.println(obj);

		//		int year = Integer.valueOf((new Date()).toLocaleString().substring(0, 4));
		//		System.out.println("year="+year);

		//		String text = "ABCD";
		//		String result = testStr(text);
		//		System.out.println(result);
		//		System.out.println(text);


		//allEntities("aircraft");

		//getBias("C-5运输机", "aircraft");

		//		queryAllKBEntities("e:\\allEntities.txt");

		//saveTest();
		//		queryTest();

		//		String question = "99式主战坦克高度";//"M16步枪的射程是多少米？";//"米格-29什么时候首飞的？"; 
		//kbEntityQueryTest("F-22猛禽战斗机");
		//		kbqaTest(question);


		//relaTest();

		//		attrTest();
		//		relaToParamsTest();
		//		attrToParamsTest();
		//eventExtractTest();

		//deleteProperTest();


		//System.out.println(Long.toHexString(34120001l));

		//attrTest();
		//kbFusionTest();
		//editEnittyPropertyTest();
		//uploadSourceTest();
		//fromNametoIdTest();
		//relaToParamsTest();
		//putRespTest();
		//System.out.println(Long.valueOf("0x1f0ad54".substring(2),16));
		//System.out.println(getSourceDict());
		//kbEntityQueryTest();

		//attrToParamsTest();
		//		segwordTest(); 
		//allEntityTest();
		//System.out.println(kbInputHintsQuery("MGM-").toString()); // <--[MGM-1导弹, MGM-1导弹的制造国家, MGM-1导弹的指导系统, MGM-1导弹的作战范围, MGM-134侏儒洲际弹道导弹]
		//System.out.println(kbCandidatesQuery("猎鹰5号").toString());//[猎鹰9号运载火箭, 猎鹰5号运载火箭, 飞鹰号航空母舰, 远望5号, 猎鹰1号运载火箭, 猎鹰60N面空导弹, 达索猎鹰7X, 海鹰二号, 凯沃尔号, 海鹰三号, 鹰击六号, ‘太湖’号, 远征5号, 鹰潭号（531）, 海鹰四号, 海鹰一号, 鹰击一号, 火星5号, 水手5号, 金星5号]

		//System.out.println((new Date()).toLocaleString());
	}

	private static void deleteProperTest(){

		JSONObject testProper = new JSONObject();
		testProper.put("id", "2732539");
		testProper.put("name", "测试属性名5");
		testProper.put("value", "测试属性值5");
		deleteEnittyProperty(testProper);

	}


	private static void allEntityTest() {
		//		Set<String> allEntities = new HashSet<>();
		//		List<String> entityCategory = new ArrayList<>();
		//		
		//		//读取实体的种类
		//		entityCategory.add("aircraft");entityCategory.add("guns");entityCategory.add("tank");
		//		entityCategory.add("spaceship");entityCategory.add("missile");entityCategory.add("warship");
		//		entityCategory.add("explosive");entityCategory.add("artillery");entityCategory.add("org");
		//		entityCategory.add("force");
		//		
		//		for(String category:entityCategory){
		//			allEntities.addAll(allEntities(category));
		//		}
		//		int k = 0;
		//		for (Iterator<String> iterator = allEntities.iterator(); iterator.hasNext();) {
		//			String str = iterator.next();
		//			k++;
		//			System.out.println(str);
		//		}
		//		System.out.println(k);
	}

	private static void editEnittyPropertyTest(){

		JSONObject newProperty = new JSONObject();
		newProperty.put("id", "2732539");
		newProperty.put("name", "测试属性名5");
		newProperty.put("value", "测试属性值5");
		editEnittyProperty(newProperty);	
	}

	private static void segwordTest() {
		String text = "他叫汤姆去拿外套";
		List<Term32s> segWordResult = segmentWords(text);
		for(Term32s term:segWordResult){
			String word = term.getName();
			String pos = term.getPos();
			System.out.println("word:" + word + "pos:" + pos);
		}
	}

	private static void eventExtractTest() {
		String text = "外媒称，俄罗斯国防部新闻部门宣布俄军在加里宁格勒地区部署了一套S-400“凯旋”防空导弹系统";
		//		String text="";
		List<cmsEvent> event = eventExtract(text, null);
		//		System.out.println(eventToParams(event));
	}
	private static void uploadSourceTest(){

		JSONObject sourceObj = new JSONObject();

		sourceObj.put("name", "中华军事网");
		sourceObj.put("host", "https://3g.china.com");
		sourceObj.put("score", 0.7);

		uploadSource(sourceObj);




	}

	private static void putRespTest(){
		String url = "http://192.168.6.15:18804/api/proper";

		String param = "{\"id\":\"0x271ea61\",\"property\":\"制造国家\",\"source\":\"维基百科\",\"value\":\"苏联5\"}";

		System.out.println(putResp(url, param));

	}

	private static void kbEntityQueryTest(){

		String entity = "M";

		System.out.println(kbEntityQuery(entity));
		System.out.println(kbEntityQuery(entity).getSimilarEntityNames());


	}

	private static void kbEntityQueryTestPro(){
		JSONObject query  = new JSONObject();

		query.put("name", "测试枪支24");
		query.put("label", "guns");

		System.out.println(kbEntityQueryPro(query).getSimilarEntityNames());
	}

	private static void kbEntityQueryTest(String entity){
		System.out.println(kbEntityQuery(entity).getSimilarEntityNames());
	}

	//	private static void fromNametoIdTest(){
	//		
	//		String text = "前卫级";
	//		
	//		System.out.println(fromNametoId(text));
	//		+
	//		
	//	}

	private static void kbFusionTest(){

		JSONObject artObject = new JSONObject();

		//artObject.put("text", "前卫级是英国新一代核动力弹道导弹潜艇，是英国4艘“前卫”级战略核潜艇之一，1986年开始建造，长149.9米，宽12.8米，最大潜水深度为350米，其排水量为15000吨；长149.9米，宽12.8米，吃水12米；水下航速25节。");
		artObject.put("text","山东舰正式入列服役。山东号航空母舰于2019年12月17日到达海南省三亚市开始服役。自今日起，中国进入了双航母时代，中国海军朝着深蓝海军建设又迈出了重要一步。");
		artObject.put("articleId", "1");
		artObject.put("source", "中华军事网");

		JSONArray fusion = kbFusion(artObject);
		System.out.println(fusion);	
	}

	private static void relaTest(){

		//String text = "报道称，美国海军新闻处发布消息说，12月1日“艾森豪威尔”号航母前往弗吉尼亚州的诺福克海军基地，维修甲板。这艘航母穿过苏伊士运河，进入地中海东部海域，并在这一海域遭遇强风暴。原计划，“艾森豪威尔”号应当在2013年年初返回基地，届时尼米兹号航母应当与其换岗。但尼米兹号的起飞甲板必须维修以及动力装置维修延误，所以“艾森豪威尔”号不得不提前1个月离开波斯湾。艾森豪威尔号上载有70架战斗机和轰炸机，船员和海军陆战队人数多达8000人。";
		String text = "";
		Object rela = relaExtract(text);

		System.out.println(rela);

	}

	private static void addEntityTest(){

		JSONObject addEntity = new JSONObject();
		addEntity.put("name","测试枪支25");
		addEntity.put("label", "guns");
		addEntity.put("source", 1);
		System.out.println(addEntity(addEntity));


	}

	//	private static void deleteEntityTest(){
	//		
	//		JSONObject entity = new JSONObject();
	//		entity.put("name","测试枪支25");
	//		entity.put("label", "guns");
	//		deleteEnitty(entity);
	//	}
	//	

	private static void attrTest(){
		String text = "辽宁号航空母舰，长30米。";

		//String text = "前卫级是英国新一代核动力弹道导弹潜艇，是英国4艘“前卫”级战略核潜艇之一，1986年开始建造，长149.9米，宽12.8米，最大潜水深度为350米，其排水量为15000吨；长149.9米，宽12.8米，吃水12米；水下航速25节。";
		Object attr = attrExtract(text);

		System.out.println(attr);

	}


	private static void queryTest(){
		String keyword = "F-35闪电II战斗机";//"MGM-29飞弹";//"99式主战坦克有多高？";//"猎鹰9号";//"猎鹰5号";// "猎鹰9号运载火箭";//"MGM-29飞弹";//"MGM-134侏儒洲际弹道导弹";//"M1917左轮手枪";//"测试手枪";//"MGMv";//"盖世太保手枪";//"MGM-29飞弹";//"MGMv";
		KBEntity entity = kbEntityQuery(keyword);

		System.out.println("entity="+entity);
	}

	private static void saveTest(){
		String keyword = "测试手枪";//"MGMv";//"盖世太保手枪";//"MGM-29飞弹";//"MGMv";
		kbSave(keyword, "guns", "新浪军事");
	}

	private static void kbqaTest(){
		String question = "99式主战坦克有多高？";//"M16步枪的射程是多少米？";//"米格-29什么时候首飞的？"; 
		Map<String, Object> result = kbQA(question);

		String answer  = result.get("answer").toString();
		String msg = result.get("msg").toString();
		int code = Integer.valueOf(result.get("code").toString());

		System.out.println("\nquestion="+question+"\nanswer="+answer+"\nmsg="+msg+"\ncode="+code);
	}

	private static void kbqaTest(String question){
		String bestQuesiotn = getBestQuestion(question);

		System.out.println("bestQuestion="+bestQuesiotn);

		if(!bestQuesiotn.isEmpty()){
			Map<String, Object> result = kbQA(question);

			String answer  = result.get("answer").toString();
			String msg = result.get("msg").toString();
			int code = Integer.valueOf(result.get("code").toString());

			System.out.println("\nquestion="+question+"\nanswer="+answer+"\nmsg="+msg+"\ncode="+code);
		}
	}

	private static void testTika(){
		org.apache.tika.Tika tika = new org.apache.tika.Tika();
		try {
			String content = tika.parseToString(new File("C:\\Users\\user.user-PC\\Desktop\\军事测试语料\\世界各国战斗机介绍.docx"));//"D:\\201912\\ProtegeOWLTutorialP4_v1_1.pdf"));
			System.out.println(content);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testUrlRepalce(long cmsId){
		String content = "\"<img class=\"img-lazy\" src=\"/htmlApp/0437a149-c7a0-4673-a387-f137365d0a14/0\" ";

		content = content.replaceAll("/htmlApp/([0-9a-z\\-]+)/", "/ZHZKGL/Accessory/"+cmsId+"/");

		System.out.println(content);		
	}
}




