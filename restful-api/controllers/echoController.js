const Redis = require('../init/initRedis');
const TODO = require('../init/initTODO');
const {EntityId} = require('redis-om');

exports.postUsersTodos = async (req, res) => {
    await Redis.getConnection();
    const todoRepo = Redis.todoRepository;

    const newTODOs = req.body;

    // Add function to call speech-to-text function
    // to generate TODOs here
    // Format of a batch with one user:
    // [TODO1, TODO2, TODO3, ...]
    // Below is sample code

    // for (const todo of newTODOs) {
    //     await todoRepo.save(todo);
    // }

    // Test code
    const testTODOs = [
                {
                    assignee: 'John Doe',
                    place: 1,
                    time: 1633036800,
                    priority: 1,
                    title: 'Complete project report',
                    isDone: false
                },
                {
                    assignee: 'John Doe',
                    place: 2,
                    time: 1633123200,
                    priority: 2,
                    title: 'Prepare for meeting',
                    isDone: false
                },{
                    assignee: 'Mike Doe',
                    place: 1,
                    time: 1633036900,
                    priority: 1,
                    title: 'Do the QA',
                    isDone: true
                },
                {
                    assignee: 'Mike Doe',
                    place: 2,
                    time: 1633123400,
                    priority: 2,
                    title: 'Write the Doc',
                    isDone: true
                }
    ];
    for (const todo of testTODOs) {
        // const exists = await redis.exists(batch.id);
        // if (exists === 1) {
        //     console.log(`${batch.id} already exists in the database!`);
        //     const insert = await redis.sAdd(batch.id, batch.todos);
        //     if (insert === 1) {
        //         console.log(`Successfully created new TODOs for ${batch.id}.`);
        //     }
        // } else {
        //     console.log(`Creating new TODOs for ${batch.id}...`);
        //     const insert = await redis.sAdd(batch.id, batch.todos);
        //     if (insert === 1) {
        //         console.log(`Successfully created new TODOs for ${batch.id}.`);
        //     }
        // }
        // console.log(await redis.sMembers(batch.id));
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
    // const completedTODOs = [{
    //     assignee: 'Mike Doe',
    //     place: 1,
    //     time: 1633036900,
    //     priority: 1,
    //     title: 'Do the QA',
    //     isDone: true
    // },
    // {
    //     assignee: 'Mike Doe',
    //     place: 2,
    //     time: 1633123400,
    //     priority: 2,
    //     title: 'Write the Doc',
    //     isDone: true
    // }];

    // res.status(200).send(completedTODOs);
    res.status(200).send(completed);
}