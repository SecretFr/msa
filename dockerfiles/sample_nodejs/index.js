const express = require('express');

const app = express();

app.get('/', (req, res) => {
    res.send('Welcome to the NODE page.');
});

app.listen(8080, () => {
    console.log('Listening on Port 8080');
});