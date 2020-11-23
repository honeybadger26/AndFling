var robot = require('robotjs');

module.exports = app => {
    app.get('/key/play', function(req, res, next) {
        robot.keyTap('audio_play');
        console.log('Play/Pause key pressed');
        res.sendStatus(200);
    });

    app.get('/key/space', function(req, res, next) {
        robot.keyTap('space');
        console.log('Space key pressed');
        res.sendStatus(200);
    });

    app.get('/key/left', function(req, res, next) {
        robot.keyTap('left');
        console.log('Left key pressed');
        res.sendStatus(200);
    });

    app.get('/key/right', function(req, res, next) {
        robot.keyTap('right');
        console.log('Right key pressed');
        res.sendStatus(200);
    });

    app.get('/action/toggle_media', function(req, res, next){
        robot.keyTap('space');
        robot.keyToggle('alt', 'down');
        robot.keyTap('tab');
        robot.keyToggle('alt', 'up');
        robot.keyTap('space');
        console.log('Media hidden/restored');
        res.sendStatus(200);
    });
}