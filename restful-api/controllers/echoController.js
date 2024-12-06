const express = require('express');
const crypto = require('crypto');
const Redis = require('../init/initRedis');

exports.postUsersTodos = async (req, res) => {
    const redis = await Redis.getConnection();

    const todo = req.body;
    const { assignee, task, place, time, priority } = todo;
    console.log("assignee: ",assignee, ", task: ", task, ", place: ", place, ", time: ", time);

    // Remember to replace these with functions
    const todoItem = {
        userId: "123", // Replace with this: todoItem.userId = crypto.createHash('sha256').update(assignee).digest('hex');
        field: `${task}`,
        value: `Title: ${task}, Priority: ${priority}, Place: ${place}, Time: ${time}, Assignee: ${assignee}, isDone: false`
    }

    // const testTODOs = [
    //     {
    //         userId: "123",
    //         field: "Buy magazine",
    //         value: `Title: Buy magazine, Priority: HIGH, Places: STORE, Time: GENERAL, Assignee: , isDone: false`
    //     },
    //     {
    //         userId: "123",
    //         field: "Buy battery",
    //         value: `Title: Buy battery, Priority: MEDIUM, Places: STORE, Time: GENERAL, Assignee: , isDone: false`
    //     }
    // ]

    todoItem.userId = crypto.createHash('sha256').update(assignee).digest('hex');

    const exists = await redis.exists(todoItem.userId);
    if (exists === 1) {

        console.log(`${todoItem.userId} already exists in the database!`);
        const insert = await redis.hSet(todoItem.userId, todoItem.field, todoItem.value);

        if (insert === 1) {
            console.log(`Successfully created new TODOs for ${todoItem.userId}.`);
        }

    } else {

        console.log(`Creating new TODOs for ${todoItem.userId}...`);
        const insert = await redis.hSet(todoItem.userId, todoItem.field, todoItem.value);

        if (insert === 1) {
            console.log(`Successfully created new TODOs for ${todoItem.userId}.`);
        }

    }
    console.log(await redis.hGetAll(todoItem.userId));
    res.status(200).send(`Successfully created new TODOs.`);
}

// Function no longer needed
exports.getCompletedTodos = async (req, res) => {
    const redis = await Redis.getConnection();

    //const completedTODOs = redis.get('completed');

    const completedTODOs = ["task1", "task2", "task3"];

    res.status(200).send(completedTODOs);
}
