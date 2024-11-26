const express = require('express');
const {ablyClient} = require('../init/initAbly');
const Redis = require('../init/initRedis');
const ably = ablyClient();

const echoRouter = express.Router();

// Posting TODOs from echo
echoRouter.post('/new', async (req, res, next) => {
    const redis = await Redis.getConnection();

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
    for (const batch of testTODOs) {
        const exists = await redis.exists(batch.assignee);
        if (exists === 1) {
            console.log(`${batch.assignee} already exists in the database!`);
            const insert = await redis.sAdd(batch.assignee, batch.todos);
            if (insert === 1) {
                console.log(`Successfully created new TODOs for ${batch.assignee}.`);
            }
        } else {
            console.log(`Creating new TODOs for ${batch.assignee}...`);
            const insert = await redis.sAdd(batch.assignee, batch.todos);
            if (insert === 1) {
                console.log(`Successfully created new TODOs for ${batch.assignee}.`);
            }
        }
        console.log(await redis.sMembers(batch.assignee));
    }

    // await redis.disconnect();
    // console.log("Transaction Done. Disconnecting from Redis.");
    
    next();
}, async (req, res) => {

    console.log("Broadcasting the TODOs.");
    console.log("Preparing Ably connection.");

    ably.connection.once("connected", () => {
        console.log("Connected to Ably!")
      });

    console.log("Ably Connection Established. Ready to publish todos.\n");

    res.locals.todos.forEach(async todo => {
        const channel = ably.channels.get(`todo-dispatcher-${todo.assignee}`);
        await channel.publish("Your Tasks!", todo);
    })

    //ably.connection.close();

    return res.send("TODO batch processing done.");
})

module.exports = echoRouter;