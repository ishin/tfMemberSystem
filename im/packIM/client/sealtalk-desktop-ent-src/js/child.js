var screenshot = require('screenshot');
if(!screenshot){
	process.disconnect();
	return;
}
process.on('message', function(m) {
		screenshot.screencapture((data) => {
		//var buffer = new Buffer(data)
		process.send('over');
		process.disconnect();
	});
});
