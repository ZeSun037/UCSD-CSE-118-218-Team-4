/**
 * @file watchRouter.js
 * @description Router for handling watch-related operations for users.
 * @module routers/watchRouter
 */

const express = require('express');
const { getUserTasks, deleteUserTasks } = require('../controllers/watchController');

const watchRouter = express.Router();

/**
 * Route to delete tasks for a specific user.
 * @name delete/:user
 * @function
 * @memberof module:routers/watchRouter
 * @param {string} user - The user identifier.
 * @param {function} deleteUserTasks - Controller function to handle task deletion.
 */
watchRouter.delete('/:user', deleteUserTasks);

/**
 * Route to get tasks for a specific user.
 * @name get/:user
 * @function
 * @memberof module:routers/watchRouter
 * @param {string} user - The user identifier.
 * @param {function} getUserTasks - Controller function to handle fetching tasks.
 */
watchRouter.get('/:user', getUserTasks);

module.exports = watchRouter;