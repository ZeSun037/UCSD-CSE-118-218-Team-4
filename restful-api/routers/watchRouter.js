const express = require('express');
const {redisClient, redisConnect, ablyClient} = require('../init/init');

const redis = redisClient();
const watchRouter = express.Router();
const ably = ablyClient();

watchRouter.delete('/:user', async (req, res, next) => {
    const user = req.params.user;
    const tasks = req.body.tasks;
    redis.exists(user, async (err, rep) => {
        if (reply !== 1) {

        } else {
            res.locals.completed = [];
            res.locals.user = user;
            tasks.forEach(async element => {
                const exists = await redis.sIsMember(user, element);
                if (exists) {
                    await redis.sRem(user, element);
                    res.locals.completed.push(element);
                }
            });
        }
    })
    next();
}, async (req, res) => {
    await ably.connection.once("connected");

    console.log("Ready to broadcast completion.");

    const channel = ably.channels.get("echo-todos");

    await channel.publish(`${res.locals.user} completed following tasks`, res.locals.completed);

    await ably.connection.close();

    return res.send("TODOs completed and notified!");
});

module.exports = watchRouter;