const express = require('express');

const echoRouter = express();

// Posting TODOs from echo
echoRouter.post('/', (req, res, next) => {

})

module.exports = echoRouter;