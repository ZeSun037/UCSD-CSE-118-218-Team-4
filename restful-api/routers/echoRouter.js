const express = require('express');
const {getAblyClient, getRedisClient} = require('../init/init');

const redis = getRedisClient();

const echoRouter = express();

// Posting TODOs from echo
echoRouter.post('/', (req, res, next) => {
    
})

module.exports = echoRouter;