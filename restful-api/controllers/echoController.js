const express = require('express');
const Redis = require('../init/initRedis');

exports.postUsersTodos = async (req, res) => {
    const redis = await Redis.getConnection();

    const newTODOs = req.body;

    const testTODOs = [
        {
            userId: "123",
            field: "Buy magazine",
            value: `Title: Buy magazine, Priority: HIGH, Places: STORE, Time: GENERAL, Assignee: , isDone: false`
        },
        {
            userId: "123",
            field: "Buy battery",
            value: `Title: Buy battery, Priority: MEDIUM, Places: STORE, Time: GENERAL, Assignee: , isDone: false`
        }
    ]

    for (const todo of testTODOs) {
        const exists = await redis.exists(todo.userId);
        if (exists === 1) {

            console.log(`${todo.userId} already exists in the database!`);
            const insert = await redis.hSet(todo.userId, todo.field, todo.value);

            if (insert === 1) {
                console.log(`Successfully created new TODOs for ${todo.userId}.`);
            }

        } else {

            console.log(`Creating new TODOs for ${todo.userId}...`);
            const insert = await redis.hSet(todo.userId, todo.field, todo.value);

            if (insert === 1) {
                console.log(`Successfully created new TODOs for ${todo.userId}.`);
            }

        }
        console.log(await redis.hGetAll(todo.userId));
    }
    res.status(200).send(`Successfully created new TODOs.`);
}

exports.getCompletedTodos = async (req, res) => {
    const redis = await Redis.getConnection();

    //const completedTODOs = redis.get('completed');

    const completedTODOs = ["task1", "task2", "task3"];

    res.status(200).send(completedTODOs);
}