const {redisClient, redisConnect, ablyClient} = require('./init/init');
const express = require('express');
const echoRouter = require('./routers/echoRouter')
const watchRouter = require('./routers/watchRouter')
const config = require('./config.json');

const app = express();
const redis = redisClient();

redisConnect(redis);

//TODO: Implement Get function
app.get('/', (req, res) => {
  res.send('Hello World');  
});

app.use('/echo', echoRouter);

app.use('/watch', watchRouter);

const port = 3000;

app.listen(port, () => {
  console.log(`Server listening on port ${port}`);  
});