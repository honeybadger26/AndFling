const express = require('express');

const HOST = '0.0.0.0';
const PORT = 4000;

const app = express();

require('./routes/check')(app);
require('./routes/keys')(app);

app.listen(PORT, HOST);
console.log(`Running at http://localhost:${PORT}`);