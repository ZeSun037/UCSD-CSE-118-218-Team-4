const express = require('express');
const Redis = require('../init/initRedis');

exports.postUsersTodos = async (req, res) => {
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
    res.status(200).send(`Successfully created new TODOs.`);
}

exports.getCompletedTodos = async (req, res) => {
    const redis = await Redis.getConnection();

    //const completedTODOs = redis.get('completed');

    const completedTODOs = ["task1", "task2", "task3"];

    res.status(200).send(completedTODOs);
}