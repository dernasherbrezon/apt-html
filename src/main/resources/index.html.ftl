<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="r2cloud APT repository">
    <meta name="author" content="Andrey Rodionov">
    <link rel="icon" href="favicon.ico">

    <title>r2cloud APT repository</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

  </head>

  <body class="bg-light">

    <div class="container">
      <div class="py-5 text-center">
        <img class="d-block mx-auto mb-4" src="apple-icon-72x72.png" alt="" width="72" height="72">
        <h2>r2cloud repository</h2>
        <p class="lead">This page shows contents of r2cloud APT repository. Please join <a href="https://gitter.im/r2cloud/Lobby">our chat</a> to discuss the project.</p>
      </div>

      <div class="row">
        <div class="col-md-12">
          <table class="table table-hover">
          	<thead>
          		<tr>
          			<td></td>
          			<td>stretch armhf</td>
          			<td>stretch arm64</td>
          			<td>bionic amd64</td>
          			<td>focal amd64</td>
          		</tr>
          	</thead>
          	<tbody>
          		<tr>
          			<td>r2cloud</td>
          			<td class="table-success">202112345</td>
          			<td class="table-success">202112345</td>
          			<td class="table-success">202112345</td>
          			<td class="table-success">202112345</td>
          		</tr>
          		<tr>
          			<td>librtlsdr</td>
          			<td class="table-success">0.6.4</td>
          			<td class="table-success">0.6.4</td>
          			<td class="table-warning">0.6.0</td>
          			<td class="table-warning">0.6.0</td>
          		</tr>
          		<tr>
          			<td>plutosdr</td>
          			<td class="table-danger"></td>
          			<td class="table-danger"></td>
          			<td class="table-success">1.0.1</td>
          			<td class="table-success">1.0.1</td>
          		</tr>
          	</tbody>
          </table>
        </div>
      </div>

      <div class="py-5 text-center">
        <h3>How to use</h3>
        <p class="lead">Just run the following commands:</p>
      </div>
      <div class="py-5">
      	<pre><code>sudo apt-get install dirmngr lsb-release
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys A5A70917
sudo bash -c "echo \"deb http://s3.amazonaws.com/r2cloud $(lsb_release --codename --short) main\" > /etc/apt/sources.list.d/r2cloud.list"
sudo apt-get update</code></pre>
      </div>

      <footer class="my-5 pt-5 text-muted text-center text-small">
        <p class="mb-1">Made by <a href="https://dernasherbrezon.com">Andrey</a> using <a href="https://github.com/dernasherbrezon/apt-html">apt-html</a></p>
      </footer>
    </div>

    <script src="js/bootstrap.min.js"></script>
  </body>
</html>

