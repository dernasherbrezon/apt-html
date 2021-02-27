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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static final String USER_AGENT = "aptHtml/1.0";

	public static void main(String[] argv) throws Exception {
		CommandLineArgs args = new CommandLineArgs();
		JCommander parser = JCommander.newBuilder().addObject(args).build();
		try {
			parser.parse(argv);
		} catch (ParameterException e) {
			LOG.error(e.getMessage());
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

		Set<Column> uniqueColumns = new HashSet<>();
		Map<String, PackageDetails> details = new HashMap<>();
		Set<String> indexedPackageInclude = new HashSet<>(args.getIncludePackage());

		for (Codename codename : convertCodename(args.getIncludeCodename())) {
			for (String component : args.getIncludeComponent()) {
				for (Architecture arch : convert(args.getIncludeArch())) {
					Packages curPackages = loadByArch(httpclient, arch, codename, component, args);
					if (curPackages == null) {
						continue;
					}
					Column curColumn = new Column(codename, arch);

					boolean atleastOneAdded = false;
					for (ControlFile cur : curPackages.getContents().values()) {
						if (!indexedPackageInclude.contains(cur.getPackageName())) {
							continue;
						}
						// ignore wildcard archs. they will be separately processed below
						// arch=all can be uploaded to all and several other archs
						if (cur.getArch().isWildcard()) {
							continue;
						}
						PackageDetails curDetails = details.get(cur.getPackageName());
						if (curDetails == null) {
							curDetails = new PackageDetails();
							curDetails.setName(cur.getPackageName());
							curDetails.setHomepage(cur.getHomepage());
							details.put(cur.getPackageName(), curDetails);
						}
						curDetails.getVersions().put(curColumn, new PackageVersion(null, cur.getVersion()));
						atleastOneAdded = true;
					}

					if (atleastOneAdded) {
						uniqueColumns.add(curColumn);
					}
				}
			}
		}

		Map<Codename, List<Architecture>> grouped = groupByCodename(uniqueColumns);
		for (Entry<Codename, List<Architecture>> curEntry : grouped.entrySet()) {
			for (String component : args.getIncludeComponent()) {
				Packages curPackages = loadByArch(httpclient, Architecture.all, curEntry.getKey(), component, args);
				if (curPackages == null) {
					continue;
				}
				for (ControlFile cur : curPackages.getContents().values()) {
					if (!indexedPackageInclude.contains(cur.getPackageName())) {
						continue;
					}
					PackageDetails curDetails = details.get(cur.getPackageName());
					if (curDetails == null) {
						curDetails = new PackageDetails();
						curDetails.setName(cur.getPackageName());
						curDetails.setHomepage(cur.getHomepage());
						details.put(cur.getPackageName(), curDetails);
					}
					for (Architecture curArch : curEntry.getValue()) {
						curDetails.getVersions().put(new Column(curEntry.getKey(), curArch), new PackageVersion(null, cur.getVersion()));
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
		data.put("url", args.getUrl());
		try (Writer w = new BufferedWriter(new FileWriter(new File(outputDir, "index.html")))) {
			fTemplate.process(data, w);
		} catch (Exception e) {
			LOG.error("unable to process data", e);
			System.exit(-1);
		}
	}

	private static Map<Codename, List<Architecture>> groupByCodename(Set<Column> uniqueColumns) {
		Map<Codename, List<Architecture>> result = new HashMap<>();
		for (Column cur : uniqueColumns) {
			List<Architecture> archs = result.get(cur.getCodename());
			if (archs == null) {
				archs = new ArrayList<>();
				result.put(cur.getCodename(), archs);
			}
			archs.add(cur.getArch());
		}
		return result;
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

	private static Packages loadByArch(CloseableHttpClient httpclient, Architecture arch, Codename codename, String component, CommandLineArgs args) {
		String path = "dists/" + codename + "/" + component + "/binary-" + arch + "/Packages";
		Packages curPackages = load(httpclient, args.getUrl() + "/" + path + ".gz");
		if (curPackages == null) {
			curPackages = load(httpclient, args.getUrl() + "/" + path);
		}
		return curPackages;
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
