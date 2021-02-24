package ru.r2cloud.apt.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import ru.r2cloud.apt.html.model.Architecture;
import ru.r2cloud.apt.html.model.Codename;
import ru.r2cloud.apt.html.model.Column;
import ru.r2cloud.apt.html.model.CommandLineArgs;
import ru.r2cloud.apt.html.model.ControlFile;
import ru.r2cloud.apt.html.model.PackageDetails;
import ru.r2cloud.apt.html.model.PackageVersion;
import ru.r2cloud.apt.html.model.Packages;

public class Main {

	private static final String USER_AGENT = "aptHtml/1.0";

	public static void main(String[] argv) throws Exception {
		CommandLineArgs args = new CommandLineArgs();
		JCommander parser = JCommander.newBuilder().addObject(args).build();
		try {
			parser.parse(argv);
		} catch (ParameterException e) {
			System.out.println(e.getMessage());
			parser.usage();
			System.exit(-1);
		}
		if (args.isHelp()) {
			parser.usage();
			return;
		}

		File outputDir = new File(args.getOutputDir());
		if (!outputDir.exists() && !outputDir.mkdirs()) {
			throw new Exception("unable to create output dir: " + outputDir.getAbsolutePath());
		}

		RequestConfig config = RequestConfig.custom().setConnectTimeout(args.getTimeout()).setConnectionRequestTimeout(args.getTimeout()).build();
		CloseableHttpClient httpclient = HttpClientBuilder.create().setUserAgent(USER_AGENT).setDefaultRequestConfig(config).build();

		//FIXME handle arch=all. I.e. it should be included into any other arch
		Set<Column> uniqueColumns = new HashSet<>();
		Map<String, PackageDetails> details = new HashMap<>();

		for (Codename codename : convertCodename(args.getIncludeCodename())) {
			for (String component : args.getIncludeComponent()) {
				for (Architecture arch : convert(args.getIncludeArch())) {
					String path = "dists/" + codename + "/" + component + "/binary-" + arch + "/Packages";
					Packages curPackages = load(httpclient, args.getUrl() + "/" + path + ".gz");
					if (curPackages == null) {
						curPackages = load(httpclient, args.getUrl() + "/" + path);
					}
					if (curPackages == null) {
						continue;
					}

					Column curColumn = new Column(codename, arch);
					uniqueColumns.add(curColumn);

					for (ControlFile cur : curPackages.getContents().values()) {
						PackageDetails curDetails = details.get(cur.getPackageName());
						if (curDetails == null) {
							curDetails = new PackageDetails();
							curDetails.setName(cur.getPackageName());
							curDetails.setHomepage(cur.getHomepage());
							details.put(cur.getPackageName(), curDetails);
						}
						curDetails.getVersions().put(curColumn, new PackageVersion(null, cur.getVersion()));
					}
				}
			}
		}

		List<Column> columnsSorted = new ArrayList<>(uniqueColumns);
		Collections.sort(columnsSorted, ColumnComparator.INSTANCE);
		List<PackageDetails> sortedPackages = new ArrayList<>(details.values());
		Collections.sort(sortedPackages, PackageDetailsComparator.INSTANCE);
		for (PackageDetails cur : sortedPackages) {
			PackageVersion maxVersion = findMax(cur.getVersions().values());
			List<PackageVersion> mappedToColumns = new ArrayList<>(columnsSorted.size());
			for (int i = 0; i < columnsSorted.size(); i++) {
				PackageVersion curVersion = cur.getVersions().get(columnsSorted.get(i));
				// version not found
				if (curVersion == null || maxVersion == null) {
					curVersion = new PackageVersion("danger", "");
				} else {
					if (PackageVersionComparator.INSTANCE.compare(curVersion, maxVersion) < 0) {
						curVersion.setColor("warning");
					} else {
						curVersion.setColor("success");
					}
				}
				mappedToColumns.add(curVersion);
			}
			cur.setMappedToColumns(mappedToColumns);
		}

		Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_0);
		freemarkerConfig.setDefaultEncoding("UTF-8");
		freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		freemarkerConfig.setClassForTemplateLoading(Main.class, "/");
		freemarkerConfig.setTimeZone(TimeZone.getTimeZone("GMT"));

		Template fTemplate = freemarkerConfig.getTemplate("index.html.ftl");
		Map<String, Object> data = new HashMap<>();
		data.put("columns", columnsSorted);
		data.put("rows", sortedPackages);
		try (Writer w = new BufferedWriter(new FileWriter(new File(outputDir, "index.html")))) {
			fTemplate.process(data, w);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static PackageVersion findMax(Collection<PackageVersion> versions) {
		PackageVersion result = null;
		for (PackageVersion cur : versions) {
			if (result == null) {
				result = cur;
				continue;
			}
			if (PackageVersionComparator.INSTANCE.compare(result, cur) < 0) {
				result = cur;
			}
		}
		return result;

	}

	private static List<Codename> convertCodename(List<String> includeCodename) throws Exception {
		List<Codename> result = new ArrayList<>(includeCodename.size());
		for (String cur : includeCodename) {
			result.add(Codename.valueOf(cur.trim()));
		}
		return result;
	}

	private static List<Architecture> convert(List<String> includeArch) throws Exception {
		List<Architecture> result = new ArrayList<>(includeArch.size());
		for (String cur : includeArch) {
			result.add(Architecture.valueOf(cur.trim()));
		}
		return result;
	}

	private static Packages load(CloseableHttpClient httpclient, String url) {
		HttpGet method = new HttpGet(url);
		org.apache.http.HttpResponse response = null;
		try {
			response = httpclient.execute(method);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				return null;
			}
			Packages result = new Packages();
			if (url.endsWith(".gz")) {
				result.load(new GZIPInputStream(response.getEntity().getContent()));
			} else {
				result.load(response.getEntity().getContent());
			}
			return result;
		} catch (Exception e) {
			return null;
		} finally {
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
			}
		}
	}
}
