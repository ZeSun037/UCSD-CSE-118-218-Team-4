const Redis = require('../init/initRedis');
const TODO = require('../init/initTODO');
const {EntityId} = require('redis-om');

exports.postUsersTodos = async (req, res) => {
    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;

    const newTODOs = req.body;

    const testTODOs = [
                {
                    assignee: 'John Doe',
                    place: "Work",
                    time: 1633036800,
                    priority: 1,
                    title: 'Complete project report',
                    isDone: false
                },
                {
                    assignee: 'John Doe',
                    place: "Work",
                    time: 1633123200,
                    priority: 2,
                    title: 'Prepare for meeting',
                    isDone: false
                },{
                    assignee: 'Mike Doe',
                    place: "Work",
                    time: 1633036900,
                    priority: 1,
                    title: 'Do the QA',
                    isDone: true
                },
                {
                    assignee: 'Mike Doe',
                    place: "Work",
                    time: 1633123400,
                    priority: 2,
                    title: 'Write the Doc',
                    isDone: true
                }
    ];
    for (const todo of testTODOs) {
        let exists = await todoRepo.search().where('title').equals(todo.title).and('assignee').equals(todo.assignee).count();
        if (exists === 0) {
            await todoRepo.save(todo);
        } else {
            res.status(400).send(`TODO with title ${todo.title} already exists for ${todo.assignee}.`);
        }
    }
    res.status(200).send(`Created new TODOs for assignees.`);
}

exports.getCompletedTodos = async (req, res) => {
    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;

    //const completedTODOs = redis.get('completed');
    let completed = await todoRepo.search().where('isDone').equals(true).return.all();
    res.status(200).send(completed);
}