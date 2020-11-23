module.exports = app => {
    app.get('/check', function(req, res, next) {
        console.log('Status check request received');
        res.status(200).send("Success");
    });
}