const express = require('express');
const {redisClient, redisConnect, ablyClient} = require('../init/init');

const redis = redisClient();
const ably = ablyClient();

const echoRouter = express.Router();

// Posting TODOs from echo
echoRouter.post('/new', async (req, res, next) => {
    const newTODOs = req.body;

    // Add function to call speech-to-text function
    // to generate TODOs here
    // Format of a batch with one user:
    // [{user: usrname, tasks: ["task1", "task2", "task3",...]}]

    const testTODOs = [
        {
            assignee: 'testwatch',
            todos: ['task1', 'task2']
        }
    ]
    res.locals.todos = testTODOs;
    testTODOs.forEach(async (batch) => {
        await redis.exists(batch.assignee, async (err, reply) => {
            if (reply === 1) { // The assignee is registered

            } else {
                // Possible way of creating encryption?
                await redis.sAdd(batch.assignee, []);
                batch.todos.forEach(async todo => {
                    await redis.sAdd(batch.assignee, todo);
                })
            }
        })
    });
    next();
}, async (req, res) => {
    await ably.connection.once("connected");

    console.log("Ably Connection Established. Ready to publish todos.");

    res.locals.todos.forEach(async todo => {
        const channel = ably.channels.get(`todo-dispatcher-${todo.assignee}`);
        await channel.publish("Your Tasks!", todo);
    })

    await ably.connection.close();

    return res.send("TODO batch processing done.");
})

module.exports = echoRouter;