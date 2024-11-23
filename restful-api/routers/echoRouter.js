const express = require('express');
const {postUsersTodos, getCompletedTodos} = require('../controllers/echoController');

const echoRouter = express.Router();

echoRouter.post('/new', postUsersTodos);

echoRouter.get('/completed', getCompletedTodos);

module.exports = echoRouter;