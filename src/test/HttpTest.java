package test;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.protobuf.TextFormat.ParseException;

public class HttpTest {
	public static void main(String[] args) {
		String url = "http://192.168.6.15:30000/api/v1/nlp/cws";
		CloseableHttpClient client = HttpClients.createDefault();
		
		try{
			HttpPost post = new HttpPost(url);
			StringEntity stringEntity = new StringEntity("{\"text\":\"Ëû½ÐÌÀÄ·È¥ÄÃÍâÌ×\"}","utf-8");
			post.addHeader("Content-Type","application/json;charset=\"utf-8\"");
			post.setEntity(stringEntity);
			CloseableHttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			if(entity != null){
				String content = EntityUtils.toString(entity);
				Gson gson = new Gson();
				Type listType = (Type) new TypeToken<Map<String, Object>>() {
				}.getType();
				Map<String, Object> result = gson.fromJson(content, (java.lang.reflect.Type) listType);
				String IP = result.get("test").toString();
			}
			resp.close();
		}catch (ClientProtocolException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}