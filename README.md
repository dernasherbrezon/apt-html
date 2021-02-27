## apt-html

Render nice html page showing APT repository packages and versions.

## How to use

1. Edit the template at ```src/main/resources/index.html.ftl```.

2. Build:

```
mvn package
```

3. Run:

```
java -jar ./target/apt-html.jar --help
```

## Configuration

<table>
  <thead>
    <tr>
      <th>
        Parameter
      </th>
      <th>
        Description
      </th>
    </tr>
  </thead>
  <tbody>
	<tr>
    <td>--url</td>
    <td>Url of APT repository. For example, http://s3.amazonaws.com/r2cloud</td>
    </tr>
    <tr>
    <td>--include-arch</td>
    <td>Comma separated list of archs to include into the search. Example: amd64,armhf</td>
    </tr>
    <tr>
    <td>--include-component</td>
    <td>Comma separated list of components to include into the search. Example: stable,unstable,main</td>
    </tr>
	<tr>
    <td>--include-codename</td>
    <td>Comma separated list of codenames to include into the search. Example: stretch,bionic</td>
    </tr>
	  <tr>
    <td>--include-package</td>
    <td>Comma separated list of packages to include into the search. Example: sdr-server,r2cloud</td>
    </tr>
	  <tr>
    <td>--output-dir</td>
    <td>Output directory for the generated web site. For example, target/generated</td>
    </tr>
    <tr>
    <td>--timeout</td>
    <td>Timeout while querying APT repository in milliseconds. Example: 10000</td>
    </tr>
  </tbody>
</table>



