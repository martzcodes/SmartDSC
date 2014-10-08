var express = require('express');
var app = express();
var nap = require('./nodealarmproxy.js');
var config = require('./config.js');
var https = require('https');

var alarm = nap.initConfig({ password:config.password,
	serverpassword:config.serverpassword,
	actualhost:config.host,
	actualport:config.port,
	serverhost:'0.0.0.0',
	serverport:config.port,
	zone:7,
	partition:1,
	proxyenable:true
});

app.get('/', function(req, res){
	console.log('req');
  res.send('hello world');
});

app.get('/status', function(req, res){
	nap.getCurrent(function(currentstate){
  		var jsonString = JSON.stringify(currentstate);
	
		var pathURL = '/api/smartapps/installations/'+config.app_id+'/panel/fullupdate?access_token='+config.access_token;

		httpsRequest(pathURL,jsonString);
	});
	res.send('json sent');
});

app.get('/json', function(req, res){
  nap.getCurrent(function(currentstate){
  	var jsonString = JSON.stringify(currentstate);
	
	var pathURL = '/api/smartapps/installations/'+config.app_id+'/panel/fullupdate?access_token='+config.access_token;

	httpsRequest(pathURL,jsonString);
  });
  res.send('json sent');
});

app.post('/jsoncommand', function(req,res){
	//console.log('request:',req);
	//console.log('request body (json?): ',req.body);
	console.log('request headers: ',req.headers);
	var reqObj = JSON.parse(req.headers.message);
	console.log('reqObj',reqObj);
	if (reqObj.password == config.STpass) {
		if (reqObj.command =='arm') {
			nap.manualCommand('0331'+config.alarm_pin,function(){
	  		console.log('armed armed armed armed');
	  		res.send('arming');
	  	});
		}
		if (reqObj.command == 'disarm') {
			nap.manualCommand('0401'+config.alarm_pin,function(){
		  		res.send('disarmed');
		  	});
		}
		if (reqObj.command == 'nightarm') {
			nap.manualCommand('0711*9'+config.alarm_pin,function(){
				res.send('nightarm');
		  	});
		}
	}
});

alarm.on('zone', function(data) {
	if (config.watchevents.indexOf(data.code) != -1) {
		var jsonString = JSON.stringify(data);

		var pathURL = '/api/smartapps/installations/'+config.app_id+'/panel/zoneupdate?access_token='+config.access_token;

		httpsRequest(pathURL,jsonString);
	}
});

alarm.on('partition', function(data) {
	if (config.watchevents.indexOf(data.code) != -1) {
		var jsonString = JSON.stringify(data);

		var pathURL = '/api/smartapps/installations/'+config.app_id+'/panel/partitionupdate?access_token='+config.access_token;

		httpsRequest(pathURL,jsonString);
	}
});

function httpsRequest (pathURL, jsonString) {
	var headers = {
		'Content-Type': 'application/json',
		'Content-Length': Buffer.byteLength(jsonString)
	};

	var options = {
		host: 'graph.api.smartthings.com',
		port: 443,
		path: pathURL,
		method: 'POST',
		headers: headers
	};

	var req = https.request(options, function(res) {
		console.log("statusCode: ", res.statusCode);
		console.log("headers: ", res.headers);

		res.on('data', function(d) {
			console.log(d);
		});
	});
	req.on('error', function(e) {
		console.log("Got error: " + e.message);
	});
	req.write(jsonString);
	req.end();
}

app.listen(8086,'0.0.0.0');