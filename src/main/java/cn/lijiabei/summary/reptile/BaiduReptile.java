package cn.lijiabei.summary.reptile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class BaiduReptile {

	public static void main(String[] args) {
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://www.baidu.com/link?url=yNzGZ68SshJl-Cy7gDQFqHZTtTxVwjqkhEo6HgN9Rfg7XvE2OFWzo5EKn9C_Ptfs&wd=&eqid=94b810300011eade0000000456c527c8");
		try {
			HttpResponse response = client.execute(get);
			InputStream in = response.getEntity().getContent();
			System.out.println(" code:" + response.getStatusLine().getStatusCode());

			// 获取请求返回的内容
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			StringBuffer content = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			System.out.println(content.toString());
		} catch (Exception e) {
		}
	}
}
