const Redis = require("../init/initRedis");
const {EntityId} = require("redis-om");
const TODO = require("../init/initTODO");

exports.getUserTasks = async (req, res) => {
    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;

    const user = req.params.user;
    console.log(user);

    // const exists = await redis.exists(user);

    // if (exists) {

    //     const tasks = await redis.get(user);
    //     res.status(200).send(tasks);

    // } else {

    //     return res.status(404).send("The user does not exist!");

    // }

    const tasks = await todoRepo.search().where('assignee').equals(user).return.all();

    if (tasks.length > 0) {

        res.status(200).send(tasks);

    } else {

        res.status(404).send("The user does not exist!");

    };
}

exports.deleteUserTasks = async (req, res) => {

    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;
    
    const user = req.params.user;
    console.log(user);
    //const tasks = req.body.tasks;

    const todos = await todoRepo.search().where('assignee').equals(user).return.all();
    for (let todo of todos) {
        const id = todo[EntityId];
        await todoRepo.remove(id);
    }

    res.status(200).send("Tasks deleted successfully.");
    // const reply = await redis.exists(user);

    // if (reply != 1) {

    //     return res.status(404).send("The user does not exist!");

    // } else {

    //     for (task of tasks) {

    //         const exists = await redis.sIsMember(user, task);

    //         if (exists) {

    //             await redis.sRem(user, task);

    //             console.log(`${task} completed for ${user}.`);

    //             await redis.sAdd("completed", task);

    //         } else {

    //             console.log(`${task} does not exist in ${user}'s TODO list.`);

    //         }
    //     }

    //     res.status(200).send("Your tasks are registered to be completed.");
    // }
}