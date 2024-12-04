const Redis = require("../init/initRedis");
const { EntityId } = require("redis-om");
const TODO = require("../init/initTODO");

exports.getUserTasks = async (req, res) => {
    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;

    const user = req.params.user;
    console.log(user);

    const tasks = await todoRepo.search().where('assignee').equals(user).return.all();

    if (tasks.length > 0) {

        res.status(200).send(tasks);

    } else {

        res.status(404).send("The user does not exist!");

    };
}

exports.completeUserTasks = async (req, res) => { 
    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;

    const data = req.body;

    const todo = await todoRepo.search().where('title').equals(data.title).and('assignee').equals(data.assignee).return.first();
    if (todo !== null) {
        todo.isDone = true;

        todoRepo.save(todo);

        res.status(200).send("Task completed successfully.");
    } else {
        res.status(404).send("Task not found.");
    }
    
}

exports.deleteUserTasks = async (req, res) => {

    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;
    
    const data = req.body;

    const todo = await todoRepo.search().where('title').equals(data.title).and('assignee').equals(data.assignee).return.first();

    if (todo !== null) {
        const id = todo[EntityId];

        await todoRepo.remove(id);

        res.status(200).send("Task deleted successfully.");
        
    } else {
        res.status(404).send("Task not found.");
    }
}