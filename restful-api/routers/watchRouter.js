const express = require('express');
const {ablyClient} = require('../init/initAbly');
const Redis = require('../init/initRedis');
const {getUserTasks, deleteUserTasks} = require('../controllers/watchController');

const ably = ablyClient();
const watchRouter = express.Router();

watchRouter.delete(':/user', deleteUserTasks);

watchRouter.get('/:user', getUserTasks);

// watchRouter.delete('/:user', async (req, res, next) => {
//     const redis = await Redis.getConnection();
    
//     console.log(req.params);
//     const user = req.params.user;
//     const tasks = req.body.tasks;
//     const reply = await redis.exists(user);
//     if (reply !== 1) {
//         return res.status(404).send("The user does not exist!");
//     } else {
//         res.locals.completed = [];
//         res.locals.user = user;
//         for (element of tasks) {
//             const exists = await redis.sIsMember(user, element);
//             if (exists) {
//                 await redis.sRem(user, element);
//                 console.log(`${element} completed for ${user}.`);
//                 res.locals.completed.push(element);
//             } else {
//                 console.log(`${element} does not exist in ${user} TODO list.`);
//             }
//         }
//     }

//     // await redis.disconnect();
//     // console.log("Transaction Done. Disconnecting from Redis.")
//     next();
// }, async (req, res) => {
//     await ably.connection.once("connected", () => {
//         console.log("Successfully connected to ably!");
//     });

//     console.log("Ready to broadcast completion.");

//     const channel = ably.channels.get("echo-todos");

//     await channel.publish(`${res.locals.user} completed following tasks`, res.locals.completed);

//     //await ably.connection.close();

//     return res.send("TODOs completed and notified!");
// });

module.exports = watchRouter;