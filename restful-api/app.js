const {getAblyClient, getRedisClient} = require('./init/init');
const express = require('express');
const config = require('./config.json');

const app = express();
const ably_realtime = getAblyClient();
const redis = getRedisClient();

//TODO: Implement Get function
app.get('/', (req, res) => {
    res.send('Hello World');  
});

//TODO: Implement Post function
app.post('/submit-item', (req, res) => {
  res.send('TODO item submitted');
});

app.delete('/:device', (req, res, next) => {

}, (req, res) => {

})

const port = 3000;

app.listen(port, () => {
  console.log(`Server listening on port ${port}`);  
});