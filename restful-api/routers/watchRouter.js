const express = require('express');
const {getUserTasks, deleteUserTasks} = require('../controllers/watchController');

const watchRouter = express.Router();

watchRouter.delete('/:user', deleteUserTasks);

watchRouter.get('/:user', getUserTasks);

module.exports = watchRouter;