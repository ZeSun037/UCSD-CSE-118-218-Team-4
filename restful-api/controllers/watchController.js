var Redis = require("../init/initRedis");

exports.getUserTasks = async (req, res) => {
    const redis = await Redis.getConnection();

    const user = req.params.user;

    const exists = await redis.exists(user);

    if (exists) {

        const tasks = await redis.get(user);
        res.status(200).send(tasks);

    } else {

        return res.status(404).send("The user does not exist!");

    }
}

exports.deleteUserTasks = async (req, res) => {

    const redis = await Redis.getConnection();

    const user = req.params.user;
    const tasks = req.body.tasks;

    const reply = await redis.exists(user);

    if (reply != 1) {

        return res.status(404).send("The user does not exist!");

    } else {

        for (task of tasks) {

            const exists = await redis.sIsMember(user, task);

            if (exists) {

                await redis.sRem(user, task);

                console.log(`${task} completed for ${user}.`);

                await redis.sAdd("completed", task);

            } else {

                console.log(`${task} does not exist in ${user}'s TODO list.`);

            }
        }

        res.status(200).send("Your tasks are registered to be completed.");
    }
}