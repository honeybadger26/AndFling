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

    app.get('/key/prevtrack', function(req, res, next) {
        robot.keyTap('audio_prev');
        console.log('Previous Track key pressed');
        res.sendStatus(200);
    });

    app.get('/key/nexttrack', function(req, res, next) {
        robot.keyTap('audio_next');
        console.log('Next Track key pressed');
        res.sendStatus(200);
    });

    app.get('/key/voldown', function(req, res, next) {
        robot.keyTap('audio_vol_down');
        console.log('Volume Down key pressed');
        res.sendStatus(200);
    });
    
    app.get('/key/volup', function(req, res, next) {
        robot.keyTap('audio_vol_up');
        console.log('Volume Up key pressed');
        res.sendStatus(200);
    });
    
    app.get('/key/mute', function(req, res, next) {
        robot.keyTap('audio_mute');
        console.log('Mute key pressed');
        res.sendStatus(200);
    });

    // TODO: remove
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